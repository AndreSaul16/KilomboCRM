package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.application.dto.PedidoDTO;
import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.application.service.EmailServiceImpl;
import com.kilombo.crm.application.service.PedidoService;
import com.kilombo.crm.application.service.WhatsAppServiceImpl;
import com.kilombo.crm.domain.exception.ValidationException;
import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.model.Pedido;
import com.kilombo.crm.domain.repository.ClienteRepository;
import com.kilombo.crm.domain.repository.DetallePedidoRepository;
import com.kilombo.crm.domain.repository.PedidoRepository;
import com.kilombo.crm.presentation.dialog.MessageConfirmationDialog;
import com.kilombo.crm.presentation.dialog.PedidoDialog;
import com.kilombo.crm.presentation.table.PedidoTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel principal para la gestión de pedidos.
 * Contiene una tabla con los pedidos y botones para las operaciones CRUD.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(PedidoPanel.class.getName());

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final WhatsAppServiceImpl whatsAppService;
    private final EmailServiceImpl emailService;
    private JTable table;
    private PedidoTableModel tableModel;
    private JButton btnAnadir;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnMensajeSeguimiento;
    private JComboBox<FiltroClienteItem> cmbFiltroCliente;
    private JLabel lblTotal;
    private JLabel lblTotalImporte;
    
    private Integer clienteFiltroId = null;
    
    /**
     * Constructor del panel.
     *
     * @param pedidoService Servicio de pedidos
     * @param clienteService Servicio de clientes
     * @param pedidoRepository Repositorio de pedidos para WhatsApp
     * @param clienteRepository Repositorio de clientes para WhatsApp
     * @param detallePedidoRepository Repositorio de detalles para WhatsApp
     */
    public PedidoPanel(PedidoService pedidoService, ClienteService clienteService,
                      PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
                      DetallePedidoRepository detallePedidoRepository) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.whatsAppService = new WhatsAppServiceImpl(pedidoRepository, clienteRepository, detallePedidoRepository);
        this.emailService = new EmailServiceImpl(pedidoRepository, clienteRepository, detallePedidoRepository);
        initComponents();
        cargarPedidos();
    }
    
    /**
     * Inicializa los componentes del panel.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtro
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("Gestión de Pedidos");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        // Panel de filtro
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFiltro.add(new JLabel("Filtrar por cliente:"));
        
        cmbFiltroCliente = new JComboBox<>();
        cmbFiltroCliente.addActionListener(e -> aplicarFiltro());
        panelFiltro.add(cmbFiltroCliente);
        
        panelSuperior.add(panelFiltro, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de pedidos
        tableModel = new PedidoTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Configurar renderers para centrar texto
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Configurar anchos de columnas y renderers
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Cliente
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Fecha
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones y estadísticas
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        btnAnadir = new JButton("Añadir");
        btnAnadir.setFont(new Font("Arial", Font.PLAIN, 12));
        btnAnadir.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnAnadir.addActionListener(e -> anadirPedido());

        btnModificar = new JButton("Modificar");
        btnModificar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnModificar.addActionListener(e -> modificarPedido());
        btnModificar.setEnabled(false);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnEliminar.addActionListener(e -> eliminarPedido());
        btnEliminar.setEnabled(false);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnActualizar.addActionListener(e -> cargarPedidos());

        btnMensajeSeguimiento = new JButton("📱 Enviar Mensajes");
        btnMensajeSeguimiento.setFont(new Font("Arial", Font.PLAIN, 12));
        btnMensajeSeguimiento.addActionListener(e -> enviarMensajes());
        btnMensajeSeguimiento.setEnabled(false);

        panelBotones.add(btnAnadir);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnMensajeSeguimiento);
        
        panelInferior.add(panelBotones, BorderLayout.WEST);
        
        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: 0 pedidos");
        lblTotalImporte = new JLabel("Importe total: 0.00 €");
        panelEstadisticas.add(lblTotal);
        panelEstadisticas.add(new JLabel(" | "));
        panelEstadisticas.add(lblTotalImporte);
        
        panelInferior.add(panelEstadisticas, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean haySeleccion = table.getSelectedRow() != -1;
            btnModificar.setEnabled(haySeleccion);
            btnEliminar.setEnabled(haySeleccion);

            // Habilitar botón de seguimiento solo si hay selección y no está CANCELADO
            if (haySeleccion) {
                PedidoDTO pedidoSeleccionado = tableModel.getPedidoAt(table.getSelectedRow());
                btnMensajeSeguimiento.setEnabled(!"CANCELADO".equals(pedidoSeleccionado.getEstado()));
            } else {
                btnMensajeSeguimiento.setEnabled(false);
            }
        });
        
        // Doble click para modificar
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    modificarPedido();
                }
            }
        });
        
        // Cargar clientes en el filtro
        cargarFiltroClientes();
    }
    
    /**
     * Carga los clientes en el combo box de filtro.
     */
    private void cargarFiltroClientes() {
        cmbFiltroCliente.removeAllItems();
        cmbFiltroCliente.addItem(new FiltroClienteItem(null, "Todos los clientes"));
        
        try {
            List<ClienteDTO> clientes = clienteService.listarClientes();
            for (ClienteDTO cliente : clientes) {
                cmbFiltroCliente.addItem(new FiltroClienteItem(cliente.getId(), cliente.getNombreCompleto()));
            }
        } catch (Exception e) {
            mostrarError("Error al cargar clientes: " + e.getMessage());
        }
    }
    
    /**
     * Aplica el filtro de cliente seleccionado.
     */
    private void aplicarFiltro() {
        FiltroClienteItem item = (FiltroClienteItem) cmbFiltroCliente.getSelectedItem();
        if (item != null) {
            clienteFiltroId = item.getId();
            cargarPedidos();
        }
    }
    
    /**
     * Carga los pedidos desde el servicio y actualiza la tabla.
     */
    public void cargarPedidos() {
        try {
            List<PedidoDTO> pedidos;
            
            if (clienteFiltroId == null) {
                pedidos = pedidoService.listarPedidos();
            } else {
                pedidos = pedidoService.obtenerPedidosPorCliente(clienteFiltroId);
            }
            
            tableModel.setPedidos(pedidos);
            actualizarEstadisticas(pedidos);
        } catch (Exception e) {
            mostrarError("Error al cargar pedidos: " + e.getMessage());
        }
    }
    
    /**
     * Filtra los pedidos por un cliente específico.
     * 
     * @param idCliente ID del cliente
     */
    public void filtrarPorCliente(Integer idCliente) {
        // Seleccionar el cliente en el combo
        for (int i = 0; i < cmbFiltroCliente.getItemCount(); i++) {
            FiltroClienteItem item = cmbFiltroCliente.getItemAt(i);
            if (item.getId() != null && item.getId().equals(idCliente)) {
                cmbFiltroCliente.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * Actualiza las estadísticas mostradas.
     * 
     * @param pedidos Lista de pedidos
     */
    private void actualizarEstadisticas(List<PedidoDTO> pedidos) {
        int cantidad = pedidos.size();
        double totalImporte = tableModel.calcularTotalGeneral();
        
        lblTotal.setText("Total: " + cantidad + " pedido" + (cantidad != 1 ? "s" : ""));
        lblTotalImporte.setText(String.format("Importe total: %.2f €", totalImporte));
    }
    
    /**
     * Abre el diálogo para añadir un nuevo pedido.
     */
    private void anadirPedido() {
        try {
            List<ClienteDTO> clientes = clienteService.listarClientes();
            
            if (clientes.isEmpty()) {
                mostrarAdvertencia("No hay clientes registrados. Debe crear al menos un cliente primero.");
                return;
            }
            
            PedidoDialog dialog = new PedidoDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null,
                clientes
            );
            dialog.setVisible(true);
            
            PedidoDTO nuevoPedido = dialog.getPedido();
            if (nuevoPedido != null) {
                pedidoService.crearPedido(nuevoPedido);
                cargarPedidos();
                mostrarInfo("Pedido creado exitosamente");
            }
        } catch (Exception e) {
            mostrarError("Error al crear pedido: " + e.getMessage());
        }
    }
    
    /**
     * Abre el diálogo para modificar el pedido seleccionado.
     */
    private void modificarPedido() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Debe seleccionar un pedido para modificar");
            return;
        }
        
        try {
            PedidoDTO pedidoSeleccionado = tableModel.getPedidoAt(selectedRow);
            List<ClienteDTO> clientes = clienteService.listarClientes();
            
            PedidoDialog dialog = new PedidoDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                pedidoSeleccionado,
                clientes
            );
            dialog.setVisible(true);
            
            PedidoDTO pedidoModificado = dialog.getPedido();
            if (pedidoModificado != null) {
                pedidoService.actualizarPedido(pedidoModificado);
                cargarPedidos();
                mostrarInfo("Pedido actualizado exitosamente");
            }
        } catch (Exception e) {
            mostrarError("Error al actualizar pedido: " + e.getMessage());
        }
    }
    
    /**
     * Elimina el pedido seleccionado previa confirmación.
     */
    private void eliminarPedido() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Debe seleccionar un pedido para eliminar");
            return;
        }

        PedidoDTO pedidoSeleccionado = tableModel.getPedidoAt(selectedRow);

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el pedido #" + pedidoSeleccionado.getId() + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                pedidoService.eliminarPedido(pedidoSeleccionado.getId());
                cargarPedidos();
                mostrarInfo("Pedido eliminado exitosamente");
            } catch (Exception e) {
                mostrarError("Error al eliminar pedido: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra el diálogo de confirmación de mensaje para el pedido seleccionado.
     */
    private void enviarMensajes() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Debe seleccionar un pedido para enviar mensajes de seguimiento");
            return;
        }

        PedidoDTO pedidoSeleccionado = tableModel.getPedidoAt(selectedRow);

        // Verificar que no esté cancelado
        if ("CANCELADO".equals(pedidoSeleccionado.getEstado())) {
            mostrarAdvertencia("No se puede enviar mensaje de seguimiento para pedidos cancelados");
            return;
        }

        try {
            logger.info("Preparando diálogo de confirmación para pedido ID: " + pedidoSeleccionado.getId());

            // Obtener datos del cliente
            Cliente cliente = obtenerClienteDelPedido(pedidoSeleccionado);
            if (cliente == null) {
                mostrarError("No se pudo obtener la información del cliente");
                return;
            }

            // Generar mensaje usando WhatsAppService (contiene la lógica de estado)
            String whatsappUrl = whatsAppService.generateWhatsAppUrl(pedidoSeleccionado.getId());

            // Extraer el mensaje de la URL (está después de ?text=)
            String mensaje = extraerMensajeDeWhatsAppUrl(whatsappUrl);
            String asuntoEmail = generarAsuntoEmail(pedidoSeleccionado);

            logger.info("Mensaje generado correctamente, mostrando diálogo de confirmación");

            // Mostrar diálogo de confirmación
            MessageConfirmationDialog dialog = new MessageConfirmationDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                pedidoSeleccionado,
                cliente.getNombre() + " " + cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                mensaje,
                asuntoEmail,
                whatsAppService,
                emailService
            );

            dialog.setVisible(true); // El diálogo permanece abierto hasta que el usuario lo cierre

        } catch (ValidationException e) {
            logger.warning("Error de validación al preparar mensaje: " + e.getMessage());
            mostrarAdvertencia("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error inesperado al preparar mensaje: " + e.getMessage(), e);
            mostrarError("Error al preparar el mensaje: " + e.getMessage());
        }
    }

    /**
     * Obtiene el cliente asociado a un pedido.
     */
    private Cliente obtenerClienteDelPedido(PedidoDTO pedido) {
        try {
            ClienteDTO clienteDTO = clienteService.obtenerCliente(pedido.getIdCliente());
            return clienteDTO.toEntity();
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener cliente: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extrae el mensaje de una URL de WhatsApp.
     */
    private String extraerMensajeDeWhatsAppUrl(String whatsappUrl) {
        try {
            int textIndex = whatsappUrl.indexOf("?text=");
            if (textIndex != -1) {
                String encodedMessage = whatsappUrl.substring(textIndex + 6);
                return java.net.URLDecoder.decode(encodedMessage, "UTF-8");
            }
        } catch (Exception e) {
            logger.log(java.util.logging.Level.WARNING, "Error al decodificar mensaje de WhatsApp URL: " + e.getMessage(), e);
        }
        return "Mensaje no disponible";
    }

    /**
     * Genera el asunto del email basado en el estado del pedido.
     */
    private String generarAsuntoEmail(PedidoDTO pedido) {
        String estado = pedido.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = "pending";
        }

        switch (estado.toLowerCase()) {
            case "completado":
                return "Tu pedido ha sido completado - Kilombo";
            case "en_proceso":
                return "Actualización de tu pedido - Kilombo";
            case "pendiente":
                return "Confirmación de pedido recibido - Kilombo";
            case "cancelado":
                return "Pedido cancelado - Kilombo";
            default:
                return "Actualización de pedido - Kilombo";
        }
    }
    
    /**
     * Muestra un mensaje de error.
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Muestra un mensaje informativo.
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Información",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Muestra un mensaje de advertencia.
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Advertencia",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Clase interna para representar items del combo box de filtro.
     */
    private static class FiltroClienteItem {
        private final Integer id;
        private final String nombre;
        
        public FiltroClienteItem(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        public Integer getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return nombre;
        }
    }
}