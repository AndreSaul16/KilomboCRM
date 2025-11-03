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

/**
 * Implementación del repositorio de Cliente usando JDBC.
 * Implementa el patrón DAO (Data Access Object) y hereda manejo de errores centralizado.
 * Incluye validaciones y logging mejorado.
 *
 * @author KilomboCRM Team
 * @version 3.0
 */
public class ClienteRepositoryImpl extends BaseRepository implements ClienteRepository {
    
    @Override
    public Cliente save(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        return executeWithIntegrityErrorHandling(() -> {
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
            }
        }, "guardar cliente", cliente.getEmail());
    }
    
    @Override
    public Optional<Cliente> findById(Integer id) {
        if (id == null || id <= 0) {
            logger.warning("ID de cliente inválido: " + id);
            return Optional.empty();
        }

        return executeWithErrorHandling(() -> {
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
            }
        }, "buscar cliente por ID " + id);
    }
    
    @Override
    public List<Cliente> findAll() {
        return executeWithErrorHandling(() -> {
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
            }
        }, "obtener lista de clientes");
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

        executeWithRowValidation(() -> {
            String sql = "UPDATE clientes SET nombre = ?, apellido = ?, email = ?, telefono = ? WHERE id = ?";

            try (Connection conn = ConexionBD.getInstance().getConnection();
                  PreparedStatement stmt = conn.prepareStatement(sql)) {

                ClienteMapper.toStatement(stmt, cliente);
                stmt.setInt(5, cliente.getId());

                return stmt.executeUpdate();
            }
        }, "actualizar cliente ID " + cliente.getId(), 1);
    }
    
    @Override
    public void deleteById(Integer id) {
        // Verificar que el cliente existe
        if (!findById(id).isPresent()) {
            throw new ClienteNotFoundException(id);
        }

        executeWithRowValidation(() -> {
            String sql = "DELETE FROM clientes WHERE id = ?";

            try (Connection conn = ConexionBD.getInstance().getConnection();
                  PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                return stmt.executeUpdate();
            }
        }, "eliminar cliente ID " + id, 1);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warning("Email inválido para verificación de existencia: " + email);
            return false;
        }

        return executeWithErrorHandling(() -> {
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
            }
        }, "verificar existencia de email " + email);
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

        return executeWithErrorHandling(() -> {
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
            }
        }, "verificar existencia de email " + email + " excluyendo ID " + excludeId);
    }
}