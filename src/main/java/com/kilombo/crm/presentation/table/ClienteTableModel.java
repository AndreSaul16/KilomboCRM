package com.kilombo.crm.presentation.table;

import com.kilombo.crm.application.dto.ClienteDTO;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla personalizado para mostrar clientes en un JTable.
 * Extiende AbstractTableModel para proporcionar datos a la tabla.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ClienteTableModel extends AbstractTableModel {
    
    private static final String[] COLUMN_NAMES = {"ID", "Nombre", "Apellido", "Email", "Teléfono"};
    private static final Class<?>[] COLUMN_TYPES = {Integer.class, String.class, String.class, String.class, String.class};
    
    private List<ClienteDTO> clientes;
    
    /**
     * Constructor que inicializa la lista vacía.
     */
    public ClienteTableModel() {
        this.clientes = new ArrayList<>();
    }
    
    /**
     * Constructor con lista inicial de clientes.
     * 
     * @param clientes Lista inicial de clientes
     */
    public ClienteTableModel(List<ClienteDTO> clientes) {
        this.clientes = clientes != null ? new ArrayList<>(clientes) : new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return clientes.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_TYPES[columnIndex];
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Las celdas no son editables directamente
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= clientes.size()) {
            return null;
        }
        
        ClienteDTO cliente = clientes.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return cliente.getId();
            case 1:
                return cliente.getNombre();
            case 2:
                return cliente.getApellido();
            case 3:
                return cliente.getEmail();
            case 4:
                return cliente.getTelefono();
            default:
                return null;
        }
    }
    
    /**
     * Establece la lista de clientes y actualiza la tabla.
     * 
     * @param clientes Nueva lista de clientes
     */
    public void setClientes(List<ClienteDTO> clientes) {
        this.clientes = clientes != null ? new ArrayList<>(clientes) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    /**
     * Agrega un cliente a la tabla.
     * 
     * @param cliente Cliente a agregar
     */
    public void addCliente(ClienteDTO cliente) {
        if (cliente != null) {
            clientes.add(cliente);
            int row = clientes.size() - 1;
            fireTableRowsInserted(row, row);
        }
    }
    
    /**
     * Actualiza un cliente en la tabla.
     * 
     * @param rowIndex Índice de la fila a actualizar
     * @param cliente Cliente con los datos actualizados
     */
    public void updateCliente(int rowIndex, ClienteDTO cliente) {
        if (rowIndex >= 0 && rowIndex < clientes.size() && cliente != null) {
            clientes.set(rowIndex, cliente);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
    
    /**
     * Elimina un cliente de la tabla.
     * 
     * @param rowIndex Índice de la fila a eliminar
     */
    public void removeCliente(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < clientes.size()) {
            clientes.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    /**
     * Obtiene el cliente en la fila especificada.
     * 
     * @param rowIndex Índice de la fila
     * @return Cliente en esa fila, o null si el índice es inválido
     */
    public ClienteDTO getClienteAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < clientes.size()) {
            return clientes.get(rowIndex);
        }
        return null;
    }
    
    /**
     * Limpia todos los datos de la tabla.
     */
    public void clear() {
        int size = clientes.size();
        if (size > 0) {
            clientes.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
    
    /**
     * Obtiene la lista completa de clientes.
     * 
     * @return Lista de clientes
     */
    public List<ClienteDTO> getClientes() {
        return new ArrayList<>(clientes);
    }
}