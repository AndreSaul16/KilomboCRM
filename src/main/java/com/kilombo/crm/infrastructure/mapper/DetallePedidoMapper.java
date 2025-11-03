package com.kilombo.crm.infrastructure.mapper;

import com.kilombo.crm.domain.model.DetallePedido;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper para convertir entre objetos DetallePedido del dominio y registros de base de datos.
 * Implementa el patrón Data Mapper para separar la lógica de mapeo.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class DetallePedidoMapper {

    /**
     * Mapea un ResultSet a un objeto DetallePedido.
     *
     * @param rs ResultSet con los datos del detalle de pedido
     * @return DetallePedido mapeado, o null si hay error
     * @throws SQLException si ocurre un error al acceder al ResultSet
     */
    public static DetallePedido fromResultSet(ResultSet rs) throws SQLException {
        try {
            DetallePedido detalle = new DetallePedido();
            detalle.setId(rs.getInt("id"));
            detalle.setIdPedido(rs.getInt("id_pedido"));
            detalle.setTipoProducto(rs.getString("tipo_producto"));
            detalle.setDescripcion(rs.getString("descripcion"));
            detalle.setCantidad(rs.getInt("cantidad"));
            detalle.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
            detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
            detalle.setSubtotal(rs.getBigDecimal("subtotal"));
            detalle.setGananciaBruta(rs.getBigDecimal("ganancia_bruta"));
            return detalle;
        } catch (SQLException e) {
            // Log del error y retorno de null para manejo en capas superiores
            System.err.println("Error mapeando DetallePedido desde ResultSet: " + e.getMessage());
            return null;
        }
    }

    /**
     * Mapea un objeto DetallePedido a un PreparedStatement.
     *
     * @param stmt PreparedStatement a configurar
     * @param detalle DetallePedido con los datos
     * @throws SQLException si ocurre un error al configurar el statement
     */
    public static void toStatement(PreparedStatement stmt, DetallePedido detalle) throws SQLException {
        stmt.setInt(1, detalle.getIdPedido());
        stmt.setString(2, detalle.getTipoProducto());
        stmt.setString(3, detalle.getDescripcion());
        stmt.setInt(4, detalle.getCantidad());
        stmt.setBigDecimal(5, detalle.getCostoUnitario());
        stmt.setBigDecimal(6, detalle.getPrecioUnitario());

        // Si es una actualización, incluir el ID al final
        if (detalle.getId() != null) {
            stmt.setInt(7, detalle.getId());
        }
    }
}