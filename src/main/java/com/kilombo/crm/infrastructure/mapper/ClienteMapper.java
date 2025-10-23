package com.kilombo.crm.infrastructure.mapper;

import com.kilombo.crm.domain.model.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper para convertir entre ResultSet de BD y entidad Cliente.
 * Centraliza la lógica de mapeo para evitar duplicación de código.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ClienteMapper {
    
    /**
     * Convierte un ResultSet en una entidad Cliente.
     * 
     * @param rs ResultSet con los datos del cliente
     * @return Cliente mapeado desde el ResultSet
     * @throws SQLException si ocurre un error al leer el ResultSet
     */
    public static Cliente fromResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellido(rs.getString("apellido"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefono(rs.getString("telefono"));
        return cliente;
    }
    
    /**
     * Establece los parámetros de un PreparedStatement desde una entidad Cliente.
     * Útil para operaciones INSERT y UPDATE.
     * 
     * @param stmt PreparedStatement a configurar
     * @param cliente Cliente con los datos a establecer
     * @param startIndex Índice inicial del parámetro (1-based)
     * @throws SQLException si ocurre un error al establecer los parámetros
     */
    public static void toStatement(PreparedStatement stmt, Cliente cliente, int startIndex) 
            throws SQLException {
        stmt.setString(startIndex, cliente.getNombre());
        stmt.setString(startIndex + 1, cliente.getApellido());
        stmt.setString(startIndex + 2, cliente.getEmail());
        stmt.setString(startIndex + 3, cliente.getTelefono());
    }
    
    /**
     * Establece los parámetros de un PreparedStatement desde una entidad Cliente.
     * Versión simplificada que asume que los parámetros empiezan en el índice 1.
     * 
     * @param stmt PreparedStatement a configurar
     * @param cliente Cliente con los datos a establecer
     * @throws SQLException si ocurre un error al establecer los parámetros
     */
    public static void toStatement(PreparedStatement stmt, Cliente cliente) 
            throws SQLException {
        toStatement(stmt, cliente, 1);
    }
}