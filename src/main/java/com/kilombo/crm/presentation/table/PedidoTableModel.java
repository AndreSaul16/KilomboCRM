package com.kilombo.crm.presentation.table;

import com.kilombo.crm.application.dto.PedidoDTO;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla personalizado para mostrar pedidos en un JTable.
 * Extiende AbstractTableModel para proporcionar datos a la tabla.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoTableModel extends AbstractTableModel {
    
    private static final String[] COLUMN_NAMES = {"ID", "Cliente", "Fecha", "Total"};
    private static final Class<?>[] COLUMN_TYPES = {Integer.class, String.class, String.class, String.class};
    
    private List<PedidoDTO> pedidos;
    
    /**
     * Constructor que inicializa la lista vacía.
     */
    public PedidoTableModel() {
        this.pedidos = new ArrayList<>();
    }
    
    /**
     * Constructor con lista inicial de pedidos.
     * 
     * @param pedidos Lista inicial de pedidos
     */
    public PedidoTableModel(List<PedidoDTO> pedidos) {
        this.pedidos = pedidos != null ? new ArrayList<>(pedidos) : new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return pedidos.size();
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
        if (rowIndex < 0 || rowIndex >= pedidos.size()) {
            return null;
        }
        
        PedidoDTO pedido = pedidos.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return pedido.getId();
            case 1:
                return pedido.getNombreCliente() != null ? pedido.getNombreCliente() : "Cliente ID: " + pedido.getIdCliente();
            case 2:
                return pedido.getFechaFormateada();
            case 3:
                return pedido.getTotalFormateado();
            default:
                return null;
        }
    }
    
    /**
     * Establece la lista de pedidos y actualiza la tabla.
     * 
     * @param pedidos Nueva lista de pedidos
     */
    public void setPedidos(List<PedidoDTO> pedidos) {
        this.pedidos = pedidos != null ? new ArrayList<>(pedidos) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    /**
     * Agrega un pedido a la tabla.
     * 
     * @param pedido Pedido a agregar
     */
    public void addPedido(PedidoDTO pedido) {
        if (pedido != null) {
            pedidos.add(pedido);
            int row = pedidos.size() - 1;
            fireTableRowsInserted(row, row);
        }
    }
    
    /**
     * Actualiza un pedido en la tabla.
     * 
     * @param rowIndex Índice de la fila a actualizar
     * @param pedido Pedido con los datos actualizados
     */
    public void updatePedido(int rowIndex, PedidoDTO pedido) {
        if (rowIndex >= 0 && rowIndex < pedidos.size() && pedido != null) {
            pedidos.set(rowIndex, pedido);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
    
    /**
     * Elimina un pedido de la tabla.
     * 
     * @param rowIndex Índice de la fila a eliminar
     */
    public void removePedido(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < pedidos.size()) {
            pedidos.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    /**
     * Obtiene el pedido en la fila especificada.
     * 
     * @param rowIndex Índice de la fila
     * @return Pedido en esa fila, o null si el índice es inválido
     */
    public PedidoDTO getPedidoAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < pedidos.size()) {
            return pedidos.get(rowIndex);
        }
        return null;
    }
    
    /**
     * Limpia todos los datos de la tabla.
     */
    public void clear() {
        int size = pedidos.size();
        if (size > 0) {
            pedidos.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
    
    /**
     * Obtiene la lista completa de pedidos.
     * 
     * @return Lista de pedidos
     */
    public List<PedidoDTO> getPedidos() {
        return new ArrayList<>(pedidos);
    }
    
    /**
     * Calcula el total de todos los pedidos mostrados.
     * 
     * @return Suma total de los pedidos
     */
    public double calcularTotalGeneral() {
        return pedidos.stream()
                .mapToDouble(PedidoDTO::getTotal)
                .sum();
    }
}