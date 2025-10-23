package com.kilombo.crm.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un pedido en el sistema.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoNotFoundException extends RuntimeException {
    
    private final Integer pedidoId;
    
    /**
     * Constructor con ID del pedido no encontrado.
     * 
     * @param pedidoId ID del pedido que no se encontró
     */
    public PedidoNotFoundException(Integer pedidoId) {
        super("Pedido con ID " + pedidoId + " no encontrado");
        this.pedidoId = pedidoId;
    }
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje descriptivo del error
     */
    public PedidoNotFoundException(String message) {
        super(message);
        this.pedidoId = null;
    }
    
    /**
     * Constructor con ID y causa.
     * 
     * @param pedidoId ID del pedido que no se encontró
     * @param cause Causa raíz de la excepción
     */
    public PedidoNotFoundException(Integer pedidoId, Throwable cause) {
        super("Pedido con ID " + pedidoId + " no encontrado", cause);
        this.pedidoId = pedidoId;
    }
    
    /**
     * Obtiene el ID del pedido no encontrado.
     * 
     * @return ID del pedido, o null si no se especificó
     */
    public Integer getPedidoId() {
        return pedidoId;
    }
}