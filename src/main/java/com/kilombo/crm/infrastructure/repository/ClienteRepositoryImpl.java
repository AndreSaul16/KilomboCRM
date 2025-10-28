package com.kilombo.crm.infrastructure.repository;

import com.kilombo.crm.domain.exception.ClienteNotFoundException;
import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.repository.ClienteRepository;
import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.mapper.ClienteMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del repositorio de Cliente usando JDBC.
 * Implementa el patrón DAO (Data Access Object).
 * Incluye manejo robusto de errores y validaciones.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class ClienteRepositoryImpl implements ClienteRepository {

    private static final Logger logger = Logger.getLogger(ClienteRepositoryImpl.class.getName());
    
    @Override
    public Cliente save(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        String sql = "INSERT INTO clientes (nombre, apellido, email, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ClienteMapper.toStatement(stmt, cliente);

            logger.info("Ejecutando INSERT para cliente: " + cliente.getEmail());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warning("INSERT falló: ninguna fila afectada para cliente " + cliente.getEmail());
                throw new DatabaseException("No se pudo guardar el cliente, ninguna fila afectada");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                    logger.info("Cliente guardado exitosamente con ID: " + cliente.getId());
                } else {
                    logger.severe("No se pudo obtener el ID generado del cliente: " + cliente.getEmail());
                    throw new DatabaseException("No se pudo obtener el ID generado del cliente");
                }
            }

            return cliente;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al guardar cliente: " + cliente.getEmail() + " - " + e.getMessage(), e);

            // Distinguir tipos de errores SQL
            if (e.getSQLState() != null) {
                if (e.getSQLState().startsWith("23")) { // Integrity constraint violation
                    throw new DatabaseException("Error de integridad al guardar cliente (posible email duplicado): " + e.getMessage(), e);
                } else if (e.getSQLState().startsWith("42")) { // Syntax error or access rule violation
                    throw new DatabaseException("Error de sintaxis SQL al guardar cliente: " + e.getMessage(), e);
                }
            }

            throw new DatabaseException("Error al guardar el cliente: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al guardar cliente: " + cliente.getEmail() + " - " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al guardar el cliente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Cliente> findById(Integer id) {
        if (id == null || id <= 0) {
            logger.warning("ID de cliente inválido: " + id);
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, apellido, email, telefono FROM clientes WHERE id = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            logger.fine("Buscando cliente con ID: " + id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = ClienteMapper.fromResultSet(rs);
                    if (cliente == null) {
                        logger.warning("Mapper devolvió null para cliente ID: " + id);
                        return Optional.empty();
                    }
                    logger.fine("Cliente encontrado: " + cliente.getEmail());
                    return Optional.of(cliente);
                }
                logger.fine("Cliente no encontrado con ID: " + id);
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al buscar cliente ID " + id + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al buscar el cliente con ID " + id + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al buscar cliente ID " + id + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al buscar el cliente con ID " + id + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Cliente> findAll() {
        String sql = "SELECT id, nombre, apellido, email, telefono FROM clientes ORDER BY apellido, nombre";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logger.info("Ejecutando consulta para obtener todos los clientes");

            int count = 0;
            while (rs.next()) {
                Cliente cliente = ClienteMapper.fromResultSet(rs);
                if (cliente != null) {
                    clientes.add(cliente);
                    count++;
                } else {
                    logger.warning("Cliente null encontrado en resultado, omitiendo");
                }
            }

            logger.info("Se encontraron " + count + " clientes");
            return clientes;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener lista de clientes: " + e.getMessage(), e);
            throw new DatabaseException("Error al obtener la lista de clientes: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al obtener lista de clientes: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener la lista de clientes: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void update(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("El cliente debe tener un ID para ser actualizado");
        }
        
        // Verificar que el cliente existe
        if (!findById(cliente.getId()).isPresent()) {
            throw new ClienteNotFoundException(cliente.getId());
        }
        
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, email = ?, telefono = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ClienteMapper.toStatement(stmt, cliente);
            stmt.setInt(5, cliente.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new ClienteNotFoundException(cliente.getId());
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el cliente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        // Verificar que el cliente existe
        if (!findById(id).isPresent()) {
            throw new ClienteNotFoundException(id);
        }
        
        String sql = "DELETE FROM clientes WHERE id = ?";
        
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new ClienteNotFoundException(id);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar el cliente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warning("Email inválido para verificación de existencia: " + email);
            return false;
        }

        String sql = "SELECT COUNT(*) FROM clientes WHERE email = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim().toLowerCase());
            logger.fine("Verificando existencia de email: " + email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    boolean exists = count > 0;
                    logger.fine("Email " + email + " existe: " + exists + " (count: " + count + ")");
                    return exists;
                }
                logger.warning("No se pudo obtener resultado de consulta de existencia para email: " + email);
                return false;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al verificar existencia de email " + email + ": " + e.getMessage(), e);
            throw new DatabaseException("Error al verificar existencia del email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al verificar existencia de email " + email + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al verificar existencia del email: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsByEmailAndIdNot(String email, Integer excludeId) {
        if (email == null || email.trim().isEmpty()) {
            logger.warning("Email inválido para verificación de existencia: " + email);
            return false;
        }
        if (excludeId == null || excludeId <= 0) {
            logger.warning("ID de exclusión inválido: " + excludeId);
            return false;
        }

        String sql = "SELECT COUNT(*) FROM clientes WHERE email = ? AND id != ?";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim().toLowerCase());
            stmt.setInt(2, excludeId);
            logger.fine("Verificando existencia de email " + email + " excluyendo ID: " + excludeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    boolean exists = count > 0;
                    logger.fine("Email " + email + " existe (excluyendo ID " + excludeId + "): " + exists + " (count: " + count + ")");
                    return exists;
                }
                logger.warning("No se pudo obtener resultado de consulta de existencia para email: " + email);
                return false;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al verificar existencia de email " + email + " (excluyendo ID " + excludeId + "): " + e.getMessage(), e);
            throw new DatabaseException("Error al verificar existencia del email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al verificar existencia de email " + email + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al verificar existencia del email: " + e.getMessage(), e);
        }
    }
}