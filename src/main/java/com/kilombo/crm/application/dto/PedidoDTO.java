package com.kilombo.crm.application.dto;

import com.kilombo.crm.domain.model.Pedido;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Data Transfer Object para Pedido.
 * Utilizado para transferir datos entre la capa de aplicación y presentación.
 * Desacopla la UI del modelo de dominio.
 * 
 * @author KilomboCRM Team
 * @version 1.0
 */
public class PedidoDTO {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private Integer id;
    private Integer idCliente;
    private LocalDate fecha;
    private Double total;
    
    // Campos adicionales para la UI
    private String nombreCliente;
    
    /**
     * Constructor vacío.
     */
    public PedidoDTO() {
    }
    
    /**
     * Constructor con todos los campos.
     * 
     * @param id ID del pedido
     * @param idCliente ID del cliente
     * @param fecha Fecha del pedido
     * @param total Total del pedido
     */
    public PedidoDTO(Integer id, Integer idCliente, LocalDate fecha, Double total) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
    }
    
    /**
     * Crea un DTO desde una entidad de dominio.
     * 
     * @param pedido Entidad Pedido
     * @return PedidoDTO con los datos del pedido
     */
    public static PedidoDTO fromEntity(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        return new PedidoDTO(
            pedido.getId(),
            pedido.getIdCliente(),
            pedido.getFecha(),
            pedido.getTotal()
        );
    }
    
    /**
     * Convierte el DTO a una entidad de dominio.
     * 
     * @return Entidad Pedido
     */
    public Pedido toEntity() {
        Pedido pedido = new Pedido();
        pedido.setId(this.id);
        pedido.setIdCliente(this.idCliente);
        pedido.setFecha(this.fecha);
        pedido.setTotal(this.total);
        return pedido;
    }
    
    /**
     * Obtiene la fecha formateada como String.
     * 
     * @return Fecha formateada (dd/MM/yyyy)
     */
    public String getFechaFormateada() {
        return fecha != null ? fecha.format(DATE_FORMATTER) : "";
    }
    
    /**
     * Obtiene el total formateado como String.
     * 
     * @return Total formateado con 2 decimales y símbolo de euro
     */
    public String getTotalFormateado() {
        return total != null ? String.format("%.2f €", total) : "0.00 €";
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
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    // equals, hashCode y toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoDTO that = (PedidoDTO) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "PedidoDTO{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", fecha=" + fecha +
                ", total=" + total +
                ", nombreCliente='" + nombreCliente + '\'' +
                '}';
    }
}