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
 * Clase Singleton para gestionar la conexión a la base de datos MySQL.
 * Implementa el patrón Singleton thread-safe con doble verificación.
 * Incluye reintentos automáticos, timeouts y validación de estructura.
 *
 * @author KilomboCRM Team
 * @version 2.1
 */
public class ConexionBD {

    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());

    private static ConexionBD instance;
    private Connection connection;
    private final Properties properties;
    private final ConfigurationManager configManager;

    // Configuración de la base de datos
    private String url;
    private String username;
    private String password;
    private final String driver;

    // Configuración de reintentos y timeouts
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int VALIDATION_QUERY_TIMEOUT_S = 5;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Carga la configuración desde application.properties y ConfigurationManager.
     *
     * @throws DatabaseException si no se puede cargar la configuración o el driver
     */
    private ConexionBD() {
        properties = new Properties();
        configManager = ConfigurationManager.getInstance();
        loadProperties();

        // Usar configuración dinámica del ConfigurationManager
        updateConfigurationFromManager();

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
     * Carga el driver JDBC de MySQL.
     *
     * @throws DatabaseException si no se puede cargar el driver
     */
    private void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(
                "No se pudo cargar el driver de MySQL: " + driver, e
            );
        }
    }

    /**
     * Actualiza la configuración desde el ConfigurationManager.
     * Permite cambiar dinámicamente la configuración de conexión.
     */
    private void updateConfigurationFromManager() {
        this.url = configManager.getConnectionUrl();
        this.username = configManager.getUsername();
        this.password = configManager.getPassword();
    }

    /**
     * Actualiza la configuración y reinicia la conexión si es necesario.
     * Se llama cuando se cambian los parámetros de configuración desde la UI.
     */
    public void refreshConfiguration() {
        updateConfigurationFromManager();

        // Cerrar conexión existente para forzar reconexión con nueva configuración
        closeConnection();

        logger.info("Configuración actualizada: " + configManager.getConfigurationInfo());
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
            validateTableStructure(stmt, "clientes", "id", "nombre", "apellido", "email", "telefono");
            validateTableStructure(stmt, "pedidos", "id", "id_cliente", "fecha", "total");

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
        // En MySQL, verificamos la estructura consultando INFORMATION_SCHEMA
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                       "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + tableName + "' ORDER BY ORDINAL_POSITION";

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
        String orphanQuery = "SELECT COUNT(*) FROM pedidos p LEFT JOIN clientes c ON p.id_cliente = c.id WHERE c.id IS NULL";
        try (ResultSet rs = stmt.executeQuery(orphanQuery)) {
            if (rs.next() && rs.getInt(1) > 0) {
                int orphanCount = rs.getInt(1);
                logger.warning("Encontrados " + orphanCount + " pedidos huérfanos (sin cliente asociado)");
                // No lanzamos excepción, solo advertimos
            }
        }

        // Verificar que los datos básicos son consistentes
        String consistencyQuery = "SELECT COUNT(*) FROM clientes WHERE nombre IS NULL OR nombre = ''";
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
        return testConnection(false);
    }

    /**
     * Prueba la conexión a la base de datos con validación completa.
     *
     * @param forceNewConnection true para forzar una nueva conexión con configuración actualizada
     * @return true si la conexión es exitosa y la BD está operativa, false en caso contrario
     */
    public boolean testConnection(boolean forceNewConnection) {
        if (forceNewConnection) {
            closeConnection();
        }

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
                stmt.executeQuery("SELECT COUNT(*) FROM clientes").close();
                stmt.executeQuery("SELECT COUNT(*) FROM pedidos").close();

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
     * Prueba la conexión con configuración específica y devuelve mensaje de error detallado.
     *
     * @param testHost Host/IP a probar
     * @param testUsername Usuario a probar
     * @param testPassword Contraseña a probar
     * @return Resultado de la prueba con mensaje de error si falla
     */
    public ConnectionTestResult testConnectionWithConfig(String testHost, String testUsername, String testPassword) {
        ConnectionTestResult result = new ConnectionTestResult();

        try {
            // Crear URL de prueba
            String testUrl = "jdbc:mysql://" + testHost + ":3306/" + configManager.getDatabase() +
                           "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";

            logger.info("Probando conexión con configuración: Host=" + testHost + ", Usuario=" + testUsername);

            // Intentar conexión con timeout reducido para pruebas
            DriverManager.setLoginTimeout(5); // 5 segundos timeout
            Connection testConn = DriverManager.getConnection(testUrl, testUsername, testPassword);

            if (testConn != null && !testConn.isClosed()) {
                // Verificar que podemos hacer consultas
                try (Statement stmt = testConn.createStatement()) {
                    stmt.setQueryTimeout(5);
                    stmt.executeQuery("SELECT 1").close();

                    result.setSuccess(true);
                    result.setMessage("Conexión exitosa a la base de datos");
                    logger.info("Prueba de conexión exitosa con configuración personalizada");

                } finally {
                    testConn.close();
                }
            }

        } catch (SQLException e) {
            result.setSuccess(false);
            result.setErrorType(getErrorType(e));
            result.setMessage(getDetailedErrorMessage(e, testHost, testUsername));
            logger.log(Level.WARNING, "Error en prueba de conexión: " + e.getMessage(), e);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorType(ConnectionTestResult.ErrorType.UNKNOWN);
            result.setMessage("Error inesperado: " + e.getMessage());
            logger.log(Level.SEVERE, "Error inesperado en prueba de conexión", e);
        }

        return result;
    }

    /**
     * Determina el tipo de error basado en la excepción SQL.
     */
    private ConnectionTestResult.ErrorType getErrorType(SQLException e) {
        String message = e.getMessage().toLowerCase();

        if (message.contains("communications link failure") ||
            message.contains("connection refused") ||
            message.contains("no route to host")) {
            return ConnectionTestResult.ErrorType.HOST_ERROR;
        } else if (message.contains("access denied") ||
                   message.contains("authentication failed")) {
            return ConnectionTestResult.ErrorType.AUTHENTICATION_ERROR;
        } else if (message.contains("unknown database")) {
            return ConnectionTestResult.ErrorType.DATABASE_ERROR;
        } else {
            return ConnectionTestResult.ErrorType.CONNECTION_ERROR;
        }
    }

    /**
     * Genera mensaje de error detallado basado en el tipo de error.
     */
    private String getDetailedErrorMessage(SQLException e, String host, String username) {
        ConnectionTestResult.ErrorType errorType = getErrorType(e);

        switch (errorType) {
            case HOST_ERROR:
                return "No se puede conectar al servidor MySQL en '" + host + "'. Verifique que:\n" +
                       "• La dirección IP/hostname sea correcta\n" +
                       "• El servidor MySQL esté ejecutándose\n" +
                       "• El puerto 3306 esté abierto y accesible";
            case AUTHENTICATION_ERROR:
                return "Error de autenticación para el usuario '" + username + "'. Verifique que:\n" +
                       "• El nombre de usuario sea correcto\n" +
                       "• La contraseña sea correcta\n" +
                       "• El usuario tenga permisos para conectarse desde este host";
            case DATABASE_ERROR:
                return "La base de datos especificada no existe. Verifique que:\n" +
                       "• El nombre de la base de datos sea correcto\n" +
                       "• La base de datos haya sido creada en el servidor";
            case CONNECTION_ERROR:
            default:
                return "Error de conexión: " + e.getMessage() + "\n" +
                       "Verifique la configuración de red y permisos del servidor MySQL.";
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