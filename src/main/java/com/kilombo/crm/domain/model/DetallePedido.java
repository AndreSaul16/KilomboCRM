package com.kilombo.crm.domain.model;

import java.math.BigDecimal;

/**
 * Modelo de dominio para DetallePedido.
 * Representa un ítem específico dentro de un pedido.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class DetallePedido {

    private Integer id;
    private Integer idPedido;
    private String tipoProducto;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private BigDecimal gananciaBruta;

    public DetallePedido() {}

    public DetallePedido(Integer idPedido, String tipoProducto, String descripcion,
                        Integer cantidad, BigDecimal costoUnitario, BigDecimal precioUnitario) {
        this.idPedido = idPedido;
        this.tipoProducto = tipoProducto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGananciaBruta() {
        return gananciaBruta;
    }

    public void setGananciaBruta(BigDecimal gananciaBruta) {
        this.gananciaBruta = gananciaBruta;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", idPedido=" + idPedido +
                ", tipoProducto='" + tipoProducto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", cantidad=" + cantidad +
                ", costoUnitario=" + costoUnitario +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", gananciaBruta=" + gananciaBruta +
                '}';
    }
}