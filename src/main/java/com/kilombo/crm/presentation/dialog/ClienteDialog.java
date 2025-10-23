package com.kilombo.crm.presentation.dialog;

import com.kilombo.crm.application.dto.ClienteDTO;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para crear o editar un cliente.
 * Proporciona un formulario con validación para los datos del cliente.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ClienteDialog extends JDialog {
    
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private ClienteDTO cliente;
    private boolean confirmado = false;
    
    /**
     * Constructor para crear o editar un cliente.
     * 
     * @param parent Ventana padre
     * @param cliente Cliente a editar, o null para crear uno nuevo
     */
    public ClienteDialog(Frame parent, ClienteDTO cliente) {
        super(parent, cliente == null ? "Nuevo Cliente" : "Editar Cliente", true);
        this.cliente = cliente;
        
        initComponents();
        
        if (cliente != null) {
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
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre: *"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelFormulario.add(txtNombre, gbc);
        
        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Apellido: *"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtApellido = new JTextField(20);
        panelFormulario.add(txtApellido, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Email: *"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        panelFormulario.add(txtEmail, gbc);
        
        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTelefono = new JTextField(20);
        panelFormulario.add(txtTelefono, gbc);
        
        // Nota de campos obligatorios
        gbc.gridx = 0;
        gbc.gridy = 4;
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
        
        // Configurar ENTER en el último campo para guardar
        txtTelefono.addActionListener(e -> guardar());
    }
    
    /**
     * Carga los datos del cliente en el formulario.
     */
    private void cargarDatos() {
        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
            txtEmail.setText(cliente.getEmail());
            txtTelefono.setText(cliente.getTelefono());
        }
    }
    
    /**
     * Valida los datos del formulario.
     * 
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean validarDatos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtNombre.getText().length() > 100) {
            mostrarError("El nombre no puede exceder 100 caracteres");
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar apellido
        if (txtApellido.getText().trim().isEmpty()) {
            mostrarError("El apellido es obligatorio");
            txtApellido.requestFocus();
            return false;
        }
        
        if (txtApellido.getText().length() > 100) {
            mostrarError("El apellido no puede exceder 100 caracteres");
            txtApellido.requestFocus();
            return false;
        }
        
        // Validar email
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarError("El email es obligatorio");
            txtEmail.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().length() > 150) {
            mostrarError("El email no puede exceder 150 caracteres");
            txtEmail.requestFocus();
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("El formato del email no es válido");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar teléfono (opcional)
        if (!txtTelefono.getText().trim().isEmpty()) {
            if (txtTelefono.getText().length() > 20) {
                mostrarError("El teléfono no puede exceder 20 caracteres");
                txtTelefono.requestFocus();
                return false;
            }
            
            if (!txtTelefono.getText().matches("^[+]?[0-9\\s-]{9,20}$")) {
                mostrarError("El formato del teléfono no es válido");
                txtTelefono.requestFocus();
                return false;
            }
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
        
        if (cliente == null) {
            cliente = new ClienteDTO();
        }
        
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellido(txtApellido.getText().trim());
        cliente.setEmail(txtEmail.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        
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
     * Obtiene el cliente con los datos del formulario.
     * 
     * @return Cliente si se confirmó, null si se canceló
     */
    public ClienteDTO getCliente() {
        return confirmado ? cliente : null;
    }
    
    /**
     * Verifica si se confirmó la operación.
     * 
     * @return true si se confirmó, false si se canceló
     */
    public boolean isConfirmado() {
        return confirmado;
    }
}