package com.example.proyectogrupo4_gtics.DTOs;

import java.time.LocalDate;
import java.util.Date;

public interface PurchaseOrderPorSedeDTO {
    int getIdPurchaseOrder();
    String getNumero();
    String getNombrePaciente();
    String getNombreDoctor();
    String getPrescripcion();
    String getTracking();
    String getAprobado();
    Date getFechaRelease();
    Double getMonto();
    String getEstadoPago();
    String getTipoCompra();
    String getTipoPago();

    LocalDate getFecha();

}
