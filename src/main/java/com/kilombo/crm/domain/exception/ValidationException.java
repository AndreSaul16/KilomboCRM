package com.kilombo.crm.domain.exception;

/**
 * Excepción lanzada cuando los datos no cumplen las reglas de validación.
 * Esta es una excepción de dominio que indica problemas con la integridad
 * de los datos según las reglas de negocio.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Mensaje descriptivo del error de validación
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error de validación
     * @param cause Causa raíz de la excepción
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}