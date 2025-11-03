package com.kilombo.crm.presentation.dialog;

import com.kilombo.crm.application.dto.PedidoDTO;
import com.kilombo.crm.application.service.EmailServiceImpl;
import com.kilombo.crm.application.service.WhatsAppServiceImpl;
import com.kilombo.crm.domain.exception.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diálogo de confirmación para envío de mensajes de seguimiento.
 * Muestra preview del mensaje, destinatario y opciones de envío.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class MessageConfirmationDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(MessageConfirmationDialog.class.getName());

    private final PedidoDTO pedido;
    private final String clienteNombre;
    private final String clienteEmail;
    private final String clienteTelefono;
    private final String mensaje;
    private final String asuntoEmail;
    private final WhatsAppServiceImpl whatsAppService;
    private final EmailServiceImpl emailService;

    private JTextArea txtMensaje;
    private JTextField txtDestinatario;
    private JTextField txtAsunto;
    private JButton btnEnviarEmail;
    private JButton btnEnviarWhatsApp;
    private JButton btnCerrar;

    public MessageConfirmationDialog(Frame parent, PedidoDTO pedido, String clienteNombre,
                                   String clienteEmail, String clienteTelefono, String mensaje,
                                   String asuntoEmail, WhatsAppServiceImpl whatsAppService,
                                   EmailServiceImpl emailService) {
        super(parent, "Confirmar Envío de Mensaje", true);
        this.pedido = pedido;
        this.clienteNombre = clienteNombre;
        this.clienteEmail = clienteEmail;
        this.clienteTelefono = clienteTelefono;
        this.mensaje = mensaje;
        this.asuntoEmail = asuntoEmail;
        this.whatsAppService = whatsAppService;
        this.emailService = emailService;

        initComponents();
        setupLayout();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Información del destinatario
        txtDestinatario = new JTextField();
        txtDestinatario.setEditable(false);
        txtDestinatario.setText(clienteNombre + " - Email: " + clienteEmail + " - Tel: " + clienteTelefono);
        txtDestinatario.setBackground(Color.LIGHT_GRAY);

        // Asunto del email
        txtAsunto = new JTextField();
        txtAsunto.setEditable(false);
        txtAsunto.setText(asuntoEmail);
        txtAsunto.setBackground(Color.LIGHT_GRAY);

        // Mensaje
        txtMensaje = new JTextArea(8, 50);
        txtMensaje.setEditable(false);
        txtMensaje.setText(mensaje);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setLineWrap(true);
        txtMensaje.setBackground(Color.LIGHT_GRAY);
        txtMensaje.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        scrollMensaje.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Botones
        btnEnviarEmail = new JButton("📧 Enviar por Email");
        btnEnviarEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarPorEmail();
            }
        });

        btnEnviarWhatsApp = new JButton("📱 Enviar por WhatsApp");
        btnEnviarWhatsApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarPorWhatsApp();
            }
        });

        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Deshabilitar botones si faltan datos
        btnEnviarEmail.setEnabled(clienteEmail != null && !clienteEmail.trim().isEmpty());
        btnEnviarWhatsApp.setEnabled(clienteTelefono != null && !clienteTelefono.trim().isEmpty());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        ((BorderLayout)getLayout()).setVgap(10);
        ((BorderLayout)getLayout()).setHgap(10);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(5, 5));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de información
        JPanel panelInfo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelInfo.add(new JLabel("Destinatario:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelInfo.add(txtDestinatario, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelInfo.add(new JLabel("Asunto:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelInfo.add(txtAsunto, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelInfo.add(new JLabel("Mensaje:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        panelInfo.add(new JScrollPane(txtMensaje), gbc);

        panelPrincipal.add(panelInfo, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(btnEnviarEmail);
        panelBotones.add(btnEnviarWhatsApp);
        panelBotones.add(btnCerrar);

        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void enviarPorEmail() {
        try {
            logger.info("Generando enlace mailto para email a: " + clienteEmail);

            // Codificar el asunto y el mensaje por separado
            String subjectEncoded = java.net.URLEncoder.encode(asuntoEmail, "UTF-8").replace("+", "%20");
            String bodyEncoded = java.net.URLEncoder.encode(mensaje, "UTF-8").replace("+", "%20");

            // Construir URL mailto completa
            String mailtoUrl = "mailto:" + clienteEmail + "?subject=" + subjectEncoded + "&body=" + bodyEncoded;

            logger.info("URL mailto generada: " + mailtoUrl);

            // Abrir en el navegador por defecto usando URI.create()
            Desktop.getDesktop().browse(new URI(mailtoUrl));

            JOptionPane.showMessageDialog(
                this,
                "Se ha abierto tu cliente de email por defecto con el mensaje preparado.\n" +
                "Si no se abre automáticamente, copia esta URL en tu navegador:\n\n" + mailtoUrl,
                "Email Preparado",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir cliente de email: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(
                this,
                "Error al abrir el cliente de email: " + e.getMessage() + "\n\n" +
                "Intenta copiar manualmente esta URL en tu navegador:\n" +
                "mailto:" + clienteEmail + "?subject=" + asuntoEmail.replace(" ", "%20") +
                "&body=" + mensaje.replace(" ", "%20").replace("\n", "%0A"),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void enviarPorWhatsApp() {
        try {
            logger.info("Generando URL de WhatsApp para pedido ID: " + pedido.getId());
            String whatsappUrl = whatsAppService.generateWhatsAppUrl(pedido.getId());

            logger.info("Abriendo WhatsApp Web con URL: " + whatsappUrl);
            Desktop.getDesktop().browse(URI.create(whatsappUrl));

            JOptionPane.showMessageDialog(
                this,
                "Se ha abierto WhatsApp Web con el mensaje preparado.\n" +
                "Revisa el contenido y envíalo manualmente.",
                "WhatsApp Preparado",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (ValidationException e) {
            logger.warning("Error de validación en WhatsApp: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Error de validación: " + e.getMessage(),
                "Error de Validación",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al abrir WhatsApp: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(
                this,
                "Error al abrir WhatsApp: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}