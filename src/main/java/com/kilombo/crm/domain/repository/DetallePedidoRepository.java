package com.kilombo.crm.domain.repository;

import com.kilombo.crm.domain.model.DetallePedido;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para la entidad DetallePedido.
 * Define el contrato para las operaciones de persistencia de detalles de pedido.
 * Siguiendo el principio de Inversión de Dependencias (DIP),
 * el dominio define la interfaz y la infraestructura la implementa.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public interface DetallePedidoRepository {

    /**
     * Guarda un nuevo detalle de pedido en el sistema.
     *
     * @param detallePedido DetallePedido a guardar
     * @return DetallePedido guardado con su ID generado
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     * @throws com.kilombo.crm.domain.exception.ValidationException si el detalle no es válido
     */
    DetallePedido save(DetallePedido detallePedido);

    /**
     * Busca un detalle de pedido por su ID.
     *
     * @param id ID del detalle de pedido a buscar
     * @return Optional con el detalle si existe, Optional.empty() si no existe
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    Optional<DetallePedido> findById(Integer id);

    /**
     * Obtiene todos los detalles de pedido del sistema.
     *
     * @return Lista de todos los detalles de pedido
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    List<DetallePedido> findAll();

    /**
     * Obtiene todos los detalles de pedido de un pedido específico.
     *
     * @param idPedido ID del pedido
     * @return Lista de detalles del pedido
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    List<DetallePedido> findByPedidoId(Integer idPedido);

    /**
     * Actualiza los datos de un detalle de pedido existente.
     *
     * @param detallePedido DetallePedido con los datos actualizados
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     * @throws com.kilombo.crm.domain.exception.ValidationException si el detalle no es válido
     */
    void update(DetallePedido detallePedido);

    /**
     * Elimina un detalle de pedido por su ID.
     *
     * @param id ID del detalle de pedido a eliminar
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    void deleteById(Integer id);

    /**
     * Encuentra el producto principal (con mayor subtotal) de un pedido.
     *
     * @param idPedido ID del pedido
     * @return Optional con el tipo de producto principal, Optional.empty() si no hay detalles
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error en la BD
     */
    Optional<String> findPrincipalProductByPedidoId(Integer idPedido);
}