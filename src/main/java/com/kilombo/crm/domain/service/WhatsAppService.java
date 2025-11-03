package com.kilombo.crm.domain.service;

/**
 * Servicio de dominio para generar enlaces de WhatsApp con mensajes personalizados.
 * Define el contrato para la lógica de seguimiento de pedidos vía WhatsApp.
 * Siguiendo el principio de Inversión de Dependencias (DIP),
 * el dominio define la interfaz y la aplicación la implementa.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public interface WhatsAppService {

    /**
     * Genera una URL de WhatsApp con un mensaje personalizado basado en el estado del pedido.
     *
     * @param idPedido ID del pedido para generar el mensaje
     * @return URL completa de WhatsApp Web con el mensaje pre-cargado
     * @throws com.kilombo.crm.domain.exception.ValidationException si faltan datos requeridos (teléfono, etc.)
     * @throws com.kilombo.crm.domain.exception.DatabaseException si ocurre un error al acceder a la BD
     */
    String generateWhatsAppUrl(Integer idPedido);
}