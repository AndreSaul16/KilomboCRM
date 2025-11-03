package com.kilombo.crm.application.service;

import com.kilombo.crm.application.dto.InformeBI_DTO;
import com.kilombo.crm.domain.repository.PedidoRepository;

import java.util.List;

/**
 * Servicio de aplicación para informes de Business Intelligence.
 * Orquesta la lógica de reportes y consultas agregadas.
 *
 * @author KilomboCRM Team
 * @version 1.0
 */
public class InformeService {

    private final PedidoRepository pedidoRepository;

    public InformeService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Obtiene los clientes más rentables por ganancia bruta.
     *
     * @return Lista de los top 5 clientes por ganancia total
     */
    public List<InformeBI_DTO> getTopRentableClients() {
        // Lógica de aplicación: se puede aplicar filtros o caché aquí
        return pedidoRepository.findTopClientsByGrossProfit(5);
    }
}