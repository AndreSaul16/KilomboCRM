package com.kilombo.crm.domain.model;

import com.kilombo.crm.domain.exception.ValidationException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio que representa un Pedido.
 * Contiene las reglas de negocio y validaciones relacionadas con pedidos.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class Pedido {
    
    // Atributos
    private Integer id;
    private Integer idCliente;
    private LocalDate fecha;
    private Double total;
    private String estado;
    
    /**
     * Constructor vacío.
     */
    public Pedido() {
    }
    
    /**
     * Constructor con todos los campos excepto ID.
     *
     * @param idCliente ID del cliente asociado al pedido
     * @param fecha Fecha del pedido
     * @param total Importe total del pedido
     */
    public Pedido(Integer idCliente, LocalDate fecha, Double total) {
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = "PENDIENTE"; // Estado por defecto
        validar();
    }
    
    /**
     * Constructor completo con ID.
     *
     * @param id ID del pedido
     * @param idCliente ID del cliente asociado al pedido
     * @param fecha Fecha del pedido
     * @param total Importe total del pedido
     */
    public Pedido(Integer id, Integer idCliente, LocalDate fecha, Double total) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = "PENDIENTE"; // Estado por defecto
        validar();
    }
    
    /**
     * Valida todos los campos del pedido según las reglas de negocio.
     * 
     * @throws ValidationException si algún campo no cumple las validaciones
     */
    public void validar() {
        validarCliente();
        validarFecha();
        validarTotal();
    }
    
    /**
     * Valida que el pedido tenga un cliente asociado.
     * 
     * @throws ValidationException si el cliente no está especificado
     */
    private void validarCliente() {
        if (idCliente == null) {
            throw new ValidationException("El pedido debe estar asociado a un cliente");
        }
        if (idCliente <= 0) {
            throw new ValidationException("El ID del cliente debe ser un número positivo");
        }
    }
    
    /**
     * Valida la fecha del pedido.
     * 
     * @throws ValidationException si la fecha no es válida
     */
    private void validarFecha() {
        if (fecha == null) {
            throw new ValidationException("La fecha del pedido es obligatoria");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new ValidationException("La fecha del pedido no puede ser futura");
        }
    }
    
    /**
     * Valida el total del pedido.
     * 
     * @throws ValidationException si el total no es válido
     */
    private void validarTotal() {
        if (total == null) {
            throw new ValidationException("El total del pedido es obligatorio");
        }
        if (total <= 0) {
            throw new ValidationException("El total del pedido debe ser mayor que cero");
        }
    }
    
    // Getters y Setters
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
        validarCliente();
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        validarFecha();
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
        validarTotal();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    /**
     * Verifica si el pedido es reciente (últimos 30 días).
     * 
     * @return true si el pedido es de los últimos 30 días
     */
    public boolean esReciente() {
        return fecha != null && fecha.isAfter(LocalDate.now().minusDays(30));
    }
    
    /**
     * Obtiene el total formateado como String.
     * 
     * @return Total formateado con 2 decimales
     */
    public String getTotalFormateado() {
        return String.format("%.2f €", total);
    }
    
    // equals, hashCode y toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", fecha=" + fecha +
                ", total=" + total +
                '}';
    }
}