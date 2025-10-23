package com.kilombo.crm.infrastructure.repository;

import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.domain.exception.PedidoNotFoundException;
import com.kilombo.crm.domain.model.Pedido;
import com.kilombo.crm.domain.repository.PedidoRepository;
import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.mapper.PedidoMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del repositorio de Pedido usando JDBC.
 * Implementa el patrón DAO (Data Access Object).
 * Incluye manejo robusto de errores y validaciones.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class PedidoRepositoryImpl implements PedidoRepository {

    private static final Logger logger = Logger.getLogger(PedidoRepositoryImpl.class.getName());
    
    @Override
    public Pedido save(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser null");
        }

        String sql = "INSERT INTO pedido (id_cliente, fecha, total) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            PedidoMapper.toStatement(stmt, pedido);

            logger.info("Ejecutando INSERT para pedido del cliente ID: " + pedido.getIdCliente());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warning("INSERT falló: ninguna fila afectada para pedido del cliente " + pedido.getIdCliente());
                throw new DatabaseException("No se pudo guardar el pedido, ninguna fila afectada");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.setId(generatedKeys.getInt(1));
                    logger.info("Pedido guardado exitosamente con ID: " + pedido.getId());
                } else {
                    logger.severe("No se pudo obtener el ID generado del pedido para cliente " + pedido.getIdCliente());
                    throw new DatabaseException("No se pudo obtener el ID generado del pedido");
                }
            }

            return pedido;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al guardar pedido para cliente " + (pedido != null ? pedido.getIdCliente() : "null") + ": " + e.getMessage(), e);

            // Distinguir tipos de errores SQL
            if (e.getSQLState() != null) {
                if (e.getSQLState().startsWith("23")) { // Integrity constraint violation
                    throw new DatabaseException("Error de integridad al guardar pedido (cliente inexistente): " + e.getMessage(), e);
                } else if (e.getSQLState().startsWith("42")) { // Syntax error or access rule violation
                    throw new DatabaseException("Error de sintaxis SQL al guardar pedido: " + e.getMessage(), e);
                }
            }

            throw new DatabaseException("Error al guardar el pedido: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al guardar pedido: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al guardar el pedido: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Pedido> findById(Integer id) {
        String sql = "SELECT id, id_cliente, fecha, total FROM pedido WHERE id = ?";
        
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pedido pedido = PedidoMapper.fromResultSet(rs);
                    return Optional.of(pedido);
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar el pedido con ID " + id + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Pedido> findAll() {
        String sql = "SELECT id, id_cliente, fecha, total FROM pedido ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logger.info("Ejecutando consulta para obtener todos los pedidos");

            int count = 0;
            while (rs.next()) {
                Pedido pedido = PedidoMapper.fromResultSet(rs);
                if (pedido != null) {
                    pedidos.add(pedido);
                    count++;
                } else {
                    logger.warning("Pedido null encontrado en resultado, omitiendo");
                }
            }

            logger.info("Se encontraron " + count + " pedidos");
            return pedidos;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener lista de pedidos: " + e.getMessage(), e);
            throw new DatabaseException("Error al obtener la lista de pedidos: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener lista de pedidos: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener la lista de pedidos: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Pedido> findByClienteId(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            logger.warning("ID de cliente inválido para búsqueda de pedidos: " + idCliente);
            return new ArrayList<>();
        }

        String sql = "SELECT id, id_cliente, fecha, total FROM pedido WHERE id_cliente = ? ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            logger.fine("Buscando pedidos para cliente ID: " + idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedido pedido = PedidoMapper.fromResultSet(rs);
                    if (pedido != null) {
                        pedidos.add(pedido);
                        count++;
                    } else {
                        logger.warning("Pedido null encontrado para cliente " + idCliente + ", omitiendo");
                    }
                }
                logger.fine("Se encontraron " + count + " pedidos para cliente ID: " + idCliente);
            }

            return pedidos;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al obtener pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public void update(Pedido pedido) {
        if (pedido.getId() == null) {
            throw new IllegalArgumentException("El pedido debe tener un ID para ser actualizado");
        }
        
        // Verificar que el pedido existe
        if (!findById(pedido.getId()).isPresent()) {
            throw new PedidoNotFoundException(pedido.getId());
        }
        
        String sql = "UPDATE pedido SET id_cliente = ?, fecha = ?, total = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            PedidoMapper.toStatement(stmt, pedido);
            stmt.setInt(4, pedido.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new PedidoNotFoundException(pedido.getId());
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        // Verificar que el pedido existe
        if (!findById(id).isPresent()) {
            throw new PedidoNotFoundException(id);
        }
        
        String sql = "DELETE FROM pedido WHERE id = ?";
        
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new PedidoNotFoundException(id);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar el pedido: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int countByClienteId(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            logger.warning("ID de cliente inválido para contar pedidos: " + idCliente);
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM pedido WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            logger.fine("Contando pedidos para cliente ID: " + idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.fine("Cliente ID " + idCliente + " tiene " + count + " pedidos");
                    return count;
                }
                logger.warning("No se pudo obtener resultado de conteo para cliente ID: " + idCliente);
                return 0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al contar pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al contar pedidos del cliente: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al contar pedidos del cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al contar pedidos del cliente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public double sumTotalByClienteId(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            logger.warning("ID de cliente inválido para calcular total gastado: " + idCliente);
            return 0.0;
        }

        String sql = "SELECT COALESCE(SUM(total), 0) FROM pedido WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            logger.fine("Calculando total gastado para cliente ID: " + idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble(1);
                    logger.fine("Cliente ID " + idCliente + " ha gastado total: " + total);
                    return total;
                }
                logger.warning("No se pudo obtener resultado de suma para cliente ID: " + idCliente);
                return 0.0;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al calcular total gastado por el cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al calcular total gastado por el cliente: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al calcular total gastado por el cliente " + idCliente + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al calcular total gastado por el cliente: " + e.getMessage(), e);
        }
    }
}