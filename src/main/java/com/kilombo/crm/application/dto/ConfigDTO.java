package com.kilombo.crm.application.dto;

import java.io.Serializable;

/**
 * DTO para configuración de base de datos.
 * Contiene los parámetros necesarios para establecer conexión con MySQL.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;
    private String username;
    private String password;
    private String database;

    /**
     * Constructor por defecto.
     */
    public ConfigDTO() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param host Host/IP del servidor
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param database Nombre de la base de datos
     */
    public ConfigDTO(String host, String username, String password, String database) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    /**
     * Obtiene el host/IP del servidor.
     *
     * @return Host del servidor
     */
    public String getHost() {
        return host;
    }

    /**
     * Establece el host/IP del servidor.
     *
     * @param host Nuevo host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param username Nuevo nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la contraseña.
     *
     * @return Contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña.
     *
     * @param password Nueva contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el nombre de la base de datos.
     *
     * @return Nombre de la base de datos
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Establece el nombre de la base de datos.
     *
     * @param database Nuevo nombre de base de datos
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String toString() {
        return "ConfigDTO{" +
                "host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", database='" + database + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ConfigDTO configDTO = (ConfigDTO) obj;

        if (host != null ? !host.equals(configDTO.host) : configDTO.host != null) return false;
        if (username != null ? !username.equals(configDTO.username) : configDTO.username != null) return false;
        if (password != null ? !password.equals(configDTO.password) : configDTO.password != null) return false;
        return database != null ? database.equals(configDTO.database) : configDTO.database == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (database != null ? database.hashCode() : 0);
        return result;
    }
}