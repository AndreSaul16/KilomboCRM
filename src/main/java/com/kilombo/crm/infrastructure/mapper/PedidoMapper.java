package com.kilombo.crm.infrastructure.mapper;

import com.kilombo.crm.domain.model.Pedido;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper para convertir entre ResultSet de BD y entidad Pedido.
 * Centraliza la lógica de mapeo para evitar duplicación de código.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoMapper {
    
    /**
     * Convierte un ResultSet en una entidad Pedido.
     * 
     * @param rs ResultSet con los datos del pedido
     * @return Pedido mapeado desde el ResultSet
     * @throws SQLException si ocurre un error al leer el ResultSet
     */
    public static Pedido fromResultSet(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setIdCliente(rs.getInt("id_cliente"));
        
        // Convertir java.sql.Date a java.time.LocalDate
        Date sqlDate = rs.getDate("fecha");
        if (sqlDate != null) {
            pedido.setFecha(sqlDate.toLocalDate());
        }
        
        pedido.setTotal(rs.getDouble("total"));
        return pedido;
    }
    
    /**
     * Establece los parámetros de un PreparedStatement desde una entidad Pedido.
     * Útil para operaciones INSERT y UPDATE.
     * 
     * @param stmt PreparedStatement a configurar
     * @param pedido Pedido con los datos a establecer
     * @param startIndex Índice inicial del parámetro (1-based)
     * @throws SQLException si ocurre un error al establecer los parámetros
     */
    public static void toStatement(PreparedStatement stmt, Pedido pedido, int startIndex) 
            throws SQLException {
        stmt.setInt(startIndex, pedido.getIdCliente());
        
        // Convertir java.time.LocalDate a java.sql.Date
        if (pedido.getFecha() != null) {
            stmt.setDate(startIndex + 1, Date.valueOf(pedido.getFecha()));
        } else {
            stmt.setNull(startIndex + 1, java.sql.Types.DATE);
        }
        
        stmt.setDouble(startIndex + 2, pedido.getTotal());
    }
    
    /**
     * Establece los parámetros de un PreparedStatement desde una entidad Pedido.
     * Versión simplificada que asume que los parámetros empiezan en el índice 1.
     * 
     * @param stmt PreparedStatement a configurar
     * @param pedido Pedido con los datos a establecer
     * @throws SQLException si ocurre un error al establecer los parámetros
     */
    public static void toStatement(PreparedStatement stmt, Pedido pedido) 
            throws SQLException {
        toStatement(stmt, pedido, 1);
    }
}