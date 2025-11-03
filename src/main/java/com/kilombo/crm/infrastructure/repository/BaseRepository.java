package com.kilombo.crm.infrastructure.repository;

import com.kilombo.crm.domain.exception.DatabaseException;

import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase base para repositorios que implementa el patrón Template Method
 * para centralizar el manejo de errores y eliminar duplicación de código.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public abstract class BaseRepository {

    protected final Logger logger;

    protected BaseRepository() {
        this.logger = Logger.getLogger(getClass().getName());
    }

    /**
     * Ejecuta una operación de base de datos con manejo centralizado de errores.
     *
     * @param operation Operación a ejecutar que puede lanzar SQLException
     * @param operationName Nombre descriptivo de la operación para logging
     * @param <T> Tipo de retorno de la operación
     * @return Resultado de la operación
     * @throws DatabaseException si ocurre un error de base de datos
     */
    protected <T> T executeWithErrorHandling(DatabaseOperation<T> operation, String operationName) {
        try {
            return operation.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL en " + operationName + ": " + e.getMessage(), e);
            throw new DatabaseException("Error en " + operationName + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en " + operationName + ": " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado en " + operationName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Ejecuta una operación de base de datos sin retorno con manejo centralizado de errores.
     *
     * @param operation Operación a ejecutar que puede lanzar SQLException
     * @param operationName Nombre descriptivo de la operación para logging
     * @throws DatabaseException si ocurre un error de base de datos
     */
    protected void executeWithErrorHandling(DatabaseOperationVoid operation, String operationName) {
        executeWithErrorHandling(() -> {
            operation.execute();
            return null;
        }, operationName);
    }

    /**
     * Ejecuta una operación de base de datos con manejo especial para errores de integridad.
     *
     * @param operation Operación a ejecutar
     * @param operationName Nombre descriptivo de la operación
     * @param entityInfo Información de la entidad para logging detallado
     * @param <T> Tipo de retorno
     * @return Resultado de la operación
     * @throws DatabaseException si ocurre un error de base de datos
     */
    protected <T> T executeWithIntegrityErrorHandling(DatabaseOperation<T> operation, String operationName, String entityInfo) {
        try {
            return operation.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL en " + operationName + " (" + entityInfo + "): " + e.getMessage(), e);

            // Distinguir tipos específicos de errores SQL
            if (e.getSQLState() != null) {
                if (e.getSQLState().startsWith("23")) { // Integrity constraint violation
                    throw new DatabaseException("Error de integridad al " + operationName + " (posible duplicado): " + e.getMessage(), e);
                } else if (e.getSQLState().startsWith("42")) { // Syntax error or access rule violation
                    throw new DatabaseException("Error de sintaxis SQL al " + operationName + ": " + e.getMessage(), e);
                }
            }

            throw new DatabaseException("Error al " + operationName + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en " + operationName + " (" + entityInfo + "): " + e.getMessage(), e);
            throw new DatabaseException("Error inesperado al " + operationName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Ejecuta una operación de base de datos con validación de filas afectadas.
     *
     * @param operation Operación que retorna el número de filas afectadas
     * @param operationName Nombre descriptivo de la operación
     * @param expectedRows Número esperado de filas afectadas
     * @throws DatabaseException si no se afectaron las filas esperadas
     */
    protected void executeWithRowValidation(DatabaseOperation<Integer> operation, String operationName, int expectedRows) {
        Integer affectedRows = executeWithErrorHandling(operation, operationName);

        if (affectedRows == null || affectedRows < expectedRows) {
            logger.warning(operationName + " falló: se esperaban " + expectedRows + " filas afectadas, pero fueron " + affectedRows);
            throw new DatabaseException("No se pudo " + operationName + ", ninguna fila afectada");
        }

        logger.fine(operationName + " exitoso: " + affectedRows + " filas afectadas");
    }

    /**
     * Interfaz funcional para operaciones de base de datos que pueden lanzar SQLException.
     */
    @FunctionalInterface
    protected interface DatabaseOperation<T> {
        T execute() throws SQLException;
    }

    /**
     * Interfaz funcional para operaciones de base de datos sin retorno que pueden lanzar SQLException.
     */
    @FunctionalInterface
    protected interface DatabaseOperationVoid {
        void execute() throws SQLException;
    }
}