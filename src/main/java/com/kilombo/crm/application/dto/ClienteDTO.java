package com.kilombo.crm.application.dto;

import com.kilombo.crm.domain.model.Cliente;
import java.util.Objects;

/**
 * Data Transfer Object para Cliente.
 * Utilizado para transferir datos entre la capa de aplicación y presentación.
 * Desacopla la UI del modelo de dominio.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class ClienteDTO {
    
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    /**
     * Constructor vacío.
     */
    public ClienteDTO() {
    }
    
    /**
     * Constructor con todos los campos.
     * 
     * @param id ID del cliente
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param email Email del cliente
     * @param telefono Teléfono del cliente
     */
    public ClienteDTO(Integer id, String nombre, String apellido, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
    }
    
    /**
     * Crea un DTO desde una entidad de dominio.
     * 
     * @param cliente Entidad Cliente
     * @return ClienteDTO con los datos del cliente
     */
    public static ClienteDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClienteDTO(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getApellido(),
            cliente.getEmail(),
            cliente.getTelefono()
        );
    }
    
    /**
     * Convierte el DTO a una entidad de dominio.
     * 
     * @return Entidad Cliente
     */
    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setId(this.id);
        cliente.setNombre(this.nombre);
        cliente.setApellido(this.apellido);
        cliente.setEmail(this.email);
        cliente.setTelefono(this.telefono);
        return cliente;
    }
    
    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // Getters y Setters
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    // equals, hashCode y toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteDTO that = (ClienteDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "ClienteDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}