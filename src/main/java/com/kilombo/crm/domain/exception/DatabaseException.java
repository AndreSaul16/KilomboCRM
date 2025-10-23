package com.kilombo.crm.domain.exception;

/**
 * Excepción lanzada cuando ocurre un error en las operaciones de base de datos.
 * Encapsula excepciones SQL y otros errores relacionados con la persistencia.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Mensaje descriptivo del error
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción (típicamente SQLException)
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor solo con causa.
     * 
     * @param cause Causa raíz de la excepción
     */
    public DatabaseException(Throwable cause) {
        super("Error en operación de base de datos", cause);
    }
}