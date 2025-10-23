package com.kilombo.crm.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un cliente en el sistema.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ClienteNotFoundException extends RuntimeException {
    
    private final Integer clienteId;
    
    /**
     * Constructor con ID del cliente no encontrado.
     * 
     * @param clienteId ID del cliente que no se encontró
     */
    public ClienteNotFoundException(Integer clienteId) {
        super("Cliente con ID " + clienteId + " no encontrado");
        this.clienteId = clienteId;
    }
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje descriptivo del error
     */
    public ClienteNotFoundException(String message) {
        super(message);
        this.clienteId = null;
    }
    
    /**
     * Constructor con ID y causa.
     * 
     * @param clienteId ID del cliente que no se encontró
     * @param cause Causa raíz de la excepción
     */
    public ClienteNotFoundException(Integer clienteId, Throwable cause) {
        super("Cliente con ID " + clienteId + " no encontrado", cause);
        this.clienteId = clienteId;
    }
    
    /**
     * Obtiene el ID del cliente no encontrado.
     * 
     * @return ID del cliente, o null si no se especificó
     */
    public Integer getClienteId() {
        return clienteId;
    }
}