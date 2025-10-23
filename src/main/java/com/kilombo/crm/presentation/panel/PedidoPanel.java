package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.application.dto.PedidoDTO;
import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.application.service.PedidoService;
import com.kilombo.crm.presentation.dialog.PedidoDialog;
import com.kilombo.crm.presentation.table.PedidoTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel principal para la gestión de pedidos.
 * Contiene una tabla con los pedidos y botones para las operaciones CRUD.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoPanel extends JPanel {
    
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private JTable table;
    private PedidoTableModel tableModel;
    private JButton btnAnadir;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JComboBox<FiltroClienteItem> cmbFiltroCliente;
    private JLabel lblTotal;
    private JLabel lblTotalImporte;
    
    private Integer clienteFiltroId = null;
    
    /**
     * Constructor del panel.
     * 
     * @param pedidoService Servicio de pedidos
     * @param clienteService Servicio de clientes
     */
    public PedidoPanel(PedidoService pedidoService, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
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
        
        // Configurar anchos de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Cliente
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Fecha
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones y estadísticas
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        btnAnadir = new JButton("Añadir");
        btnAnadir.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnAnadir.addActionListener(e -> anadirPedido());
        
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarPedido());
        btnModificar.setEnabled(false);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarPedido());
        btnEliminar.setEnabled(false);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarPedidos());
        
        panelBotones.add(btnAnadir);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        
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