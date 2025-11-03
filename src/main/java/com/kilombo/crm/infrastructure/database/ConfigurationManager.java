package com.kilombo.crm.infrastructure.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de configuración dinámica para la base de datos.
 * Permite configurar dinámicamente la conexión a MySQL desde la interfaz de usuario.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ConfigurationManager {

    private static final Logger logger = Logger.getLogger(ConfigurationManager.class.getName());
    private static final String CONFIG_FILE = "database_config.properties";

    private static ConfigurationManager instance;
    private Properties configProperties;

    // Configuración por defecto
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final String DEFAULT_DATABASE = "kilombo";

    /**
     * Constructor privado para patrón Singleton.
     */
    private ConfigurationManager() {
        configProperties = new Properties();
        loadConfiguration();
    }

    /**
     * Obtiene la instancia única del ConfigurationManager.
     *
     * @return Instancia única de ConfigurationManager
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    /**
     * Carga la configuración desde el archivo de configuración.
     * Si no existe, crea uno con valores por defecto.
     */
    private void loadConfiguration() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            configProperties.load(input);
            logger.info("Configuración cargada desde " + CONFIG_FILE);
        } catch (IOException e) {
            logger.info("Archivo de configuración no encontrado, creando con valores por defecto");
            setDefaultConfiguration();
            saveConfiguration();
        }
    }

    /**
     * Establece la configuración por defecto.
     */
    private void setDefaultConfiguration() {
        configProperties.setProperty("db.host", DEFAULT_HOST);
        configProperties.setProperty("db.username", DEFAULT_USERNAME);
        configProperties.setProperty("db.password", DEFAULT_PASSWORD);
        configProperties.setProperty("db.database", DEFAULT_DATABASE);
    }

    /**
     * Guarda la configuración actual en el archivo.
     */
    public void saveConfiguration() {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            configProperties.store(output, "Configuración de base de datos KilomboCRM");
            logger.info("Configuración guardada en " + CONFIG_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar la configuración", e);
        }
    }

    /**
     * Obtiene la URL completa de conexión a la base de datos.
     *
     * @return URL de conexión JDBC
     */
    public String getConnectionUrl() {
        String host = getHost();
        String database = getDatabase();
        return "jdbc:mysql://" + host + ":3306/" + database +
               "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
    }

    /**
     * Obtiene el host/IP del servidor MySQL.
     *
     * @return Host del servidor
     */
    public String getHost() {
        return configProperties.getProperty("db.host", DEFAULT_HOST);
    }

    /**
     * Establece el host/IP del servidor MySQL.
     *
     * @param host Nuevo host
     */
    public void setHost(String host) {
        configProperties.setProperty("db.host", host != null ? host.trim() : DEFAULT_HOST);
    }

    /**
     * Obtiene el nombre de usuario para la conexión.
     *
     * @return Nombre de usuario
     */
    public String getUsername() {
        return configProperties.getProperty("db.username", DEFAULT_USERNAME);
    }

    /**
     * Establece el nombre de usuario para la conexión.
     *
     * @param username Nuevo nombre de usuario
     */
    public void setUsername(String username) {
        configProperties.setProperty("db.username", username != null ? username.trim() : DEFAULT_USERNAME);
    }

    /**
     * Obtiene la contraseña para la conexión.
     *
     * @return Contraseña
     */
    public String getPassword() {
        return configProperties.getProperty("db.password", DEFAULT_PASSWORD);
    }

    /**
     * Establece la contraseña para la conexión.
     *
     * @param password Nueva contraseña
     */
    public void setPassword(String password) {
        configProperties.setProperty("db.password", password != null ? password : DEFAULT_PASSWORD);
    }

    /**
     * Obtiene el nombre de la base de datos.
     *
     * @return Nombre de la base de datos
     */
    public String getDatabase() {
        return configProperties.getProperty("db.database", DEFAULT_DATABASE);
    }

    /**
     * Establece el nombre de la base de datos.
     *
     * @param database Nuevo nombre de base de datos
     */
    public void setDatabase(String database) {
        configProperties.setProperty("db.database", database != null ? database.trim() : DEFAULT_DATABASE);
    }

    /**
     * Obtiene información de la configuración actual (sin contraseña).
     *
     * @return String con información de configuración
     */
    public String getConfigurationInfo() {
        return "Host: " + getHost() + "\n" +
               "Base de datos: " + getDatabase() + "\n" +
               "Usuario: " + getUsername();
    }
}