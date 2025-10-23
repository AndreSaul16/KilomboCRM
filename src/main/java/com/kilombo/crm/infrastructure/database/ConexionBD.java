package com.kilombo.crm.infrastructure.database;

import com.kilombo.crm.domain.exception.DatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase Singleton para gestionar la conexión a la base de datos H2 embebida.
 * Implementa el patrón Singleton thread-safe con doble verificación.
 * Incluye reintentos automáticos, timeouts y validación de estructura.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class ConexionBD {

    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());

    private static ConexionBD instance;
    private Connection connection;
    private final Properties properties;

    // Configuración de la base de datos
    private final String url;
    private final String username;
    private final String password;
    private final String driver;

    // Configuración de reintentos y timeouts
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int VALIDATION_QUERY_TIMEOUT_S = 5;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Carga la configuración desde application.properties.
     * 
     * @throws DatabaseException si no se puede cargar la configuración o el driver
     */
    private ConexionBD() {
        properties = new Properties();
        loadProperties();
        
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
        this.driver = properties.getProperty("db.driver");
        
        loadDriver();
    }
    
    /**
     * Carga las propiedades de configuración desde el archivo application.properties.
     * 
     * @throws DatabaseException si no se puede cargar el archivo de propiedades
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                throw new DatabaseException(
                    "No se pudo encontrar el archivo application.properties"
                );
            }
            
            properties.load(input);
            
        } catch (IOException e) {
            throw new DatabaseException(
                "Error al cargar el archivo de configuración", e
            );
        }
    }
    
    /**
     * Carga el driver JDBC de H2.
     *
     * @throws DatabaseException si no se puede cargar el driver
     */
    private void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(
                "No se pudo cargar el driver de H2: " + driver, e
            );
        }
    }
    
    /**
     * Obtiene la instancia única de ConexionBD (Singleton).
     * Implementa doble verificación para thread-safety.
     * 
     * @return Instancia única de ConexionBD
     */
    public static ConexionBD getInstance() {
        if (instance == null) {
            synchronized (ConexionBD.class) {
                if (instance == null) {
                    instance = new ConexionBD();
                }
            }
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión activa a la base de datos con reintentos automáticos.
     * Si la conexión está cerrada o es nula, crea una nueva con validación.
     *
     * @return Conexión activa a la base de datos
     * @throws DatabaseException si no se puede establecer la conexión después de reintentos
     */
    public Connection getConnection() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (connection == null || connection.isClosed() || !isConnectionValid()) {
                    logger.info("Intentando establecer conexión a BD (intento " + attempt + "/" + MAX_RETRIES + ")");

                    // Establecer conexión con timeout
                    DriverManager.setLoginTimeout(CONNECTION_TIMEOUT_MS / 1000);
                    connection = DriverManager.getConnection(url, username, password);
                    connection.setAutoCommit(true);

                    // Validar la conexión y estructura
                    validateConnectionAndSchema();

                    logger.info("Conexión a BD establecida exitosamente");
                    return connection;
                }

                // Verificar que la conexión existente sigue siendo válida
                if (isConnectionValid()) {
                    return connection;
                } else {
                    logger.warning("Conexión existente no válida, creando nueva");
                    closeConnection();
                }

            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error en intento " + attempt + " de conexión a BD: " + e.getMessage(), e);

                if (attempt == MAX_RETRIES) {
                    throw new DatabaseException(
                        "Error al establecer conexión con la base de datos después de " + MAX_RETRIES + " intentos: " + e.getMessage(), e
                    );
                }

                // Esperar antes del siguiente intento
                try {
                    Thread.sleep(RETRY_DELAY_MS * attempt); // Backoff exponencial
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new DatabaseException("Interrupción durante reintento de conexión", ie);
                }
            }
        }

        // Este código nunca debería alcanzarse, pero por seguridad
        throw new DatabaseException("Error inesperado al obtener conexión");
    }
    
    /**
     * Cierra la conexión actual a la base de datos.
     * 
     * @throws DatabaseException si ocurre un error al cerrar la conexión
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                "Error al cerrar la conexión con la base de datos", e
            );
        }
    }
    
    /**
     * Verifica si la conexión está activa y funcional.
     *
     * @return true si la conexión está activa, false en caso contrario
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && isConnectionValid();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error al verificar estado de conexión: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica si la conexión es válida ejecutando una consulta de prueba.
     *
     * @return true si la conexión es válida, false en caso contrario
     */
    private boolean isConnectionValid() {
        if (connection == null) {
            return false;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(VALIDATION_QUERY_TIMEOUT_S);
            stmt.executeQuery("SELECT 1");
            return true;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Conexión no válida: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Valida la conexión y verifica que el esquema de la base de datos existe.
     *
     * @throws DatabaseException si la validación falla
     */
    private void validateConnectionAndSchema() {
        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(VALIDATION_QUERY_TIMEOUT_S);

            // Verificar que las tablas existen y tienen estructura correcta
            validateTableStructure(stmt, "cliente", "id", "nombre", "apellido", "email", "telefono");
            validateTableStructure(stmt, "pedido", "id", "id_cliente", "fecha", "total");

            // Verificar integridad referencial básica
            validateReferentialIntegrity(stmt);

            logger.info("Esquema de base de datos validado correctamente");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al validar esquema de BD: " + e.getMessage(), e);
            throw new DatabaseException(
                "La base de datos no tiene el esquema correcto o está corrupta: " + e.getMessage(), e
            );
        }
    }

    /**
     * Valida que una tabla tiene las columnas esperadas.
     *
     * @param stmt Statement para ejecutar consultas
     * @param tableName Nombre de la tabla
     * @param expectedColumns Columnas esperadas
     * @throws SQLException si la validación falla
     */
    private void validateTableStructure(Statement stmt, String tableName, String... expectedColumns) throws SQLException {
        // En H2, podemos verificar la estructura consultando INFORMATION_SCHEMA
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                      "WHERE TABLE_NAME = '" + tableName.toUpperCase() + "' ORDER BY ORDINAL_POSITION";

        try (ResultSet rs = stmt.executeQuery(query)) {
            java.util.Set<String> actualColumns = new java.util.HashSet<>();
            while (rs.next()) {
                actualColumns.add(rs.getString(1).toLowerCase());
            }

            for (String expected : expectedColumns) {
                if (!actualColumns.contains(expected.toLowerCase())) {
                    throw new SQLException("Columna '" + expected + "' no encontrada en tabla '" + tableName + "'");
                }
            }

            logger.fine("Estructura de tabla '" + tableName + "' validada correctamente");
        }
    }

    /**
     * Valida la integridad referencial básica entre tablas.
     *
     * @param stmt Statement para ejecutar consultas
     * @throws SQLException si la validación falla
     */
    private void validateReferentialIntegrity(Statement stmt) throws SQLException {
        // Verificar que no hay pedidos huérfanos (sin cliente)
        String orphanQuery = "SELECT COUNT(*) FROM pedido p LEFT JOIN cliente c ON p.id_cliente = c.id WHERE c.id IS NULL";
        try (ResultSet rs = stmt.executeQuery(orphanQuery)) {
            if (rs.next() && rs.getInt(1) > 0) {
                int orphanCount = rs.getInt(1);
                logger.warning("Encontrados " + orphanCount + " pedidos huérfanos (sin cliente asociado)");
                // No lanzamos excepción, solo advertimos
            }
        }

        // Verificar que los datos básicos son consistentes
        String consistencyQuery = "SELECT COUNT(*) FROM cliente WHERE nombre IS NULL OR nombre = ''";
        try (ResultSet rs = stmt.executeQuery(consistencyQuery)) {
            if (rs.next() && rs.getInt(1) > 0) {
                int invalidCount = rs.getInt(1);
                logger.warning("Encontrados " + invalidCount + " clientes con nombre inválido");
            }
        }

        logger.fine("Integridad referencial validada");
    }
    
    /**
     * Prueba la conexión a la base de datos con validación completa.
     *
     * @return true si la conexión es exitosa y la BD está operativa, false en caso contrario
     */
    public boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn == null || testConn.isClosed()) {
                logger.warning("Prueba de conexión fallida: conexión nula o cerrada");
                return false;
            }

            // Ejecutar una consulta de prueba más completa
            try (Statement stmt = testConn.createStatement()) {
                stmt.setQueryTimeout(VALIDATION_QUERY_TIMEOUT_S);

                // Verificar que podemos hacer consultas básicas
                stmt.executeQuery("SELECT COUNT(*) FROM cliente").close();
                stmt.executeQuery("SELECT COUNT(*) FROM pedido").close();

                logger.info("Prueba de conexión exitosa");
                return true;
            }

        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error de base de datos en prueba de conexión: " + e.getMessage(), e);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL en prueba de conexión: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en prueba de conexión: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene información de la configuración de la base de datos.
     * 
     * @return String con información de la configuración (sin password)
     */
    public String getConnectionInfo() {
        return "URL: " + url + "\n" +
               "Usuario: " + username + "\n" +
               "Driver: " + driver + "\n" +
               "Conectado: " + isConnected();
    }
}