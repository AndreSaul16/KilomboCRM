package com.kilombo.crm.application.service;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.domain.exception.ClienteNotFoundException;
import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.domain.exception.ValidationException;
import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.repository.ClienteRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestionar clientes.
 * Implementa los casos de uso relacionados con clientes.
 * Coordina entre la capa de presentación y el dominio.
 * Incluye validaciones robustas y manejo de errores.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class ClienteService {

    private static final Logger logger = Logger.getLogger(ClienteService.class.getName());

    private final ClienteRepository clienteRepository;
    
    /**
     * Constructor con inyección de dependencias.
     * 
     * @param clienteRepository Repositorio de clientes
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Crea un nuevo cliente en el sistema.
     *
     * @param dto DTO con los datos del cliente
     * @return DTO del cliente creado con su ID generado
     * @throws ValidationException si los datos no son válidos
     * @throws DatabaseException si ocurre un error de base de datos
     */
    public ClienteDTO crearCliente(ClienteDTO dto) {
        if (dto == null) {
            throw new ValidationException("Los datos del cliente no pueden ser null");
        }

        try {
            logger.info("Creando nuevo cliente: " + dto.getEmail());

            // Validar que el email no esté duplicado
            if (clienteRepository.existsByEmail(dto.getEmail())) {
                logger.warning("Intento de crear cliente con email duplicado: " + dto.getEmail());
                throw new ValidationException("Ya existe un cliente con el email: " + dto.getEmail());
            }

            // Convertir DTO a entidad (esto valida los datos)
            Cliente cliente = dto.toEntity();

            // Guardar en el repositorio
            Cliente clienteGuardado = clienteRepository.save(cliente);

            logger.info("Cliente creado exitosamente con ID: " + clienteGuardado.getId());

            // Retornar DTO
            return ClienteDTO.fromEntity(clienteGuardado);

        } catch (ValidationException e) {
            logger.log(Level.WARNING, "Error de validación al crear cliente: " + e.getMessage(), e);
            throw e;
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al crear cliente: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al crear cliente: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al crear el cliente: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene un cliente por su ID.
     * 
     * @param id ID del cliente
     * @return DTO del cliente
     * @throws ClienteNotFoundException si el cliente no existe
     */
    public ClienteDTO obtenerCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        
        return ClienteDTO.fromEntity(cliente);
    }
    
    /**
     * Obtiene todos los clientes del sistema.
     *
     * @return Lista de DTOs de clientes
     * @throws DatabaseException si ocurre un error al acceder a la base de datos
     */
    public List<ClienteDTO> listarClientes() {
        try {
            logger.info("Obteniendo lista de todos los clientes");

            List<Cliente> clientes = clienteRepository.findAll();

            List<ClienteDTO> dtos = clientes.stream()
                    .map(ClienteDTO::fromEntity)
                    .collect(Collectors.toList());

            logger.info("Se obtuvieron " + dtos.size() + " clientes");
            return dtos;

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al listar clientes: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al listar clientes: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al obtener la lista de clientes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza los datos de un cliente existente.
     * 
     * @param dto DTO con los datos actualizados del cliente
     * @throws ClienteNotFoundException si el cliente no existe
     * @throws ValidationException si los datos no son válidos
     */
    public void actualizarCliente(ClienteDTO dto) {
        if (dto.getId() == null) {
            throw new ValidationException("El ID del cliente es obligatorio para actualizar");
        }
        
        // Verificar que el cliente existe
        if (!clienteRepository.findById(dto.getId()).isPresent()) {
            throw new ClienteNotFoundException(dto.getId());
        }
        
        // Validar que el email no esté duplicado (excluyendo el cliente actual)
        if (clienteRepository.existsByEmailAndIdNot(dto.getEmail(), dto.getId())) {
            throw new ValidationException("Ya existe otro cliente con el email: " + dto.getEmail());
        }
        
        // Convertir DTO a entidad (esto valida los datos)
        Cliente cliente = dto.toEntity();
        
        // Actualizar en el repositorio
        clienteRepository.update(cliente);
    }
    
    /**
     * Elimina un cliente del sistema.
     * 
     * @param id ID del cliente a eliminar
     * @throws ClienteNotFoundException si el cliente no existe
     */
    public void eliminarCliente(Integer id) {
        clienteRepository.deleteById(id);
    }
    
    /**
     * Verifica si existe un cliente con el email especificado.
     * 
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeClientePorEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }
    
    /**
     * Busca clientes por nombre, apellido o email (búsqueda en memoria con validaciones).
     *
     * @param termino Término de búsqueda (puede ser null o vacío)
     * @return Lista de clientes que coinciden con el término
     * @throws DatabaseException si ocurre un error al acceder a la base de datos
     */
    public List<ClienteDTO> buscarClientes(String termino) {
        try {
            if (termino == null || termino.trim().isEmpty()) {
                logger.fine("Búsqueda sin término específico, retornando todos los clientes");
                return listarClientes();
            }

            String terminoLower = termino.trim().toLowerCase();
            logger.info("Buscando clientes con término: '" + termino + "'");

            List<ClienteDTO> todosLosClientes = listarClientes();

            List<ClienteDTO> resultados = todosLosClientes.stream()
                    .filter(c -> {
                        if (c == null) return false;
                        return (c.getNombre() != null && c.getNombre().toLowerCase().contains(terminoLower)) ||
                               (c.getApellido() != null && c.getApellido().toLowerCase().contains(terminoLower)) ||
                               (c.getEmail() != null && c.getEmail().toLowerCase().contains(terminoLower));
                    })
                    .collect(Collectors.toList());

            logger.info("Búsqueda completada: " + resultados.size() + " resultados para '" + termino + "'");
            return resultados;

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos en búsqueda de clientes: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en búsqueda de clientes: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al buscar clientes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene el número total de clientes.
     *
     * @return Número de clientes
     * @throws DatabaseException si ocurre un error al acceder a la base de datos
     */
    public int contarClientes() {
        try {
            logger.fine("Contando total de clientes");

            int count = clienteRepository.findAll().size();

            logger.fine("Total de clientes: " + count);
            return count;

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos al contar clientes: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al contar clientes: " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al contar clientes: " + e.getMessage(), e);
        }
    }
}