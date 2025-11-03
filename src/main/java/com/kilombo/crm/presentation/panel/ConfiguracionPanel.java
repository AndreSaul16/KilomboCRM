package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.database.ConfigurationManager;
import com.kilombo.crm.infrastructure.database.ConnectionTestResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel de configuración de la base de datos.
 * Permite configurar dinámicamente la conexión MySQL desde la interfaz de usuario.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ConfiguracionPanel extends JPanel {

    private ConfigurationManager configManager;
    private ConexionBD conexionBD;

    // Componentes de la interfaz
    private JTextField txtHost;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnProbarConexion;
    private JButton btnGuardar;
    private JButton btnRestaurar;
    private JTextArea txtResultado;
    private JProgressBar progressBar;

    /**
     * Constructor del panel de configuración.
     */
    public ConfiguracionPanel() {
        this.configManager = ConfigurationManager.getInstance();
        this.conexionBD = ConexionBD.getInstance();

        initComponents();
        cargarConfiguracionActual();
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblTitulo = new JLabel("Configuración de Base de Datos MySQL");
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        mainPanel.add(lblTitulo, gbc);

        // Host/IP
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Servidor (Host/IP):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtHost = new JTextField(20);
        mainPanel.add(txtHost, gbc);

        // Usuario
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtUsuario = new JTextField(20);
        mainPanel.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        mainPanel.add(txtPassword, gbc);

        // Botones
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnProbarConexion = new JButton("Probar Conexión");
        btnProbarConexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                probarConexion();
            }
        });

        btnGuardar = new JButton("Guardar Configuración");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarConfiguracion();
            }
        });

        btnRestaurar = new JButton("Restaurar Valores por Defecto");
        btnRestaurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restaurarValoresPorDefecto();
            }
        });

        panelBotones.add(btnProbarConexion);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnRestaurar);
        mainPanel.add(panelBotones, gbc);

        // Barra de progreso
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Probando conexión...");
        mainPanel.add(progressBar, gbc);

        // Área de resultados
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JScrollPane scrollPane = new JScrollPane();
        txtResultado = new JTextArea(10, 40);
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtResultado.setBackground(new Color(240, 240, 240));
        scrollPane.setViewportView(txtResultado);
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Información adicional
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información"));

        JTextArea txtInfo = new JTextArea(
            "Configuración de conexión a MySQL:\n\n" +
            "• Host: Dirección IP o nombre del servidor MySQL\n" +
            "• Usuario: Usuario de MySQL con permisos de conexión\n" +
            "• Contraseña: Contraseña del usuario MySQL\n\n" +
            "Valores por defecto:\n" +
            "• Host: localhost\n" +
            "• Usuario: admin\n" +
            "• Contraseña: admin\n\n" +
            "Use 'Probar Conexión' para verificar la configuración antes de guardar."
        );
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);
        txtInfo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoPanel.add(txtInfo, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    /**
     * Carga la configuración actual en los campos de texto.
     */
    private void cargarConfiguracionActual() {
        txtHost.setText(configManager.getHost());
        txtUsuario.setText(configManager.getUsername());
        txtPassword.setText(configManager.getPassword());
        txtResultado.setText("Configuración actual:\n" + configManager.getConfigurationInfo());
    }

    /**
     * Prueba la conexión con los valores actuales de los campos.
     */
    private void probarConexion() {
        String host = txtHost.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (host.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor complete todos los campos obligatorios (Host y Usuario).",
                "Campos Requeridos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Mostrar barra de progreso
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        btnProbarConexion.setEnabled(false);
        txtResultado.setText("Probando conexión...\n");

        // Ejecutar en un hilo separado para no bloquear la UI
        SwingWorker<ConnectionTestResult, Void> worker = new SwingWorker<ConnectionTestResult, Void>() {
            @Override
            protected ConnectionTestResult doInBackground() throws Exception {
                return conexionBD.testConnectionWithConfig(host, usuario, password);
            }

            @Override
            protected void done() {
                try {
                    ConnectionTestResult result = get();

                    progressBar.setVisible(false);
                    btnProbarConexion.setEnabled(true);

                    if (result.isSuccess()) {
                        txtResultado.setText("✅ CONEXIÓN EXITOSA\n\n" + result.getMessage());
                        txtResultado.setBackground(new Color(200, 255, 200)); // Verde claro
                    } else {
                        txtResultado.setText("❌ ERROR DE CONEXIÓN\n\n" + result.getMessage());
                        txtResultado.setBackground(new Color(255, 200, 200)); // Rojo claro

                        // Mostrar diálogo de error detallado
                        JOptionPane.showMessageDialog(
                            ConfiguracionPanel.this,
                            result.getMessage(),
                            "Error de Conexión - " + getErrorTypeDescription(result.getErrorType()),
                            JOptionPane.ERROR_MESSAGE
                        );
                    }

                } catch (Exception e) {
                    progressBar.setVisible(false);
                    btnProbarConexion.setEnabled(true);
                    txtResultado.setText("❌ ERROR INESPERADO\n\n" + e.getMessage());
                    txtResultado.setBackground(new Color(255, 200, 200));

                    JOptionPane.showMessageDialog(
                        ConfiguracionPanel.this,
                        "Error inesperado al probar la conexión:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    /**
     * Guarda la configuración actual.
     */
    private void guardarConfiguracion() {
        String host = txtHost.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (host.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor complete todos los campos obligatorios (Host y Usuario).",
                "Campos Requeridos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Actualizar configuración
        configManager.setHost(host);
        configManager.setUsername(usuario);
        configManager.setPassword(password);

        // Guardar en archivo
        configManager.saveConfiguration();

        // Actualizar conexión
        conexionBD.refreshConfiguration();

        txtResultado.setText("✅ CONFIGURACIÓN GUARDADA\n\n" + configManager.getConfigurationInfo());
        txtResultado.setBackground(new Color(200, 255, 200));

        JOptionPane.showMessageDialog(
            this,
            "Configuración guardada correctamente.\nLos cambios se aplicarán en la próxima conexión.",
            "Configuración Guardada",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Restaura los valores por defecto.
     */
    private void restaurarValoresPorDefecto() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea restaurar los valores por defecto?\n\n" +
            "Host: localhost\n" +
            "Usuario: admin\n" +
            "Contraseña: admin",
            "Confirmar Restauración",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            configManager.setHost("localhost");
            configManager.setUsername("admin");
            configManager.setPassword("admin");
            configManager.saveConfiguration();

            cargarConfiguracionActual();

            txtResultado.setText("✅ VALORES POR DEFECTO RESTAURADOS\n\n" + configManager.getConfigurationInfo());
            txtResultado.setBackground(new Color(200, 255, 200));

            JOptionPane.showMessageDialog(
                this,
                "Valores por defecto restaurados correctamente.",
                "Restauración Completa",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Obtiene la descripción del tipo de error.
     */
    private String getErrorTypeDescription(ConnectionTestResult.ErrorType errorType) {
        switch (errorType) {
            case HOST_ERROR:
                return "Error de Servidor";
            case AUTHENTICATION_ERROR:
                return "Error de Autenticación";
            case DATABASE_ERROR:
                return "Error de Base de Datos";
            case CONNECTION_ERROR:
                return "Error de Conexión";
            default:
                return "Error Desconocido";
        }
    }
}