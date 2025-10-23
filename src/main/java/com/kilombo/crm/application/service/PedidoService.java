package com.kilombo.crm.application.service;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.application.dto.PedidoDTO;
import com.kilombo.crm.domain.exception.ClienteNotFoundException;
import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.domain.exception.PedidoNotFoundException;
import com.kilombo.crm.domain.exception.ValidationException;
import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.model.Pedido;
import com.kilombo.crm.domain.repository.ClienteRepository;
import com.kilombo.crm.domain.repository.PedidoRepository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestionar pedidos.
 * Implementa los casos de uso relacionados con pedidos.
 * Coordina entre la capa de presentación y el dominio.
 * Incluye validaciones robustas y manejo de errores.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class PedidoService {

    private static final Logger logger = Logger.getLogger(PedidoService.class.getName());

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    
    /**
     * Constructor con inyección de dependencias.
     * 
     * @param pedidoRepository Repositorio de pedidos
     * @param clienteRepository Repositorio de clientes
     */
    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Crea un nuevo pedido en el sistema.
     *
     * @param dto DTO con los datos del pedido
     * @return DTO del pedido creado con su ID generado
     * @throws ValidationException si los datos no son válidos
     * @throws ClienteNotFoundException si el cliente no existe
     * @throws DatabaseException si ocurre un error de base de datos
     */
    public PedidoDTO crearPedido(PedidoDTO dto) {
        if (dto == null) {
            throw new ValidationException("Los datos del pedido no pueden ser null");
        }

        try {
            logger.info("Creando nuevo pedido para cliente ID: " + dto.getIdCliente());

            // Validar que el cliente existe
            Optional<Cliente> cliente = clienteRepository.findById(dto.getIdCliente());
            if (!cliente.isPresent()) {
                logger.warning("Intento de crear pedido para cliente inexistente ID: " + dto.getIdCliente());
                throw new ClienteNotFoundException(dto.getIdCliente());
            }

            // Convertir DTO a entidad (esto valida los datos)
            Pedido pedido = dto.toEntity();

            // Guardar en el repositorio
            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            // Crear DTO de respuesta con nombre del cliente
            PedidoDTO resultado = PedidoDTO.fromEntity(pedidoGuardado);
            resultado.setNombreCliente(cliente.get().getNombreCompleto());

            logger.info("Pedido creado exitosamente con ID: " + pedidoGuardado.getId());
            return resultado;

        } catch (ClienteNotFoundException | ValidationException e) {
            logger.log(Level.WARNING, "Error de validación al crear pedido: " + e.getMessage(), e);
            throw e;
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al crear pedido: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al crear pedido: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al crear el pedido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene un pedido por su ID.
     * 
     * @param id ID del pedido
     * @return DTO del pedido
     * @throws PedidoNotFoundException si el pedido no existe
     */
    public PedidoDTO obtenerPedido(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        
        PedidoDTO dto = PedidoDTO.fromEntity(pedido);
        
        // Agregar nombre del cliente
        clienteRepository.findById(pedido.getIdCliente())
                .ifPresent(c -> dto.setNombreCliente(c.getNombreCompleto()));
        
        return dto;
    }
    
    /**
     * Obtiene todos los pedidos del sistema.
     *
     * @return Lista de DTOs de pedidos
     * @throws DatabaseException si ocurre un error al acceder a la base de datos
     */
    public List<PedidoDTO> listarPedidos() {
        try {
            logger.info("Obteniendo lista de todos los pedidos");

            List<Pedido> pedidos = pedidoRepository.findAll();

            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedido -> {
                        PedidoDTO dto = PedidoDTO.fromEntity(pedido);
                        // Agregar nombre del cliente
                        try {
                            clienteRepository.findById(pedido.getIdCliente())
                                    .ifPresent(c -> dto.setNombreCliente(c.getNombreCompleto()));
                        } catch (Exception e) {
                            logger.warning("Error al obtener nombre del cliente ID " + pedido.getIdCliente() + ": " + e.getMessage());
                            dto.setNombreCliente("Cliente desconocido");
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            logger.info("Se obtuvieron " + dtos.size() + " pedidos");
            return dtos;

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al listar pedidos: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al listar pedidos: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener la lista de pedidos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene todos los pedidos de un cliente específico.
     *
     * @param idCliente ID del cliente
     * @return Lista de DTOs de pedidos del cliente
     * @throws ClienteNotFoundException si el cliente no existe
     * @throws DatabaseException si ocurre un error de base de datos
     */
    public List<PedidoDTO> obtenerPedidosPorCliente(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new ValidationException("El ID del cliente debe ser un número positivo");
        }

        try {
            logger.info("Obteniendo pedidos para cliente ID: " + idCliente);

            // Verificar que el cliente existe
            Cliente cliente = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new ClienteNotFoundException(idCliente));

            List<Pedido> pedidos = pedidoRepository.findByClienteId(idCliente);

            String nombreCliente = cliente.getNombreCompleto();

            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedido -> {
                        PedidoDTO dto = PedidoDTO.fromEntity(pedido);
                        dto.setNombreCliente(nombreCliente);
                        return dto;
                    })
                    .collect(Collectors.toList());

            logger.info("Se encontraron " + dtos.size() + " pedidos para cliente " + nombreCliente);
            return dtos;

        } catch (ClienteNotFoundException e) {
            logger.log(Level.WARNING, "Cliente no encontrado ID: " + idCliente, e);
            throw e;
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al obtener pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener pedidos del cliente: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza los datos de un pedido existente.
     * 
     * @param dto DTO con los datos actualizados del pedido
     * @throws PedidoNotFoundException si el pedido no existe
     * @throws ClienteNotFoundException si el cliente no existe
     * @throws ValidationException si los datos no son válidos
     */
    public void actualizarPedido(PedidoDTO dto) {
        if (dto.getId() == null) {
            throw new ValidationException("El ID del pedido es obligatorio para actualizar");
        }
        
        // Verificar que el pedido existe
        if (!pedidoRepository.findById(dto.getId()).isPresent()) {
            throw new PedidoNotFoundException(dto.getId());
        }
        
        // Validar que el cliente existe
        if (!clienteRepository.findById(dto.getIdCliente()).isPresent()) {
            throw new ClienteNotFoundException(dto.getIdCliente());
        }
        
        // Convertir DTO a entidad (esto valida los datos)
        Pedido pedido = dto.toEntity();
        
        // Actualizar en el repositorio
        pedidoRepository.update(pedido);
    }
    
    /**
     * Elimina un pedido del sistema.
     * 
     * @param id ID del pedido a eliminar
     * @throws PedidoNotFoundException si el pedido no existe
     */
    public void eliminarPedido(Integer id) {
        pedidoRepository.deleteById(id);
    }
    
    /**
     * Obtiene el número de pedidos de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Número de pedidos
     */
    public int contarPedidosPorCliente(Integer idCliente) {
        return pedidoRepository.countByClienteId(idCliente);
    }
    
    /**
     * Calcula el total gastado por un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Total gastado
     */
    public double calcularTotalGastadoPorCliente(Integer idCliente) {
        return pedidoRepository.sumTotalByClienteId(idCliente);
    }
    
    /**
     * Obtiene el número total de pedidos.
     *
     * @return Número de pedidos
     * @throws DatabaseException si ocurre un error al acceder a la base de datos
     */
    public int contarPedidos() {
        try {
            logger.fine("Contando total de pedidos");

            int count = pedidoRepository.findAll().size();

            logger.fine("Total de pedidos: " + count);
            return count;

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al contar pedidos: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al contar pedidos: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al contar pedidos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene estadísticas de un cliente (número de pedidos y total gastado).
     *
     * @param idCliente ID del cliente
     * @return Array con [numPedidos, totalGastado]
     * @throws DatabaseException si ocurre un error al acceder a la base de datos
     */
    public double[] obtenerEstadisticasCliente(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new ValidationException("El ID del cliente debe ser un número positivo");
        }

        try {
            logger.fine("Obteniendo estadísticas para cliente ID: " + idCliente);

            int numPedidos = contarPedidosPorCliente(idCliente);
            double totalGastado = calcularTotalGastadoPorCliente(idCliente);

            logger.fine("Estadísticas cliente ID " + idCliente + ": pedidos=" + numPedidos + ", total=" + totalGastado);
            return new double[]{numPedidos, totalGastado};

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al obtener estadísticas del cliente " + idCliente + ": " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener estadísticas del cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener estadísticas del cliente: " + e.getMessage(), e);
        }
    }
}