package com.kilombo.crm.presentation.dialog;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.application.dto.PedidoDTO;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Diálogo modal para crear o editar un pedido.
 * Proporciona un formulario con validación para los datos del pedido.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoDialog extends JDialog {
    
    private JComboBox<ClienteComboItem> cmbCliente;
    private JSpinner spnFecha;
    private JFormattedTextField txtTotal;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private PedidoDTO pedido;
    private List<ClienteDTO> clientes;
    private boolean confirmado = false;
    
    /**
     * Constructor para crear o editar un pedido.
     * 
     * @param parent Ventana padre
     * @param pedido Pedido a editar, o null para crear uno nuevo
     * @param clientes Lista de clientes disponibles
     */
    public PedidoDialog(Frame parent, PedidoDTO pedido, List<ClienteDTO> clientes) {
        super(parent, pedido == null ? "Nuevo Pedido" : "Editar Pedido", true);
        this.pedido = pedido;
        this.clientes = clientes;
        
        initComponents();
        
        if (pedido != null) {
            cargarDatos();
        }
        
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    /**
     * Inicializa los componentes del diálogo.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Cliente: *"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbCliente = new JComboBox<>();
        cargarClientes();
        panelFormulario.add(cmbCliente, gbc);
        
        // Fecha
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Fecha: *"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Configurar JSpinner para fechas
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnFecha = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy");
        spnFecha.setEditor(dateEditor);
        spnFecha.setValue(new Date()); // Fecha actual por defecto
        
        panelFormulario.add(spnFecha, gbc);
        
        // Total
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Total (€): *"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Configurar campo de texto formateado para números
        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.01);
        formatter.setAllowsInvalid(false);
        
        txtTotal = new JFormattedTextField(formatter);
        txtTotal.setColumns(10);
        txtTotal.setValue(0.0);
        
        panelFormulario.add(txtTotal, gbc);
        
        // Nota de campos obligatorios
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC, 10f));
        lblNota.setForeground(Color.GRAY);
        panelFormulario.add(lblNota, gbc);
        
        add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Configurar tecla ESC para cancelar
        getRootPane().registerKeyboardAction(
            e -> cancelar(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    /**
     * Carga los clientes en el combo box.
     */
    private void cargarClientes() {
        cmbCliente.removeAllItems();
        
        if (clientes == null || clientes.isEmpty()) {
            cmbCliente.addItem(new ClienteComboItem(null, "No hay clientes disponibles"));
            cmbCliente.setEnabled(false);
            return;
        }
        
        for (ClienteDTO cliente : clientes) {
            cmbCliente.addItem(new ClienteComboItem(cliente.getId(), cliente.getNombreCompleto()));
        }
    }
    
    /**
     * Carga los datos del pedido en el formulario.
     */
    private void cargarDatos() {
        if (pedido != null) {
            // Seleccionar cliente
            for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                ClienteComboItem item = cmbCliente.getItemAt(i);
                if (item.getId() != null && item.getId().equals(pedido.getIdCliente())) {
                    cmbCliente.setSelectedIndex(i);
                    break;
                }
            }
            
            // Establecer fecha
            if (pedido.getFecha() != null) {
                Date fecha = Date.from(pedido.getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant());
                spnFecha.setValue(fecha);
            }
            
            // Establecer total
            if (pedido.getTotal() != null) {
                txtTotal.setValue(pedido.getTotal());
            }
        }
    }
    
    /**
     * Valida los datos del formulario.
     * 
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean validarDatos() {
        // Validar cliente
        ClienteComboItem clienteSeleccionado = (ClienteComboItem) cmbCliente.getSelectedItem();
        if (clienteSeleccionado == null || clienteSeleccionado.getId() == null) {
            mostrarError("Debe seleccionar un cliente");
            cmbCliente.requestFocus();
            return false;
        }
        
        // Validar fecha
        Date fechaSeleccionada = (Date) spnFecha.getValue();
        LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        if (fecha.isAfter(LocalDate.now())) {
            mostrarError("La fecha del pedido no puede ser futura");
            spnFecha.requestFocus();
            return false;
        }
        
        // Validar total
        try {
            double total = ((Number) txtTotal.getValue()).doubleValue();
            if (total <= 0) {
                mostrarError("El total debe ser mayor que cero");
                txtTotal.requestFocus();
                return false;
            }
        } catch (Exception e) {
            mostrarError("El total debe ser un número válido");
            txtTotal.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Guarda los datos del formulario.
     */
    private void guardar() {
        if (!validarDatos()) {
            return;
        }
        
        if (pedido == null) {
            pedido = new PedidoDTO();
        }
        
        ClienteComboItem clienteSeleccionado = (ClienteComboItem) cmbCliente.getSelectedItem();
        pedido.setIdCliente(clienteSeleccionado.getId());
        pedido.setNombreCliente(clienteSeleccionado.getNombre());
        
        Date fechaSeleccionada = (Date) spnFecha.getValue();
        LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        pedido.setFecha(fecha);
        
        double total = ((Number) txtTotal.getValue()).doubleValue();
        pedido.setTotal(total);
        
        confirmado = true;
        dispose();
    }
    
    /**
     * Cancela la operación.
     */
    private void cancelar() {
        confirmado = false;
        dispose();
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
            "Error de Validación",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Obtiene el pedido con los datos del formulario.
     * 
     * @return Pedido si se confirmó, null si se canceló
     */
    public PedidoDTO getPedido() {
        return confirmado ? pedido : null;
    }
    
    /**
     * Verifica si se confirmó la operación.
     * 
     * @return true si se confirmó, false si se canceló
     */
    public boolean isConfirmado() {
        return confirmado;
    }
    
    /**
     * Clase interna para representar items del combo box de clientes.
     */
    private static class ClienteComboItem {
        private final Integer id;
        private final String nombre;
        
        public ClienteComboItem(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        public Integer getId() {
            return id;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        @Override
        public String toString() {
            return nombre;
        }
    }
}