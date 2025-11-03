package com.kilombo.crm.application.service;

import com.kilombo.crm.application.dto.ConfigDTO;
import com.kilombo.crm.domain.exception.DatabaseException;
import com.kilombo.crm.infrastructure.database.ConexionBD;
import com.kilombo.crm.infrastructure.database.ConfigurationManager;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de configuración para gestión dinámica de la conexión a base de datos.
 * Maneja la persistencia y validación de la configuración del usuario.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ConfigService {

    private static final Logger logger = Logger.getLogger(ConfigService.class.getName());
    private static final String CONFIG_FILE = "database_config.dat";

    private ConfigurationManager configManager;
    private ConexionBD conexionBD;

    /**
     * Constructor del servicio de configuración.
     */
    public ConfigService() {
        this.configManager = ConfigurationManager.getInstance();
        this.conexionBD = ConexionBD.getInstance();
    }

    /**
     * Carga la configuración guardada del usuario.
     * Si no existe, retorna configuración por defecto.
     *
     * @return ConfigDTO con la configuración cargada
     */
    public ConfigDTO loadConfiguration() {
        ConfigDTO config = new ConfigDTO();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CONFIG_FILE))) {
            config = (ConfigDTO) ois.readObject();
            logger.info("Configuración cargada desde " + CONFIG_FILE);

            // Aplicar configuración cargada
            applyConfiguration(config);

        } catch (FileNotFoundException e) {
            logger.info("Archivo de configuración no encontrado, usando valores por defecto");
            config = getDefaultConfiguration();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING, "Error al cargar configuración, usando valores por defecto", e);
            config = getDefaultConfiguration();
        }

        return config;
    }

    /**
     * Guarda la configuración del usuario.
     *
     * @param config Configuración a guardar
     * @throws DatabaseException si ocurre un error al guardar
     */
    public void saveConfiguration(ConfigDTO config) throws DatabaseException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(config);
            logger.info("Configuración guardada en " + CONFIG_FILE);

            // Aplicar configuración guardada
            applyConfiguration(config);

        } catch (IOException e) {
            throw new DatabaseException("Error al guardar la configuración: " + e.getMessage(), e);
        }
    }

    /**
     * Prueba la conexión con la configuración proporcionada.
     *
     * @param config Configuración a probar
     * @return true si la conexión es exitosa
     */
    public boolean testConnection(ConfigDTO config) {
        try {
            return conexionBD.testConnectionWithConfig(
                config.getHost(),
                config.getUsername(),
                config.getPassword()
            ).isSuccess();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al probar conexión", e);
            return false;
        }
    }

    /**
     * Aplica la configuración al sistema.
     *
     * @param config Configuración a aplicar
     */
    private void applyConfiguration(ConfigDTO config) {
        configManager.setHost(config.getHost());
        configManager.setUsername(config.getUsername());
        configManager.setPassword(config.getPassword());
        configManager.setDatabase(config.getDatabase());

        // Actualizar conexión existente
        conexionBD.refreshConfiguration();
    }

    /**
     * Obtiene la configuración por defecto.
     *
     * @return ConfigDTO con valores por defecto
     */
    public ConfigDTO getDefaultConfiguration() {
        ConfigDTO config = new ConfigDTO();
        config.setHost("localhost");
        config.setUsername("admin");
        config.setPassword("admin");
        config.setDatabase("kilombo");
        return config;
    }

    /**
     * Obtiene la configuración actual del sistema.
     *
     * @return ConfigDTO con la configuración actual
     */
    public ConfigDTO getCurrentConfiguration() {
        ConfigDTO config = new ConfigDTO();
        config.setHost(configManager.getHost());
        config.setUsername(configManager.getUsername());
        config.setPassword(configManager.getPassword());
        config.setDatabase(configManager.getDatabase());
        return config;
    }

    /**
     * Verifica si existe una configuración guardada.
     *
     * @return true si existe configuración guardada
     */
    public boolean hasSavedConfiguration() {
        return new File(CONFIG_FILE).exists();
    }

    /**
     * Restaura la configuración por defecto.
     */
    public void restoreDefaultConfiguration() {
        ConfigDTO defaultConfig = getDefaultConfiguration();
        applyConfiguration(defaultConfig);

        // Eliminar archivo de configuración guardada
        try {
            new File(CONFIG_FILE).delete();
            logger.info("Configuración por defecto restaurada");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al eliminar archivo de configuración", e);
        }
    }
}