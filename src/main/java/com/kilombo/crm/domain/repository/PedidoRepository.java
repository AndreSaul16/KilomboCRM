package com.kilombo.crm.domain.repository;

import com.kilombo.crm.domain.model.Pedido;
import com.kilombo.crm.application.dto.InformeBI_DTO;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para la entidad Pedido.
 * Define el contrato para las operaciones de persistencia de pedidos.
 * Siguiendo el principio de Inversión de Dependencias (DIP),
 * el dominio define la interfaz y la infraestructura la implementa.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public interface PedidoRepository {
    
    /**
     * Guarda un nuevo pedido en el sistema.
     * 
     * @param pedido Pedido a guardar
     * @return Pedido guardado con su ID generado
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     * @throws com.kilombo.crm.domain.exception.ValidationException si el pedido no es válido
     */
    Pedido save(Pedido pedido);
    
    /**
     * Busca un pedido por su ID.
     * 
     * @param id ID del pedido a buscar
     * @return Optional con el pedido si existe, Optional.empty() si no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    Optional<Pedido> findById(Integer id);
    
    /**
     * Obtiene todos los pedidos del sistema.
     * 
     * @return Lista de todos los pedidos
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    List<Pedido> findAll();
    
    /**
     * Obtiene todos los pedidos de un cliente específico.
     * 
     * @param idCliente ID del cliente
     * @return Lista de pedidos del cliente
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    List<Pedido> findByClienteId(Integer idCliente);
    
    /**
     * Actualiza los datos de un pedido existente.
     * 
     * @param pedido Pedido con los datos actualizados
     * @throws com.kilombo.crm.domain.exception.PedidoNotFoundException si el pedido no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     * @throws com.kilombo.crm.domain.exception.ValidationException si el pedido no es válido
     */
    void update(Pedido pedido);
    
    /**
     * Elimina un pedido por su ID.
     * 
     * @param id ID del pedido a eliminar
     * @throws com.kilombo.crm.domain.exception.PedidoNotFoundException si el pedido no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    void deleteById(Integer id);
    
    /**
     * Cuenta el número de pedidos de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Número de pedidos del cliente
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    int countByClienteId(Integer idCliente);
    
    /**
     * Calcula el total gastado por un cliente.
     *
     * @param idCliente ID del cliente
     * @return Total gastado por el cliente
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    double sumTotalByClienteId(Integer idCliente);

    /**
     * Obtiene los clientes top por ganancia bruta.
     *
     * @param limit Número máximo de clientes a retornar
     * @return Lista de InformeBI_DTO con los clientes top
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    List<InformeBI_DTO> findTopClientsByGrossProfit(int limit);
}