package com.kilombo.crm.infrastructure.database;

/**
 * Resultado de una prueba de conexión a la base de datos.
 * Incluye información sobre el éxito/fallo y tipo de error específico.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ConnectionTestResult {

    /**
     * Tipos de error posibles en la conexión.
     */
    public enum ErrorType {
        HOST_ERROR,        // Error de host/IP
        AUTHENTICATION_ERROR, // Error de usuario/contraseña
        DATABASE_ERROR,    // Base de datos no existe
        CONNECTION_ERROR,  // Error general de conexión
        UNKNOWN           // Error desconocido
    }

    private boolean success;
    private ErrorType errorType;
    private String message;

    /**
     * Constructor por defecto.
     */
    public ConnectionTestResult() {
        this.success = false;
        this.errorType = ErrorType.UNKNOWN;
        this.message = "";
    }

    /**
     * Constructor con parámetros.
     *
     * @param success true si la conexión fue exitosa
     * @param errorType tipo de error (si success es false)
     * @param message mensaje descriptivo
     */
    public ConnectionTestResult(boolean success, ErrorType errorType, String message) {
        this.success = success;
        this.errorType = errorType;
        this.message = message;
    }

    /**
     * Indica si la conexión fue exitosa.
     *
     * @return true si exitosa, false en caso contrario
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Establece el estado de éxito de la conexión.
     *
     * @param success true si exitosa
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Obtiene el tipo de error.
     *
     * @return tipo de error
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Establece el tipo de error.
     *
     * @param errorType tipo de error
     */
    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    /**
     * Obtiene el mensaje descriptivo del resultado.
     *
     * @return mensaje descriptivo
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el mensaje descriptivo del resultado.
     *
     * @param message mensaje descriptivo
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ConnectionTestResult{" +
                "success=" + success +
                ", errorType=" + errorType +
                ", message='" + message + '\'' +
                '}';
    }
}