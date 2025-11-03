package com.kilombo.crm.infrastructure.repository;

import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.infrastructure.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio genérico para consultas dinámicas a cualquier tabla.
 * Utiliza ResultSetMetaData para introspección de datos y adaptabilidad del esquema.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class GenericRepository {

    private static final Logger logger = Logger.getLogger(GenericRepository.class.getName());
    private final ConexionBD conexionBD;

    /**
     * Constructor del repositorio genérico.
     */
    public GenericRepository() {
        this.conexionBD = ConexionBD.getInstance();
    }

    /**
     * Obtiene todas las tablas disponibles en la base de datos.
     *
     * @return Lista de nombres de tablas
     * @throws DatabaseException si ocurre un error al consultar las tablas
     */
    public List<String> getAllTables() throws DatabaseException {
        List<String> tables = new ArrayList<>();
        String query = "SHOW TABLES";

        try (Connection conn = conexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tables.add(rs.getString(1));
            }

            logger.info("Encontradas " + tables.size() + " tablas en la base de datos");

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener lista de tablas: " + e.getMessage(), e);
        }

        return tables;
    }

    /**
     * Obtiene los datos dinámicos de una tabla específica.
     * Utiliza ResultSetMetaData para determinar las columnas dinámicamente.
     *
     * @param tableName Nombre de la tabla
     * @return Lista de mapas con los datos (clave= nombre columna, valor= dato)
     * @throws DatabaseException si ocurre un error al consultar la tabla
     */
    public List<Map<String, Object>> findDynamicTableData(String tableName) throws DatabaseException {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection conn = conexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            logger.info("Consultando tabla '" + tableName + "' con " + columnCount + " columnas");

            // Procesar cada fila
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                // Para cada columna, obtener nombre y valor
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);

                    // Manejar valores null
                    if (rs.wasNull()) {
                        value = null;
                    }

                    row.put(columnName, value);
                }

                result.add(row);
            }

            logger.info("Obtenidos " + result.size() + " registros de tabla '" + tableName + "'");

        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar tabla '" + tableName + "': " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Obtiene información de las columnas de una tabla.
     *
     * @param tableName Nombre de la tabla
     * @return Lista de mapas con información de columnas (name, type, nullable, etc.)
     * @throws DatabaseException si ocurre un error al obtener información de columnas
     */
    public List<Map<String, Object>> getTableColumnsInfo(String tableName) throws DatabaseException {
        List<Map<String, Object>> columns = new ArrayList<>();
        String query = "DESCRIBE " + tableName;

        try (Connection conn = conexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> columnInfo = new HashMap<>();
                columnInfo.put("Field", rs.getString("Field"));
                columnInfo.put("Type", rs.getString("Type"));
                columnInfo.put("Null", rs.getString("Null"));
                columnInfo.put("Key", rs.getString("Key"));
                columnInfo.put("Default", rs.getString("Default"));
                columnInfo.put("Extra", rs.getString("Extra"));

                columns.add(columnInfo);
            }

            logger.info("Obtenida información de " + columns.size() + " columnas para tabla '" + tableName + "'");

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener información de columnas para tabla '" + tableName + "': " + e.getMessage(), e);
        }

        return columns;
    }

    /**
     * Ejecuta una consulta SQL personalizada y retorna resultados dinámicos.
     *
     * @param sql Consulta SQL a ejecutar
     * @return Lista de mapas con los resultados
     * @throws DatabaseException si ocurre un error al ejecutar la consulta
     */
    public List<Map<String, Object>> executeDynamicQuery(String sql) throws DatabaseException {
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = conexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            logger.info("Ejecutando consulta dinámica con " + columnCount + " columnas");

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);

                    if (rs.wasNull()) {
                        value = null;
                    }

                    row.put(columnName, value);
                }

                result.add(row);
            }

            logger.info("Consulta ejecutada, " + result.size() + " filas retornadas");

        } catch (SQLException e) {
            throw new DatabaseException("Error al ejecutar consulta: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Verifica si una tabla existe en la base de datos.
     *
     * @param tableName Nombre de la tabla
     * @return true si la tabla existe
     * @throws DatabaseException si ocurre un error al verificar la tabla
     */
    public boolean tableExists(String tableName) throws DatabaseException {
        try {
            List<String> tables = getAllTables();
            return tables.contains(tableName);
        } catch (DatabaseException e) {
            throw new DatabaseException("Error al verificar existencia de tabla '" + tableName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el conteo de registros en una tabla.
     *
     * @param tableName Nombre de la tabla
     * @return Número de registros
     * @throws DatabaseException si ocurre un error al contar registros
     */
    public int getTableRowCount(String tableName) throws DatabaseException {
        String query = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = conexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("Tabla '" + tableName + "' tiene " + count + " registros");
                return count;
            }

            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error al contar registros en tabla '" + tableName + "': " + e.getMessage(), e);
        }
    }
}