package com.kilombo.crm.presentation;

import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.application.service.PedidoService;
import com.kilombo.crm.domain.repository.ClienteRepository;
import com.kilombo.crm.domain.repository.DetallePedidoRepository;
import com.kilombo.crm.domain.repository.PedidoRepository;
import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.repository.ClienteRepositoryImpl;
import com.kilombo.crm.infrastructure.repository.DetallePedidoRepositoryImpl;
import com.kilombo.crm.infrastructure.repository.PedidoRepositoryImpl;
import com.kilombo.crm.application.service.InformeService;
import com.kilombo.crm.presentation.panel.*;

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
public class MainFrame extends JFrame implements NavigationPanel.NavigationListener {

    // Componentes principales
    private NavigationPanel navigationPanel;
    private JPanel actionPanel;
    private JPanel contentPanel;

    // Paneles de contenido
    private ClientePanel clientePanel;
    private PedidoPanel pedidoPanel;
    private DashboardBIPanel dashboardBIPanel;
    private ConfiguracionPanel configuracionPanel;
    private AdditionalTablePanel additionalTablePanel;

    // Servicios
    private ClienteService clienteService;
    private PedidoService pedidoService;
    private InformeService informeService;

    // Estado actual
    private String currentModule = "clientes";
    
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

        // No verificar conexión automáticamente al iniciar - permitir funcionamiento sin BBDD
        // verificarConexion();
    }
    
    /**
     * Inicializa los servicios de la aplicación.
     */
    private void initServices() {
        // Crear repositorios
        ClienteRepositoryImpl clienteRepository = new ClienteRepositoryImpl();
        PedidoRepositoryImpl pedidoRepository = new PedidoRepositoryImpl();
        DetallePedidoRepositoryImpl detallePedidoRepository = new DetallePedidoRepositoryImpl();

        // Crear servicios
        clienteService = new ClienteService(clienteRepository);
        pedidoService = new PedidoService(pedidoRepository, clienteRepository);
        informeService = new InformeService(pedidoRepository);
    }
    
    /**
     * Inicializa los componentes de la interfaz con el nuevo layout de 3 áreas.
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        // Crear paneles de contenido
        clientePanel = new ClientePanel(clienteService);
        dashboardBIPanel = new DashboardBIPanel(informeService);

        // Crear repositorios para WhatsApp
        ClienteRepositoryImpl clienteRepository = new ClienteRepositoryImpl();
        PedidoRepositoryImpl pedidoRepository = new PedidoRepositoryImpl();
        DetallePedidoRepositoryImpl detallePedidoRepository = new DetallePedidoRepositoryImpl();

        pedidoPanel = new PedidoPanel(pedidoService, clienteService, pedidoRepository, clienteRepository, detallePedidoRepository);
        configuracionPanel = new ConfiguracionPanel();
        additionalTablePanel = new AdditionalTablePanel();

        // Listener para cambiar a módulo de pedidos cuando se selecciona "Ver Pedidos"
        clientePanel.addPropertyChangeListener("verPedidosCliente", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Integer idCliente = (Integer) evt.getNewValue();
                navigateToModule("pedidos");
                pedidoPanel.filtrarPorCliente(idCliente);
            }
        });

        // Crear panel de navegación lateral
        navigationPanel = new NavigationPanel();
        navigationPanel.setNavigationListener(this);

        // Crear panel de acciones superior (inicialmente vacío)
        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        actionPanel.setBackground(new Color(250, 250, 250));

        // Crear panel de contenido principal
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Mostrar módulo inicial (clientes)
        showModule("clientes");

        // Layout principal
        add(navigationPanel, BorderLayout.WEST);
        add(actionPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Crear menú
        crearMenu();

        // Crear barra de estado
        crearBarraEstado();
    }

    @Override
    public void onNavigateToModule(String moduleName) {
        navigateToModule(moduleName);
    }

    /**
     * Navega a un módulo específico.
     */
    private void navigateToModule(String moduleName) {
        currentModule = moduleName;
        showModule(moduleName);
        updateActionPanel(moduleName);
        navigationPanel.selectModule(moduleName);
    }

    /**
     * Muestra el panel correspondiente al módulo.
     */
    private void showModule(String moduleName) {
        contentPanel.removeAll();

        JPanel panelToShow = null;
        switch (moduleName) {
            case "clientes":
                panelToShow = clientePanel;
                break;
            case "pedidos":
                panelToShow = pedidoPanel;
                break;
            case "dashboard_bi":
                panelToShow = dashboardBIPanel;
                break;
            case "configuracion":
                panelToShow = configuracionPanel;
                break;
            case "visor_tablas":
                panelToShow = additionalTablePanel;
                break;
            default:
                panelToShow = clientePanel;
        }

        if (panelToShow != null) {
            contentPanel.add(panelToShow, BorderLayout.CENTER);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Actualiza el panel de acciones según el módulo actual.
     */
    private void updateActionPanel(String moduleName) {
        actionPanel.removeAll();

        switch (moduleName) {
            case "clientes":
                setupClientesActionPanel();
                break;
            case "pedidos":
                setupPedidosActionPanel();
                break;
            case "dashboard_bi":
                setupDashboardBIActionPanel();
                break;
            default:
                // Panel vacío para otros módulos
                break;
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void setupClientesActionPanel() {
        JButton btnNuevoCliente = new JButton("➕ Nuevo Cliente");
        btnNuevoCliente.setFont(new Font("Arial", Font.BOLD, 8));
        btnNuevoCliente.setBackground(new Color(34, 139, 34));
        btnNuevoCliente.setForeground(Color.WHITE);
        btnNuevoCliente.setFocusPainted(false);
        btnNuevoCliente.addActionListener(e -> {
            // TODO: Implementar método en ClientePanel
            JOptionPane.showMessageDialog(this, "Funcionalidad de nuevo cliente próximamente", "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        JTextField txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 8));
        txtBuscar.setToolTipText("Buscar por nombre, apellido o email");
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setFont(new Font("Arial", Font.PLAIN, 8));
        btnBuscar.addActionListener(e -> {
            // TODO: Implementar búsqueda en ClientePanel
            JOptionPane.showMessageDialog(this, "Funcionalidad de búsqueda próximamente", "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        actionPanel.add(btnNuevoCliente);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(new JLabel("Buscar:"));
        actionPanel.add(txtBuscar);
        actionPanel.add(btnBuscar);
    }

    private void setupPedidosActionPanel() {
        JButton btnNuevoPedido = new JButton("📦 Nuevo Pedido");
        btnNuevoPedido.setFont(new Font("Arial", Font.BOLD, 8));
        btnNuevoPedido.setBackground(new Color(70, 130, 180));
        btnNuevoPedido.setForeground(Color.WHITE);
        btnNuevoPedido.setFocusPainted(false);
        btnNuevoPedido.addActionListener(e -> {
            // TODO: Implementar método en PedidoPanel
            JOptionPane.showMessageDialog(this, "Funcionalidad de nuevo pedido próximamente", "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "EN_PROCESO", "COMPLETADO", "CANCELADO"});
        cmbEstado.setFont(new Font("Arial", Font.PLAIN, 8));
        cmbEstado.addActionListener(e -> {
            // TODO: Implementar filtrado por estado en PedidoPanel
            JOptionPane.showMessageDialog(this, "Filtro por estado próximamente", "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnMensajeSeguimiento = new JButton("💬 Mensaje de Seguimiento");
        btnMensajeSeguimiento.setFont(new Font("Arial", Font.PLAIN, 8));
        btnMensajeSeguimiento.setEnabled(false);
        btnMensajeSeguimiento.addActionListener(e -> {
            // TODO: Implementar envío de mensajes en PedidoPanel
            JOptionPane.showMessageDialog(this, "Mensajes de seguimiento próximamente", "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        actionPanel.add(btnNuevoPedido);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(new JLabel("Estado:"));
        actionPanel.add(cmbEstado);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(btnMensajeSeguimiento);

        // Listener para habilitar/deshabilitar botón de seguimiento
        pedidoPanel.addPropertyChangeListener("pedidoSeleccionado", evt -> {
            Boolean haySeleccion = (Boolean) evt.getNewValue();
            btnMensajeSeguimiento.setEnabled(haySeleccion != null && haySeleccion);
        });
    }

    private void setupDashboardBIActionPanel() {
        JButton btnRefrescar = new JButton("🔄 Actualizar Datos");
        btnRefrescar.setFont(new Font("Arial", Font.PLAIN, 8));
        btnRefrescar.addActionListener(e -> dashboardBIPanel.refrescarDatos());

        actionPanel.add(btnRefrescar);
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
        itemClientes.addActionListener(e -> navigateToModule("clientes"));

        JMenuItem itemPedidos = new JMenuItem("Pedidos");
        itemPedidos.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        itemPedidos.addActionListener(e -> navigateToModule("pedidos"));

        JMenuItem itemDashboardBI = new JMenuItem("Dashboard BI");
        itemDashboardBI.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
        itemDashboardBI.addActionListener(e -> navigateToModule("dashboard_bi"));

        JMenuItem itemConfiguracion = new JMenuItem("Configuración");
        itemConfiguracion.setAccelerator(KeyStroke.getKeyStroke("ctrl 4"));
        itemConfiguracion.addActionListener(e -> navigateToModule("configuracion"));

        JMenuItem itemTablasAdicionales = new JMenuItem("Visor de Tablas");
        itemTablasAdicionales.setAccelerator(KeyStroke.getKeyStroke("ctrl 5"));
        itemTablasAdicionales.addActionListener(e -> navigateToModule("visor_tablas"));
        
        menuVer.add(itemClientes);
        menuVer.add(itemPedidos);
        menuVer.add(itemDashboardBI);
        menuVer.add(itemConfiguracion);
        menuVer.add(itemTablasAdicionales);
        
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
        dashboardBIPanel.refrescarDatos();
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