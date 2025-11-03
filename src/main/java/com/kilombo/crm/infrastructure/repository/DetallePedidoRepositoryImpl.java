package com.kilombo.crm.infrastructure.repository;

import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.domain.model.DetallePedido;
import com.kilombo.crm.domain.repository.DetallePedidoRepository;
import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.mapper.DetallePedidoMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del repositorio de DetallePedido usando JDBC.
 * Implementa el patrón DAO (Data Access Object).
 * Incluye manejo robusto de errores y validaciones.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class DetallePedidoRepositoryImpl implements DetallePedidoRepository {

    private static final Logger logger = Logger.getLogger(DetallePedidoRepositoryImpl.class.getName());

    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        if (detallePedido == null) {
            throw new IllegalArgumentException("El detalle de pedido no puede ser null");
        }

        String sql = "INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DetallePedidoMapper.toStatement(stmt, detallePedido);

            logger.info("Ejecutando INSERT para detalle de pedido del pedido ID: " + detallePedido.getIdPedido());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warning("INSERT falló: ninguna fila afectada para detalle del pedido " + detallePedido.getIdPedido());
                throw new DatabaseException("No se pudo guardar el detalle del pedido, ninguna fila afectada");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detallePedido.setId(generatedKeys.getInt(1));
                    logger.info("Detalle de pedido guardado exitosamente con ID: " + detallePedido.getId());
                } else {
                    logger.severe("No se pudo obtener el ID generado del detalle para pedido " + detallePedido.getIdPedido());
                    throw new DatabaseException("No se pudo obtener el ID generado del detalle del pedido");
                }
            }

            return detallePedido;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al guardar detalle de pedido para pedido " + (detallePedido != null ? detallePedido.getIdPedido() : "null") + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al guardar el detalle del pedido: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al guardar detalle de pedido: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al guardar el detalle del pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<DetallePedido> findById(Integer id) {
        String sql = "SELECT id, id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario, subtotal, ganancia_bruta FROM detalles_pedido WHERE id = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DetallePedido detalle = DetallePedidoMapper.fromResultSet(rs);
                    return Optional.ofNullable(detalle);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar el detalle del pedido con ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetallePedido> findAll() {
        String sql = "SELECT id, id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario, subtotal, ganancia_bruta FROM detalles_pedido ORDER BY id_pedido";
        List<DetallePedido> detalles = new ArrayList<>();

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logger.info("Ejecutando consulta para obtener todos los detalles de pedido");

            int count = 0;
            while (rs.next()) {
                DetallePedido detalle = DetallePedidoMapper.fromResultSet(rs);
                if (detalle != null) {
                    detalles.add(detalle);
                    count++;
                } else {
                    logger.warning("Detalle de pedido null encontrado en resultado, omitiendo");
                }
            }

            logger.info("Se encontraron " + count + " detalles de pedido");
            return detalles;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener lista de detalles de pedido: " + e.getMessage(), e);
            throw new DatabaseException("Error al obtener la lista de detalles de pedido: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener lista de detalles de pedido: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener la lista de detalles de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetallePedido> findByPedidoId(Integer idPedido) {
        if (idPedido == null || idPedido <= 0) {
            logger.warning("ID de pedido inválido para búsqueda de detalles: " + idPedido);
            return new ArrayList<>();
        }

        String sql = "SELECT id, id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario, subtotal, ganancia_bruta FROM detalles_pedido WHERE id_pedido = ? ORDER BY subtotal DESC";
        List<DetallePedido> detalles = new ArrayList<>();

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);
            logger.fine("Buscando detalles para pedido ID: " + idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    DetallePedido detalle = DetallePedidoMapper.fromResultSet(rs);
                    if (detalle != null) {
                        detalles.add(detalle);
                        count++;
                    } else {
                        logger.warning("Detalle null encontrado para pedido " + idPedido + ", omitiendo");
                    }
                }
                logger.fine("Se encontraron " + count + " detalles para pedido ID: " + idPedido);
            }

            return detalles;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener detalles del pedido " + idPedido + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al obtener detalles del pedido " + idPedido + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener detalles del pedido " + idPedido + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener detalles del pedido " + idPedido + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DetallePedido detallePedido) {
        if (detallePedido.getId() == null) {
            throw new IllegalArgumentException("El detalle de pedido debe tener un ID para ser actualizado");
        }

        // Verificar que el detalle existe
        if (!findById(detallePedido.getId()).isPresent()) {
            throw new DatabaseException("Detalle de pedido con ID " + detallePedido.getId() + " no encontrado");
        }

        String sql = "UPDATE detalles_pedido SET id_pedido = ?, tipo_producto = ?, descripcion = ?, cantidad = ?, costo_unitario = ?, precio_unitario = ? WHERE id = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            DetallePedidoMapper.toStatement(stmt, detallePedido);
            stmt.setInt(7, detallePedido.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Detalle de pedido con ID " + detallePedido.getId() + " no encontrado");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el detalle del pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        // Verificar que el detalle existe
        if (!findById(id).isPresent()) {
            throw new DatabaseException("Detalle de pedido con ID " + id + " no encontrado");
        }

        String sql = "DELETE FROM detalles_pedido WHERE id = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Detalle de pedido con ID " + id + " no encontrado");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar el detalle del pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> findPrincipalProductByPedidoId(Integer idPedido) {
        if (idPedido == null || idPedido <= 0) {
            logger.warning("ID de pedido inválido para encontrar producto principal: " + idPedido);
            return Optional.empty();
        }

        String sql = "SELECT tipo_producto FROM detalles_pedido WHERE id_pedido = ? ORDER BY subtotal DESC LIMIT 1";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);
            logger.fine("Buscando producto principal para pedido ID: " + idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipoProducto = rs.getString("tipo_producto");
                    logger.fine("Producto principal encontrado: " + tipoProducto);
                    return Optional.ofNullable(tipoProducto);
                }
                logger.fine("No se encontraron detalles para pedido ID: " + idPedido);
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al encontrar producto principal del pedido " + idPedido + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al encontrar producto principal del pedido: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al encontrar producto principal del pedido " + idPedido + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al encontrar producto principal del pedido: " + e.getMessage(), e);
        }
    }
}