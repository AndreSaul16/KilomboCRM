package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.presentation.dialog.ClienteDialog;
import com.kilombo.crm.presentation.table.ClienteTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel principal para la gestión de clientes.
 * Contiene una tabla con los clientes y botones para las operaciones CRUD.
 * Incluye recuperación automática de errores y mejores mensajes de usuario.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class ClientePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(ClientePanel.class.getName());

    private final ClienteService clienteService;
    private JTable table;
    private ClienteTableModel tableModel;
    private JButton btnAnadir;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnVerPedidos;
    private JLabel lblTotal;
    private JLabel lblStatus;
    private boolean isLoading = false;
    
    /**
     * Constructor del panel.
     * 
     * @param clienteService Servicio de clientes
     */
    public ClientePanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents();
        cargarClientes();
    }
    
    /**
     * Inicializa los componentes del panel.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y estado
        JPanel panelSuperior = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JPanel panelDerecha = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: 0 clientes");
        panelDerecha.add(lblTotal, BorderLayout.NORTH);

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.BLUE);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.ITALIC, 11f));
        panelDerecha.add(lblStatus, BorderLayout.SOUTH);

        panelSuperior.add(panelDerecha, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de clientes
        tableModel = new ClienteTableModel();
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
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Apellido
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Teléfono
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        btnAnadir = new JButton("Añadir");
        btnAnadir.setFont(new Font("Arial", Font.PLAIN, 12));
        btnAnadir.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnAnadir.addActionListener(e -> anadirCliente());

        btnModificar = new JButton("Modificar");
        btnModificar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnModificar.addActionListener(e -> modificarCliente());
        btnModificar.setEnabled(false);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnEliminar.setEnabled(false);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnActualizar.addActionListener(e -> {
            if (!isLoading) {
                cargarClientesAsync();
            }
        });

        btnVerPedidos = new JButton("Ver Pedidos");
        btnVerPedidos.setFont(new Font("Arial", Font.PLAIN, 12));
        btnVerPedidos.addActionListener(e -> verPedidos());
        btnVerPedidos.setEnabled(false);
        
        panelBotones.add(btnAnadir);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVerPedidos);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean haySeleccion = table.getSelectedRow() != -1;
            btnModificar.setEnabled(haySeleccion);
            btnEliminar.setEnabled(haySeleccion);
            btnVerPedidos.setEnabled(haySeleccion);
        });
        
        // Doble click para modificar
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    modificarCliente();
                }
            }
        });
    }
    
    /**
     * Carga los clientes desde el servicio y actualiza la tabla (síncrono).
     */
    public void cargarClientes() {
        cargarClientesAsync().join(); // Esperar a que termine
    }

    /**
     * Carga los clientes de forma asíncrona con recuperación automática de errores.
     */
    public CompletableFuture<Void> cargarClientesAsync() {
        if (isLoading) {
            logger.fine("Carga de clientes ya en progreso, ignorando solicitud");
            return CompletableFuture.completedFuture(null);
        }

        isLoading = true;
        setStatus("Cargando clientes...", Color.BLUE);
        setButtonsEnabled(false);

        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Iniciando carga de clientes");
                java.util.List<ClienteDTO> clientes = clienteService.listarClientes();

                SwingUtilities.invokeLater(() -> {
                    tableModel.setClientes(clientes);
                    lblTotal.setText("Total: " + clientes.size() + " cliente" + (clientes.size() != 1 ? "s" : ""));
                    setStatus("Clientes cargados correctamente", Color.GREEN);
                    logger.info("Clientes cargados exitosamente: " + clientes.size());
                });

            } catch (DatabaseException e) {
                logger.log(Level.SEVERE, "Error de base de datos al cargar clientes: " + e.getMessage(), e);
                SwingUtilities.invokeLater(() -> {
                    mostrarErrorDetallado("Error de conexión a la base de datos",
                        "No se pudo conectar a la base de datos. Verifique que el servidor esté ejecutándose.\n\nDetalles: " + e.getMessage(),
                        () -> cargarClientesAsync()); // Reintento automático
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error inesperado al cargar clientes: " + e.getMessage(), e);
                SwingUtilities.invokeLater(() -> {
                    mostrarErrorDetallado("Error inesperado",
                        "Ocurrió un error inesperado al cargar los clientes.\n\nDetalles: " + e.getMessage(),
                        () -> cargarClientesAsync()); // Reintento automático
                });
            }
        }).whenComplete((result, throwable) -> {
            SwingUtilities.invokeLater(() -> {
                isLoading = false;
                setButtonsEnabled(true);
                if (throwable == null) {
                    // Limpiar mensaje de estado después de 3 segundos
                    Timer timer = new Timer(3000, e -> setStatus(" ", Color.BLACK));
                    timer.setRepeats(false);
                    timer.start();
                }
            });
        });
    }
    
    /**
     * Abre el diálogo para añadir un nuevo cliente.
     */
    private void anadirCliente() {
        ClienteDialog dialog = new ClienteDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            null
        );
        dialog.setVisible(true);

        ClienteDTO nuevoCliente = dialog.getCliente();
        if (nuevoCliente != null) {
            setStatus("Creando cliente...", Color.BLUE);
            setButtonsEnabled(false);

            CompletableFuture.runAsync(() -> {
                try {
                    logger.info("Creando nuevo cliente: " + nuevoCliente.getEmail());
                    clienteService.crearCliente(nuevoCliente);

                    SwingUtilities.invokeLater(() -> {
                        cargarClientesAsync();
                        mostrarInfo("Cliente creado exitosamente");
                        setStatus("Cliente creado correctamente", Color.GREEN);
                    });

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error al crear cliente: " + e.getMessage(), e);
                    SwingUtilities.invokeLater(() -> {
                        mostrarError("Error al crear cliente: " + e.getMessage());
                        setStatus("Error al crear cliente", Color.RED);
                        setButtonsEnabled(true);
                    });
                }
            });
        }
    }
    
    /**
     * Abre el diálogo para modificar el cliente seleccionado.
     */
    private void modificarCliente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Debe seleccionar un cliente para modificar");
            return;
        }
        
        ClienteDTO clienteSeleccionado = tableModel.getClienteAt(selectedRow);
        
        ClienteDialog dialog = new ClienteDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            clienteSeleccionado
        );
        dialog.setVisible(true);
        
        ClienteDTO clienteModificado = dialog.getCliente();
        if (clienteModificado != null) {
            try {
                clienteService.actualizarCliente(clienteModificado);
                cargarClientes();
                mostrarInfo("Cliente actualizado exitosamente");
            } catch (Exception e) {
                mostrarError("Error al actualizar cliente: " + e.getMessage());
            }
        }
    }
    
    /**
     * Elimina el cliente seleccionado previa confirmación.
     */
    private void eliminarCliente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Debe seleccionar un cliente para eliminar");
            return;
        }
        
        ClienteDTO clienteSeleccionado = tableModel.getClienteAt(selectedRow);
        
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el cliente " + clienteSeleccionado.getNombreCompleto() + "?\n" +
            "Esta acción también eliminará todos sus pedidos.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                clienteService.eliminarCliente(clienteSeleccionado.getId());
                cargarClientes();
                mostrarInfo("Cliente eliminado exitosamente");
            } catch (Exception e) {
                mostrarError("Error al eliminar cliente: " + e.getMessage());
            }
        }
    }
    
    /**
     * Muestra los pedidos del cliente seleccionado.
     * Cambia a la pestaña de pedidos con filtro aplicado.
     */
    private void verPedidos() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Debe seleccionar un cliente");
            return;
        }
        
        ClienteDTO clienteSeleccionado = tableModel.getClienteAt(selectedRow);
        
        // Notificar al componente padre (MainFrame) para cambiar de pestaña
        firePropertyChange("verPedidosCliente", null, clienteSeleccionado.getId());
    }
    
    /**
     * Obtiene el cliente seleccionado.
     * 
     * @return Cliente seleccionado, o null si no hay selección
     */
    public ClienteDTO getClienteSeleccionado() {
        int selectedRow = table.getSelectedRow();
        return selectedRow != -1 ? tableModel.getClienteAt(selectedRow) : null;
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
     * Establece el mensaje de estado en la interfaz.
     *
     * @param mensaje Mensaje a mostrar
     * @param color Color del texto
     */
    private void setStatus(String mensaje, Color color) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(color);
    }

    /**
     * Habilita o deshabilita todos los botones de acción.
     *
     * @param enabled true para habilitar, false para deshabilitar
     */
    private void setButtonsEnabled(boolean enabled) {
        btnAnadir.setEnabled(enabled);
        btnModificar.setEnabled(enabled && table.getSelectedRow() != -1);
        btnEliminar.setEnabled(enabled && table.getSelectedRow() != -1);
        btnActualizar.setEnabled(enabled);
        btnVerPedidos.setEnabled(enabled && table.getSelectedRow() != -1);
    }

    /**
     * Muestra un mensaje de error detallado con opción de reintento.
     *
     * @param titulo Título del diálogo
     * @param mensaje Mensaje detallado
     * @param reintento Acción a ejecutar si el usuario elige reintentar
     */
    private void mostrarErrorDetallado(String titulo, String mensaje, Runnable reintento) {
        Object[] options = {"Reintentar", "Cancelar"};
        int choice = JOptionPane.showOptionDialog(
            this,
            mensaje,
            titulo,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == JOptionPane.YES_OPTION && reintento != null) {
            logger.info("Usuario eligió reintentar operación");
            reintento.run();
        } else {
            setStatus("Operación cancelada por el usuario", Color.ORANGE);
        }
    }
}