package com.kilombo.crm.presentation.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel de navegación lateral para KilomboCRM.
 * Proporciona navegación entre módulos principales del sistema.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class NavigationPanel extends JPanel {

    public interface NavigationListener {
        void onNavigateToModule(String moduleName);
    }

    private NavigationListener navigationListener;
    private JButton btnClientes;
    private JButton btnPedidos;
    private JButton btnDashboardBI;
    private JButton btnConfiguracion;
    private JButton btnVisorTablas;

    public NavigationPanel() {
        initComponents();
        setupLayout();
    }

    public void setNavigationListener(NavigationListener listener) {
        this.navigationListener = listener;
    }

    private void initComponents() {
        setPreferredSize(new Dimension(200, 600));
        setBackground(new Color(240, 240, 240));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Botones de navegación
        btnClientes = createNavigationButton("👥 Clientes", "clientes");
        btnPedidos = createNavigationButton("📦 Pedidos", "pedidos");
        btnDashboardBI = createNavigationButton("📊 Dashboard BI", "dashboard_bi");
        btnConfiguracion = createNavigationButton("⚙️ Configuración", "configuracion");
        btnVisorTablas = createNavigationButton("📋 Visor de Tablas", "visor_tablas");

        // Estilo inicial - Clientes seleccionado por defecto
        updateButtonSelection(btnClientes);
    }

    private JButton createNavigationButton(String text, String moduleName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (navigationListener != null) {
                    navigationListener.onNavigateToModule(moduleName);
                }
                updateButtonSelection(button);
            }
        });

        return button;
    }

    private void updateButtonSelection(JButton selectedButton) {
        // Reset all buttons
        resetButtonStyle(btnClientes);
        resetButtonStyle(btnPedidos);
        resetButtonStyle(btnDashboardBI);
        resetButtonStyle(btnConfiguracion);
        resetButtonStyle(btnVisorTablas);

        // Highlight selected button
        selectedButton.setBackground(new Color(70, 130, 180));
        selectedButton.setForeground(Color.WHITE);
        selectedButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void resetButtonStyle(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void setupLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Título
        JLabel lblTitle = new JLabel("KilomboCRM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Espaciador
        add(Box.createVerticalStrut(10));
        add(lblTitle);
        add(Box.createVerticalStrut(20));

        // Botones de navegación
        add(btnClientes);
        add(Box.createVerticalStrut(5));
        add(btnPedidos);
        add(Box.createVerticalStrut(5));
        add(btnDashboardBI);
        add(Box.createVerticalStrut(20));

        // Separador visual
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(180, 1));
        add(separator);
        add(Box.createVerticalStrut(20));

        // Botones de configuración
        add(btnConfiguracion);
        add(Box.createVerticalStrut(5));
        add(btnVisorTablas);

        // Espaciador flexible al final
        add(Box.createVerticalGlue());
    }

    /**
     * Selecciona programáticamente un módulo
     */
    public void selectModule(String moduleName) {
        JButton buttonToSelect = null;
        switch (moduleName) {
            case "clientes":
                buttonToSelect = btnClientes;
                break;
            case "pedidos":
                buttonToSelect = btnPedidos;
                break;
            case "dashboard_bi":
                buttonToSelect = btnDashboardBI;
                break;
            case "configuracion":
                buttonToSelect = btnConfiguracion;
                break;
            case "visor_tablas":
                buttonToSelect = btnVisorTablas;
                break;
        }

        if (buttonToSelect != null) {
            updateButtonSelection(buttonToSelect);
        }
    }
}