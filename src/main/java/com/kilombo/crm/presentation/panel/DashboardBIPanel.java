package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.application.dto.InformeBI_DTO;
import com.kilombo.crm.application.service.InformeService;
import com.kilombo.crm.domain.exception.DatabaseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel del Dashboard BI para KilomboCRM.
 * Muestra métricas clave de negocio con diseño de tarjetas.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class DashboardBIPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(DashboardBIPanel.class.getName());

    private final InformeService informeService;

    // Componentes de métricas
    private JLabel lblGananciaTotal;
    private JTable tableTopClientes;
    private JLabel lblPedidosEnProceso;

    public DashboardBIPanel(InformeService informeService) {
        this.informeService = informeService;
        initComponents();
        setupLayout();
        cargarDatosBI();
    }

    private void initComponents() {
        setBackground(Color.WHITE);

        // Etiqueta de título
        JLabel lblTitle = new JLabel("📊 Dashboard de Business Intelligence");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(20, 20, 30, 20));

        // Tarjeta de Ganancia Total
        lblGananciaTotal = new JLabel("Cargando...");
        lblGananciaTotal.setFont(new Font("Arial", Font.BOLD, 24));
        lblGananciaTotal.setHorizontalAlignment(SwingConstants.CENTER);

        // Tabla de Top Clientes
        tableTopClientes = new JTable();
        tableTopClientes.setRowHeight(25);
        tableTopClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableTopClientes.setFont(new Font("Arial", Font.PLAIN, 12));

        // Tarjeta de Pedidos en Proceso
        lblPedidosEnProceso = new JLabel("Cargando...");
        lblPedidosEnProceso.setFont(new Font("Arial", Font.BOLD, 18));
        lblPedidosEnProceso.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JLabel lblTitle = new JLabel("📊 Dashboard de Business Intelligence");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, gbc);

        // Primera fila - Ganancia Total y Pedidos en Proceso
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5; gbc.weighty = 0.3;

        // Tarjeta Ganancia Total
        JPanel cardGanancia = createMetricCard("💰 Ganancia Bruta Total", lblGananciaTotal, new Color(34, 139, 34));
        gbc.gridx = 0;
        mainPanel.add(cardGanancia, gbc);

        // Tarjeta Pedidos en Proceso
        JPanel cardPedidos = createMetricCard("📦 Pedidos en Proceso", lblPedidosEnProceso, new Color(255, 140, 0));
        gbc.gridx = 1;
        mainPanel.add(cardPedidos, gbc);

        // Segunda fila - Top Clientes (ocupa toda la fila)
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.weighty = 0.7; gbc.fill = GridBagConstraints.BOTH;

        JPanel cardTopClientes = createTopClientesCard();
        mainPanel.add(cardTopClientes, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(color);
        card.add(lblTitle, BorderLayout.NORTH);

        // Valor
        valueLabel.setForeground(color);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTopClientesCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título
        JLabel lblTitle = new JLabel("🏆 Top 5 Clientes por Ganancia Bruta");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(70, 130, 180));
        card.add(lblTitle, BorderLayout.NORTH);

        // Tabla en scroll
        JScrollPane scrollPane = new JScrollPane(tableTopClientes);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void cargarDatosBI() {
        try {
            logger.info("Cargando datos del Dashboard BI");

            // Cargar Top Clientes
            List<InformeBI_DTO> topClientes = informeService.getTopRentableClients();
            actualizarTablaTopClientes(topClientes);

            // Calcular ganancia total
            double gananciaTotal = topClientes.stream()
                .mapToDouble(InformeBI_DTO::getGananciaTotal)
                .sum();

            // Formatear y mostrar ganancia total
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
            lblGananciaTotal.setText(currencyFormat.format(gananciaTotal));

            // Mostrar pedidos en proceso (placeholder - necesitaríamos un método en el servicio)
            lblPedidosEnProceso.setText("Calculando...");

            logger.info("Dashboard BI cargado exitosamente");

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al cargar Dashboard BI: " + e.getMessage(), e);
            mostrarErrorBD();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al cargar Dashboard BI: " + e.getMessage(), e);
            mostrarErrorBD();
        }
    }

    private void actualizarTablaTopClientes(List<InformeBI_DTO> topClientes) {
        String[] columnNames = {"Cliente", "Ganancia Bruta"};
        Object[][] data = new Object[topClientes.size()][2];

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

        for (int i = 0; i < topClientes.size(); i++) {
            InformeBI_DTO informe = topClientes.get(i);
            data[i][0] = informe.getNombreCliente();
            data[i][1] = currencyFormat.format(informe.getGananciaTotal());
        }

        tableTopClientes.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        });

        // Ajustar ancho de columnas
        tableTopClientes.getColumnModel().getColumn(0).setPreferredWidth(300);
        tableTopClientes.getColumnModel().getColumn(1).setPreferredWidth(150);
    }

    private void mostrarErrorBD() {
        lblGananciaTotal.setText("Error de conexión");
        lblGananciaTotal.setForeground(Color.RED);

        lblPedidosEnProceso.setText("Error de conexión");
        lblPedidosEnProceso.setForeground(Color.RED);

        // Tabla vacía con mensaje de error
        String[] columnNames = {"Cliente", "Ganancia Bruta"};
        Object[][] data = {{"Error al cargar datos", "Verifique conexión BD"}};
        tableTopClientes.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    /**
     * Método para refrescar los datos del dashboard
     */
    public void refrescarDatos() {
        cargarDatosBI();
    }
}