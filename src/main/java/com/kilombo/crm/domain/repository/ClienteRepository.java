package com.kilombo.crm.domain.repository;

import com.kilombo.crm.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para la entidad Cliente.
 * Define el contrato para las operaciones de persistencia de clientes.
 * Siguiendo el principio de Inversión de Dependencias (DIP),
 * el dominio define la interfaz y la infraestructura la implementa.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public interface ClienteRepository {
    
    /**
     * Guarda un nuevo cliente en el sistema.
     * 
     * @param cliente Cliente a guardar
     * @return Cliente guardado con su ID generado
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     * @throws com.kilombo.crm.domain.exception.ValidationException si el cliente no es válido
     */
    Cliente save(Cliente cliente);
    
    /**
     * Busca un cliente por su ID.
     * 
     * @param id ID del cliente a buscar
     * @return Optional con el cliente si existe, Optional.empty() si no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    Optional<Cliente> findById(Integer id);
    
    /**
     * Obtiene todos los clientes del sistema.
     * 
     * @return Lista de todos los clientes
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    List<Cliente> findAll();
    
    /**
     * Actualiza los datos de un cliente existente.
     * 
     * @param cliente Cliente con los datos actualizados
     * @throws com.kilombo.crm.domain.exception.ClienteNotFoundException si el cliente no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     * @throws com.kilombo.crm.domain.exception.ValidationException si el cliente no es válido
     */
    void update(Cliente cliente);
    
    /**
     * Elimina un cliente por su ID.
     * 
     * @param id ID del cliente a eliminar
     * @throws com.kilombo.crm.domain.exception.ClienteNotFoundException si el cliente no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    void deleteById(Integer id);
    
    /**
     * Verifica si existe un cliente con el email especificado.
     * Útil para validar unicidad del email antes de guardar.
     * 
     * @param email Email a verificar
     * @return true si existe un cliente con ese email, false en caso contrario
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un cliente con el email especificado, excluyendo un ID.
     * Útil para validar unicidad del email al actualizar un cliente.
     * 
     * @param email Email a verificar
     * @param excludeId ID del cliente a excluir de la búsqueda
     * @return true si existe otro cliente con ese email, false en caso contrario
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    boolean existsByEmailAndIdNot(String email, Integer excludeId);
}