package com.kilombo.crm.domain.model;

import com.kilombo.crm.domain.exception.ValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidad de dominio que representa un Cliente.
 * Contiene las reglas de negocio y validaciones relacionadas con clientes.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class Cliente {
    
    // Constantes para validación
    private static final int MAX_NOMBRE_LENGTH = 100;
    private static final int MAX_APELLIDO_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 150;
    private static final int MAX_TELEFONO_LENGTH = 20;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s-]{9,20}$"
    );
    
    // Atributos
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    /**
     * Constructor vacío.
     */
    public Cliente() {
    }
    
    /**
     * Constructor con todos los campos excepto ID.
     * 
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param email Email del cliente
     * @param telefono Teléfono del cliente
     */
    public Cliente(String nombre, String apellido, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        validar();
    }
    
    /**
     * Constructor completo con ID.
     * 
     * @param id ID del cliente
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param email Email del cliente
     * @param telefono Teléfono del cliente
     */
    public Cliente(Integer id, String nombre, String apellido, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        validar();
    }
    
    /**
     * Valida todos los campos del cliente según las reglas de negocio.
     * 
     * @throws ValidationException si algún campo no cumple las validaciones
     */
    public void validar() {
        validarNombre();
        validarApellido();
        validarEmail();
        validarTelefono();
    }
    
    /**
     * Valida el nombre del cliente.
     * 
     * @throws ValidationException si el nombre no es válido
     */
    private void validarNombre() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre es obligatorio");
        }
        if (nombre.length() > MAX_NOMBRE_LENGTH) {
            throw new ValidationException(
                "El nombre no puede exceder " + MAX_NOMBRE_LENGTH + " caracteres"
            );
        }
    }
    
    /**
     * Valida el apellido del cliente.
     * 
     * @throws ValidationException si el apellido no es válido
     */
    private void validarApellido() {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ValidationException("El apellido es obligatorio");
        }
        if (apellido.length() > MAX_APELLIDO_LENGTH) {
            throw new ValidationException(
                "El apellido no puede exceder " + MAX_APELLIDO_LENGTH + " caracteres"
            );
        }
    }
    
    /**
     * Valida el email del cliente.
     * 
     * @throws ValidationException si el email no es válido
     */
    private void validarEmail() {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("El email es obligatorio");
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException(
                "El email no puede exceder " + MAX_EMAIL_LENGTH + " caracteres"
            );
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("El formato del email no es válido");
        }
    }
    
    /**
     * Valida el teléfono del cliente.
     * 
     * @throws ValidationException si el teléfono no es válido
     */
    private void validarTelefono() {
        if (telefono != null && !telefono.trim().isEmpty()) {
            if (telefono.length() > MAX_TELEFONO_LENGTH) {
                throw new ValidationException(
                    "El teléfono no puede exceder " + MAX_TELEFONO_LENGTH + " caracteres"
                );
            }
            if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
                throw new ValidationException("El formato del teléfono no es válido");
            }
        }
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
        validarNombre();
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
        validarApellido();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        validarEmail();
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
        validarTelefono();
    }
    
    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return Nombre completo (nombre + apellido)
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // equals, hashCode y toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) &&
               Objects.equals(email, cliente.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}