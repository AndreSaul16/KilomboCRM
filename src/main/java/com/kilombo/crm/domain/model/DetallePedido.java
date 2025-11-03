package com.kilombo.crm.domain.model;

import com.kilombo.crm.domain.exception.ValidationException;

import java.math.BigDecimal;

/**
 * Modelo de dominio para DetallePedido.
 * Representa un ítem específico dentro de un pedido.
 * Incluye validaciones de negocio para garantizar integridad de datos.
 *
 * @author KilomboCRM Team
 * @version 2.0
 */
public class DetallePedido {

    // Constantes para validación
    private static final int MAX_TIPO_PRODUCTO_LENGTH = 100;
    private static final int MAX_DESCRIPCION_LENGTH = 500;
    private static final BigDecimal MIN_COSTO_PRECIO = BigDecimal.ZERO;
    private static final int MIN_CANTIDAD = 1;
    private static final int MAX_CANTIDAD = 9999;

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
        validar();
    }

    /**
     * Valida todos los campos del detalle de pedido según las reglas de negocio.
     *
     * @throws ValidationException si algún campo no cumple las validaciones
     */
    public void validar() {
        validarIdPedido();
        validarTipoProducto();
        validarDescripcion();
        validarCantidad();
        validarCostoUnitario();
        validarPrecioUnitario();
        validarPreciosConsistentes();
    }

    /**
     * Valida que el detalle esté asociado a un pedido válido.
     *
     * @throws ValidationException si el ID del pedido no es válido
     */
    private void validarIdPedido() {
        if (idPedido == null || idPedido <= 0) {
            throw new ValidationException("El detalle debe estar asociado a un pedido válido");
        }
    }

    /**
     * Valida el tipo de producto.
     *
     * @throws ValidationException si el tipo de producto no es válido
     */
    private void validarTipoProducto() {
        if (tipoProducto == null || tipoProducto.trim().isEmpty()) {
            throw new ValidationException("El tipo de producto es obligatorio");
        }
        if (tipoProducto.length() > MAX_TIPO_PRODUCTO_LENGTH) {
            throw new ValidationException(
                "El tipo de producto no puede exceder " + MAX_TIPO_PRODUCTO_LENGTH + " caracteres"
            );
        }
    }

    /**
     * Valida la descripción del producto.
     *
     * @throws ValidationException si la descripción no es válida
     */
    private void validarDescripcion() {
        if (descripcion != null && descripcion.length() > MAX_DESCRIPCION_LENGTH) {
            throw new ValidationException(
                "La descripción no puede exceder " + MAX_DESCRIPCION_LENGTH + " caracteres"
            );
        }
    }

    /**
     * Valida la cantidad del producto.
     *
     * @throws ValidationException si la cantidad no es válida
     */
    private void validarCantidad() {
        if (cantidad == null || cantidad < MIN_CANTIDAD) {
            throw new ValidationException("La cantidad debe ser al menos " + MIN_CANTIDAD);
        }
        if (cantidad > MAX_CANTIDAD) {
            throw new ValidationException("La cantidad no puede exceder " + MAX_CANTIDAD);
        }
    }

    /**
     * Valida el costo unitario.
     *
     * @throws ValidationException si el costo unitario no es válido
     */
    private void validarCostoUnitario() {
        if (costoUnitario == null || costoUnitario.compareTo(MIN_COSTO_PRECIO) < 0) {
            throw new ValidationException("El costo unitario debe ser mayor o igual a cero");
        }
    }

    /**
     * Valida el precio unitario.
     *
     * @throws ValidationException si el precio unitario no es válido
     */
    private void validarPrecioUnitario() {
        if (precioUnitario == null || precioUnitario.compareTo(MIN_COSTO_PRECIO) < 0) {
            throw new ValidationException("El precio unitario debe ser mayor o igual a cero");
        }
    }

    /**
     * Valida que los precios sean consistentes (precio >= costo).
     *
     * @throws ValidationException si los precios no son consistentes
     */
    private void validarPreciosConsistentes() {
        if (costoUnitario != null && precioUnitario != null &&
            precioUnitario.compareTo(costoUnitario) < 0) {
            throw new ValidationException("El precio unitario no puede ser menor al costo unitario");
        }
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
        validarIdPedido();
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
        validarTipoProducto();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        validarDescripcion();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        validarCantidad();
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
        validarCostoUnitario();
        validarPreciosConsistentes();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        validarPrecioUnitario();
        validarPreciosConsistentes();
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

    /**
     * Calcula el subtotal del detalle (cantidad * precio unitario).
     *
     * @return El subtotal calculado
     */
    public BigDecimal calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            return this.subtotal;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula la ganancia bruta del detalle ((precio - costo) * cantidad).
     *
     * @return La ganancia bruta calculada
     */
    public BigDecimal calcularGananciaBruta() {
        if (cantidad != null && precioUnitario != null && costoUnitario != null) {
            BigDecimal gananciaUnitaria = precioUnitario.subtract(costoUnitario);
            this.gananciaBruta = gananciaUnitaria.multiply(BigDecimal.valueOf(cantidad));
            return this.gananciaBruta;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula tanto el subtotal como la ganancia bruta.
     */
    public void calcularTotales() {
        calcularSubtotal();
        calcularGananciaBruta();
    }

    /**
     * Verifica si el detalle tiene ganancia (precio > costo).
     *
     * @return true si hay ganancia, false en caso contrario
     */
    public boolean tieneGanancia() {
        return precioUnitario != null && costoUnitario != null &&
               precioUnitario.compareTo(costoUnitario) > 0;
    }

    /**
     * Obtiene el margen de ganancia como porcentaje.
     *
     * @return El margen de ganancia (0-100), o 0 si no hay precio o costo
     */
    public BigDecimal getMargenGanancia() {
        if (precioUnitario != null && costoUnitario != null &&
            costoUnitario.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = precioUnitario.subtract(costoUnitario)
                    .divide(costoUnitario, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            return margen;
        }
        return BigDecimal.ZERO;
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