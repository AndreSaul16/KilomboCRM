package com.kilombo.crm.application.dto;

public class InformeBI_DTO {
    private String nombreCliente;
    private double gananciaTotal;

    public InformeBI_DTO() {}

    public InformeBI_DTO(String nombreCliente, double gananciaTotal) {
        this.nombreCliente = nombreCliente;
        this.gananciaTotal = gananciaTotal;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public double getGananciaTotal() {
        return gananciaTotal;
    }

    public void setGananciaTotal(double gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }
}