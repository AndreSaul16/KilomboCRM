package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.infrastructure.repository.GenericRepository;
import com.kilombo.crm.presentation.table.GenericTableModel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel para visualización de tablas adicionales de la base de datos.
 * Permite explorar dinámicamente cualquier tabla del esquema de base de datos.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class AdditionalTablePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(AdditionalTablePanel.class.getName());

    private GenericRepository genericRepository;
    private GenericTableModel tableModel;
    private JTable table;
    private JComboBox<String> tableSelector;
    private JButton btnLoadTable;
    private JButton btnRefreshTables;
    private JLabel lblStatus;
    private JProgressBar progressBar;
    private JTextArea txtTableInfo;

    /**
     * Constructor del panel de tablas adicionales.
     */
    public AdditionalTablePanel() {
        this.genericRepository = new GenericRepository();
        this.tableModel = new GenericTableModel();

        initComponents();
        loadTableList();
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Selección de Tabla"));

        controlPanel.add(new JLabel("Tabla:"));
        tableSelector = new JComboBox<>();
        tableSelector.setPreferredSize(new Dimension(200, 25));
        controlPanel.add(tableSelector);

        btnLoadTable = new JButton("Cargar Tabla");
        btnLoadTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSelectedTable();
            }
        });
        controlPanel.add(btnLoadTable);

        btnRefreshTables = new JButton("Actualizar Lista");
        btnRefreshTables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableList();
            }
        });
        controlPanel.add(btnRefreshTables);

        // Barra de progreso
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        controlPanel.add(progressBar);

        add(controlPanel, BorderLayout.NORTH);

        // Panel central con la tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Datos de la Tabla"));

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Configurar renderers para mejor visualización
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Formatear valores null
                if (value == null) {
                    setText("<null>");
                    setForeground(java.awt.Color.GRAY);
                } else {
                    setForeground(java.awt.Color.BLACK);
                }

                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Panel inferior con información
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información"));

        // Área de información de tabla
        txtTableInfo = new JTextArea(4, 50);
        txtTableInfo.setEditable(false);
        txtTableInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        txtTableInfo.setBackground(getBackground());
        txtTableInfo.setText("Seleccione una tabla para ver su información...");

        JScrollPane infoScrollPane = new JScrollPane(txtTableInfo);
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);

        // Etiqueta de estado
        lblStatus = new JLabel("Listo");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(lblStatus, BorderLayout.SOUTH);

        add(infoPanel, BorderLayout.SOUTH);

        // Configurar atajos de teclado
        setupKeyboardShortcuts();
    }

    /**
     * Configura atajos de teclado.
     */
    private void setupKeyboardShortcuts() {
        // F5 para actualizar lista de tablas
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("F5"), "refreshTables");
        getActionMap().put("refreshTables", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableList();
            }
        });

        // Enter en selector para cargar tabla
        tableSelector.getInputMap().put(
            KeyStroke.getKeyStroke("ENTER"), "loadTable");
        tableSelector.getActionMap().put("loadTable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSelectedTable();
            }
        });
    }

    /**
     * Carga la lista de tablas disponibles en la base de datos.
     */
    private void loadTableList() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                showProgress("Cargando lista de tablas...");
                try {
                    List<String> tables = genericRepository.getAllTables();
                    SwingUtilities.invokeLater(() -> {
                        tableSelector.removeAllItems();
                        for (String tableName : tables) {
                            tableSelector.addItem(tableName);
                        }
                        updateStatus("Lista de tablas actualizada: " + tables.size() + " tablas encontradas");
                        txtTableInfo.setText("Tablas disponibles: " + tables.size() + "\n" +
                                           "Seleccione una tabla para explorar sus datos.");
                    });
                } catch (DatabaseException e) {
                    SwingUtilities.invokeLater(() -> {
                        updateStatus("Error al cargar tablas: " + e.getMessage());
                        JOptionPane.showMessageDialog(AdditionalTablePanel.this,
                            "Error al cargar lista de tablas:\n" + e.getMessage(),
                            "Error de Base de Datos",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }

            @Override
            protected void done() {
                hideProgress();
            }
        };

        worker.execute();
    }

    /**
     * Carga los datos de la tabla seleccionada.
     */
    private void loadSelectedTable() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        if (selectedTable == null || selectedTable.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione una tabla de la lista.",
                "Tabla No Seleccionada",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                showProgress("Cargando datos de tabla '" + selectedTable + "'...");
                try {
                    List<Map<String, Object>> data = genericRepository.findDynamicTableData(selectedTable);
                    List<Map<String, Object>> columnsInfo = genericRepository.getTableColumnsInfo(selectedTable);
                    int rowCount = genericRepository.getTableRowCount(selectedTable);

                    SwingUtilities.invokeLater(() -> {
                        tableModel.setData(data);
                        adjustColumnWidths();
                        updateTableInfo(selectedTable, data.size(), columnsInfo, rowCount);
                        updateStatus("Tabla '" + selectedTable + "' cargada: " + data.size() + " filas");
                    });

                } catch (DatabaseException e) {
                    SwingUtilities.invokeLater(() -> {
                        updateStatus("Error al cargar tabla '" + selectedTable + "': " + e.getMessage());
                        JOptionPane.showMessageDialog(AdditionalTablePanel.this,
                            "Error al cargar datos de tabla '" + selectedTable + "':\n" + e.getMessage(),
                            "Error de Base de Datos",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }

            @Override
            protected void done() {
                hideProgress();
            }
        };

        worker.execute();
    }

    /**
     * Ajusta el ancho de las columnas basado en el contenido.
     */
    private void adjustColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 100; // Ancho mínimo

            // Calcular ancho basado en el header
            String header = table.getColumnName(column);
            if (header != null) {
                width = Math.max(width, header.length() * 8);
            }

            // Calcular ancho basado en las primeras filas
            for (int row = 0; row < Math.min(table.getRowCount(), 50); row++) {
                Object value = table.getValueAt(row, column);
                if (value != null) {
                    String strValue = value.toString();
                    width = Math.max(width, strValue.length() * 7);
                }
            }

            // Limitar ancho máximo
            width = Math.min(width, 300);

            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    /**
     * Actualiza la información mostrada sobre la tabla.
     */
    private void updateTableInfo(String tableName, int dataSize, List<Map<String, Object>> columnsInfo, int totalRows) {
        StringBuilder info = new StringBuilder();
        info.append("Tabla: ").append(tableName).append("\n");
        info.append("Total de filas: ").append(totalRows).append("\n");
        info.append("Filas mostradas: ").append(dataSize).append("\n");
        info.append("Columnas: ").append(columnsInfo.size()).append("\n\n");

        info.append("Estructura de columnas:\n");
        for (Map<String, Object> column : columnsInfo) {
            info.append("• ").append(column.get("Field"))
                .append(" (").append(column.get("Type")).append(")");
            if ("PRI".equals(column.get("Key"))) {
                info.append(" [PRIMARY KEY]");
            }
            if ("NO".equals(column.get("Null"))) {
                info.append(" [NOT NULL]");
            }
            info.append("\n");
        }

        txtTableInfo.setText(info.toString());
    }

    /**
     * Muestra la barra de progreso con mensaje.
     */
    private void showProgress(String message) {
        progressBar.setString(message);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        btnLoadTable.setEnabled(false);
        btnRefreshTables.setEnabled(false);
        tableSelector.setEnabled(false);
    }

    /**
     * Oculta la barra de progreso.
     */
    private void hideProgress() {
        progressBar.setVisible(false);
        btnLoadTable.setEnabled(true);
        btnRefreshTables.setEnabled(true);
        tableSelector.setEnabled(true);
    }

    /**
     * Actualiza el mensaje de estado.
     */
    private void updateStatus(String message) {
        lblStatus.setText(message);
        logger.info(message);
    }

    /**
     * Obtiene el modelo de tabla para acceso externo.
     */
    public GenericTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Fuerza la recarga de la lista de tablas.
     */
    public void refreshTableList() {
        loadTableList();
    }
}