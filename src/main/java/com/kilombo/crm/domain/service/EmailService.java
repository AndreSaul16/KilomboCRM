package com.kilombo.crm.domain.service;

/**
 * Servicio de dominio para envío de correos electrónicos.
 * Define el contrato para la lógica de envío de emails de seguimiento.
 * Siguiendo el principio de Inversión de Dependencias (DIP),
 * el dominio define la interfaz y la aplicación la implementa.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public interface EmailService {

    /**
     * Envía un correo electrónico con un mensaje personalizado basado en el estado del pedido.
     *
     * @param idPedido ID del pedido para generar el mensaje
     * @return true si el correo se envió exitosamente, false en caso contrario
     * @throws com.kilombo.crm.domain.exception.ValidationException si faltan datos requeridos (email, etc.)
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error al acceder a la BD
     */
    boolean sendEmail(Integer idPedido);
}