package com.kilombo.crm.application.service;

import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.domain.exception.ValidationException;
import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.model.Pedido;
import com.kilombo.crm.domain.repository.ClienteRepository;
import com.kilombo.crm.domain.repository.DetallePedidoRepository;
import com.kilombo.crm.domain.repository.PedidoRepository;
import com.kilombo.crm.domain.service.WhatsAppService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del servicio de WhatsApp para seguimiento de pedidos.
 * Maneja la carga de plantillas, sustitución de placeholders y generación de URLs.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger logger = Logger.getLogger(WhatsAppServiceImpl.class.getName());
    private static final String MESSAGES_FILE = "/messages.properties";
    private static final String WHATSAPP_BASE_URL = "https://wa.me/";

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    // Caché de plantillas cargadas una sola vez
    private final Map<String, String> messageTemplates = new HashMap<>();

    public WhatsAppServiceImpl(PedidoRepository pedidoRepository,
                              ClienteRepository clienteRepository,
                              DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        loadMessageTemplates();
    }

    /**
     * Carga las plantillas de mensajes desde el archivo de recursos.
     * Se ejecuta una sola vez al inicializar el servicio.
     */
    private void loadMessageTemplates() {
        try (InputStream inputStream = getClass().getResourceAsStream(MESSAGES_FILE)) {
            if (inputStream == null) {
                throw new IOException("Archivo de mensajes no encontrado: " + MESSAGES_FILE);
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            // Cargar todas las plantillas
            for (String key : properties.stringPropertyNames()) {
                messageTemplates.put(key, properties.getProperty(key));
            }

            logger.info("Plantillas de mensajes cargadas exitosamente: " + messageTemplates.size() + " plantillas");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar plantillas de mensajes: " + e.getMessage(), e);
            throw new RuntimeException("Error al inicializar WhatsAppService: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateWhatsAppUrl(Integer idPedido) {
        logger.info("=== INICIANDO GENERACIÓN DE URL WHATSAPP ===");
        logger.info("ID de pedido solicitado: " + idPedido);

        if (idPedido == null || idPedido <= 0) {
            logger.warning("ID de pedido inválido: " + idPedido);
            throw new ValidationException("ID de pedido inválido");
        }

        // Obtener pedido
        logger.info("Buscando pedido con ID: " + idPedido);
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (!pedidoOpt.isPresent()) {
            logger.warning("Pedido no encontrado con ID: " + idPedido);
            throw new ValidationException("Pedido no encontrado con ID: " + idPedido);
        }
        Pedido pedido = pedidoOpt.get();
        logger.info("Pedido encontrado - Estado: " + (pedido.getEstado() != null ? pedido.getEstado() : "SIN ESTADO"));

        // Si el pedido no tiene estado, buscar el último pedido del cliente
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            logger.info("Pedido sin estado, buscando último pedido del cliente ID: " + pedido.getIdCliente());
            try {
                List<Pedido> pedidosCliente = pedidoRepository.findByClienteId(pedido.getIdCliente());
                if (!pedidosCliente.isEmpty()) {
                    // Ordenar por fecha descendente y tomar el primero (más reciente)
                    pedidosCliente.sort((p1, p2) -> p2.getFecha().compareTo(p1.getFecha()));
                    Pedido ultimoPedido = pedidosCliente.get(0);
                    logger.info("Usando último pedido del cliente - ID: " + ultimoPedido.getId() +
                              ", Estado: " + (ultimoPedido.getEstado() != null ? ultimoPedido.getEstado() : "SIN ESTADO"));
                    pedido = ultimoPedido;
                } else {
                    logger.warning("No se encontraron pedidos para el cliente ID: " + pedido.getIdCliente());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error al buscar último pedido del cliente: " + e.getMessage(), e);
            }
        }

        // Obtener cliente
        logger.info("Buscando cliente con ID: " + pedido.getIdCliente());
        Optional<Cliente> clienteOpt = clienteRepository.findById(pedido.getIdCliente());
        if (!clienteOpt.isPresent()) {
            logger.warning("Cliente no encontrado para el pedido ID: " + idPedido);
            throw new ValidationException("Cliente no encontrado para el pedido ID: " + idPedido);
        }
        Cliente cliente = clienteOpt.get();
        logger.info("Cliente encontrado: " + cliente.getNombre() + " " + cliente.getApellido() +
                  ", Email: " + cliente.getEmail() + ", Teléfono: " + cliente.getTelefono());

        // Validar teléfono
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            logger.warning("Teléfono no encontrado o inválido para cliente: " + cliente.getNombre() + " " + cliente.getApellido());
            throw new ValidationException("Teléfono no encontrado o inválido para este cliente");
        }

        // Obtener producto principal
        logger.info("Buscando producto principal para pedido ID: " + pedido.getId());
        Optional<String> productoPrincipalOpt = detallePedidoRepository.findPrincipalProductByPedidoId(pedido.getId());
        String productoPrincipal = productoPrincipalOpt.orElse("producto");
        logger.info("Producto principal encontrado: " + productoPrincipal);

        // Determinar estado para la plantilla
        String estadoParaPlantilla = (pedido.getEstado() != null && !pedido.getEstado().trim().isEmpty())
                ? pedido.getEstado().toLowerCase()
                : "pending";
        logger.info("Estado para plantilla: " + estadoParaPlantilla);

        // Obtener plantilla según estado
        String templateKey = "template." + estadoParaPlantilla + ".body";
        logger.info("Buscando plantilla con clave: " + templateKey);
        String template = messageTemplates.get(templateKey);
        if (template == null) {
            logger.warning("Plantilla no encontrada para estado: " + estadoParaPlantilla + ", usando plantilla por defecto");
            template = messageTemplates.get("template.pending.body");
            if (template == null) {
                logger.severe("No se encontraron plantillas de mensaje válidas");
                throw new ValidationException("No se encontraron plantillas de mensaje válidas");
            }
        }
        logger.info("Plantilla encontrada y cargada correctamente");

        // Sustituir placeholders
        logger.info("Realizando sustitución de placeholders en la plantilla");
        String mensaje = template
                .replace("{NOMBRE_CLIENTE}", cliente.getNombre() + " " + cliente.getApellido())
                .replace("{PRODUCTO_PRINCIPAL}", productoPrincipal)
                .replace("{TELEFONO}", cliente.getTelefono())
                .replace("{ESTADO_PEDIDO}", estadoParaPlantilla);

        logger.info("Mensaje después de sustitución: " + mensaje);

        // Codificar mensaje para URL
        try {
            logger.info("Codificando mensaje para URL");
            String mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8.toString());
            logger.info("Mensaje codificado correctamente");

            // Construir URL completa
            String telefonoLimpio = cliente.getTelefono().replaceAll("[^\\d]", "");
            logger.info("Teléfono limpio para URL: " + telefonoLimpio);
            String whatsappUrl = WHATSAPP_BASE_URL + telefonoLimpio + "?text=" + mensajeCodificado;

            logger.info("=== URL DE WHATSAPP GENERADA EXITOSAMENTE ===");
            logger.info("URL final: " + whatsappUrl);
            return whatsappUrl;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al codificar mensaje para URL: " + e.getMessage(), e);
            throw new ValidationException("Error al generar URL de WhatsApp: " + e.getMessage());
        }
    }
}