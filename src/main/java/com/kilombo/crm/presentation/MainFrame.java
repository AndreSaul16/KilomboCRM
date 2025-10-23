package com.kilombo.crm.presentation;

import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.application.service.PedidoService;
import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.repository.ClienteRepositoryImpl;
import com.kilombo.crm.infrastructure.repository.PedidoRepositoryImpl;
import com.kilombo.crm.presentation.panel.ClientePanel;
import com.kilombo.crm.presentation.panel.PedidoPanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Ventana principal de la aplicación KilomboCRM.
 * Contiene las pestañas para gestionar clientes y pedidos.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private ClientePanel clientePanel;
    private PedidoPanel pedidoPanel;
    
    private ClienteService clienteService;
    private PedidoService pedidoService;
    
    /**
     * Constructor de la ventana principal.
     */
    public MainFrame() {
        initServices();
        initComponents();
        
        setTitle("KilomboCRM - Sistema de Gestión de Clientes y Pedidos");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Verificar conexión a la base de datos al iniciar
        verificarConexion();
    }
    
    /**
     * Inicializa los servicios de la aplicación.
     */
    private void initServices() {
        // Crear repositorios
        ClienteRepositoryImpl clienteRepository = new ClienteRepositoryImpl();
        PedidoRepositoryImpl pedidoRepository = new PedidoRepositoryImpl();
        
        // Crear servicios
        clienteService = new ClienteService(clienteRepository);
        pedidoService = new PedidoService(pedidoRepository, clienteRepository);
    }
    
    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initComponents() {
        // Crear paneles
        clientePanel = new ClientePanel(clienteService);
        pedidoPanel = new PedidoPanel(pedidoService, clienteService);
        
        // Listener para cambiar a pestaña de pedidos cuando se selecciona "Ver Pedidos"
        clientePanel.addPropertyChangeListener("verPedidosCliente", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Integer idCliente = (Integer) evt.getNewValue();
                tabbedPane.setSelectedIndex(1); // Cambiar a pestaña de pedidos
                pedidoPanel.filtrarPorCliente(idCliente);
            }
        });
        
        // Crear pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clientes", createIcon("👥"), clientePanel, "Gestión de Clientes");
        tabbedPane.addTab("Pedidos", createIcon("📦"), pedidoPanel, "Gestión de Pedidos");
        
        add(tabbedPane);
        
        // Crear menú
        crearMenu();
        
        // Crear barra de estado
        crearBarraEstado();
    }
    
    /**
     * Crea el menú de la aplicación.
     */
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');
        
        JMenuItem itemActualizar = new JMenuItem("Actualizar Todo");
        itemActualizar.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemActualizar.addActionListener(e -> actualizarTodo());
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        itemSalir.addActionListener(e -> salir());
        
        menuArchivo.add(itemActualizar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Ver
        JMenu menuVer = new JMenu("Ver");
        menuVer.setMnemonic('V');
        
        JMenuItem itemClientes = new JMenuItem("Clientes");
        itemClientes.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
        itemClientes.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem itemPedidos = new JMenuItem("Pedidos");
        itemPedidos.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        itemPedidos.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        menuVer.add(itemClientes);
        menuVer.add(itemPedidos);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('Y');
        
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        
        JMenuItem itemConexion = new JMenuItem("Probar Conexión");
        itemConexion.addActionListener(e -> verificarConexion());
        
        menuAyuda.add(itemConexion);
        menuAyuda.addSeparator();
        menuAyuda.add(itemAcercaDe);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuVer);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Crea la barra de estado en la parte inferior.
     */
    private void crearBarraEstado() {
        JPanel barraEstado = new JPanel(new BorderLayout());
        barraEstado.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel lblEstado = new JLabel(" KilomboCRM v1.0 - Listo");
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.PLAIN, 11f));
        
        barraEstado.add(lblEstado, BorderLayout.WEST);
        
        add(barraEstado, BorderLayout.SOUTH);
    }
    
    /**
     * Crea un icono simple con texto.
     * 
     * @param emoji Emoji o texto para el icono
     * @return ImageIcon con el texto
     */
    private Icon createIcon(String emoji) {
        return new ImageIcon();
    }
    
    /**
     * Actualiza todos los datos de la aplicación.
     */
    private void actualizarTodo() {
        clientePanel.cargarClientes();
        pedidoPanel.cargarPedidos();
        JOptionPane.showMessageDialog(
            this,
            "Datos actualizados correctamente",
            "Actualizar",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Verifica la conexión a la base de datos.
     */
    private void verificarConexion() {
        try {
            ConexionBD conexion = ConexionBD.getInstance();
            boolean conectado = conexion.testConnection();
            
            if (conectado) {
                JOptionPane.showMessageDialog(
                    this,
                    "Conexión a la base de datos establecida correctamente\n\n" +
                    conexion.getConnectionInfo(),
                    "Conexión Exitosa",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar a la base de datos\n" +
                    "Verifique la configuración en application.properties",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error al verificar la conexión:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Muestra el diálogo "Acerca de".
     */
    private void mostrarAcercaDe() {
        String mensaje = "<html><body style='width: 300px; padding: 10px;'>" +
                "<h2>KilomboCRM v1.0</h2>" +
                "<p><b>Sistema de Gestión de Clientes y Pedidos</b></p>" +
                "<p>Desarrollado con:</p>" +
                "<ul>" +
                "<li>Java 17</li>" +
                "<li>Swing (GUI)</li>" +
                "<li>MySQL (Base de Datos)</li>" +
                "<li>JDBC (Conectividad)</li>" +
                "<li>Maven (Gestión de Dependencias)</li>" +
                "</ul>" +
                "<p><b>Arquitectura:</b> Clean Architecture</p>" +
                "<p><b>Principios:</b> SOLID, Clean Code</p>" +
                "<p><b>Patrones:</b> Singleton, DAO, Service Layer, DTO</p>" +
                "<hr>" +
                "<p style='text-align: center;'>&copy; 2024 Kilombo Team</p>" +
                "</body></html>";
        
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Acerca de KilomboCRM",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Cierra la aplicación con confirmación.
     */
    private void salir() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea salir?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Cerrar conexión a la base de datos
            try {
                ConexionBD.getInstance().closeConnection();
            } catch (Exception e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
            
            System.exit(0);
        }
    }
    
    /**
     * Método principal para ejecutar la aplicación.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }
        
        // Ejecutar en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}