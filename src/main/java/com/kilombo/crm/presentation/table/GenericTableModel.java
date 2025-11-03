package com.kilombo.crm.presentation.table;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modelo de tabla genérico que puede adaptarse dinámicamente a cualquier estructura de datos.
 * Utiliza List<Map<String, Object>> para representar filas con columnas variables.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class GenericTableModel extends AbstractTableModel {

    private List<Map<String, Object>> data;
    private List<String> columnNames;
    private int rowCount;

    /**
     * Constructor del modelo genérico.
     */
    public GenericTableModel() {
        this.data = new ArrayList<>();
        this.columnNames = new ArrayList<>();
        this.rowCount = 0;
    }

    /**
     * Establece los datos del modelo.
     * Las columnas se determinan automáticamente de la primera fila de datos.
     *
     * @param data Lista de mapas con los datos (cada mapa representa una fila)
     */
    public void setData(List<Map<String, Object>> data) {
        this.data = data != null ? data : new ArrayList<>();
        this.rowCount = this.data.size();

        // Determinar nombres de columnas de la primera fila
        this.columnNames = new ArrayList<>();
        if (!this.data.isEmpty()) {
            Map<String, Object> firstRow = this.data.get(0);
            this.columnNames.addAll(firstRow.keySet());
        }

        // Notificar cambios a la tabla
        fireTableStructureChanged();
    }

    /**
     * Establece nombres de columnas personalizados.
     *
     * @param columnNames Lista de nombres de columnas
     */
    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames != null ? columnNames : new ArrayList<>();
        fireTableStructureChanged();
    }

    /**
     * Obtiene los nombres de las columnas actuales.
     *
     * @return Lista de nombres de columnas
     */
    public List<String> getColumnNames() {
        return new ArrayList<>(columnNames);
    }

    /**
     * Obtiene los datos actuales.
     *
     * @return Lista de mapas con los datos
     */
    public List<Map<String, Object>> getData() {
        return new ArrayList<>(data);
    }

    /**
     * Limpia todos los datos del modelo.
     */
    public void clearData() {
        this.data.clear();
        this.rowCount = 0;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < columnNames.size()) {
            return columnNames.get(column);
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < data.size() && columnIndex >= 0 && columnIndex < columnNames.size()) {
            Map<String, Object> row = data.get(rowIndex);
            String columnName = columnNames.get(columnIndex);
            return row.get(columnName);
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columnNames.size()) {
            // Intentar determinar el tipo de dato de la primera fila no nula
            for (Map<String, Object> row : data) {
                Object value = row.get(columnNames.get(columnIndex));
                if (value != null) {
                    return value.getClass();
                }
            }
        }
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Por defecto, las celdas no son editables en este modelo genérico
        return false;
    }

    /**
     * Obtiene el valor de una celda como String formateado.
     *
     * @param rowIndex Índice de fila
     * @param columnIndex Índice de columna
     * @return Valor formateado como String
     */
    public String getFormattedValueAt(int rowIndex, int columnIndex) {
        Object value = getValueAt(rowIndex, columnIndex);
        if (value == null) {
            return "";
        }

        // Formatear diferentes tipos de datos
        if (value instanceof java.sql.Timestamp) {
            return value.toString(); // Podría formatearse mejor
        } else if (value instanceof java.sql.Date) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "Sí" : "No";
        } else if (value instanceof Number) {
            // Para números, mantener formato natural
            return value.toString();
        }

        return value.toString();
    }

    /**
     * Obtiene una fila completa como mapa.
     *
     * @param rowIndex Índice de fila
     * @return Mapa con los datos de la fila
     */
    public Map<String, Object> getRowData(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            return data.get(rowIndex);
        }
        return null;
    }

    /**
     * Verifica si el modelo tiene datos.
     *
     * @return true si hay datos cargados
     */
    public boolean hasData() {
        return !data.isEmpty();
    }

    /**
     * Obtiene información del modelo para debugging.
     *
     * @return String con información del modelo
     */
    public String getModelInfo() {
        return String.format("GenericTableModel: %d filas, %d columnas",
                           getRowCount(), getColumnCount());
    }
}