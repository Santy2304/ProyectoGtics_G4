package com.example.proyectogrupo4_gtics.DTOs;

import java.time.LocalDate;

public interface PurchasePorPatientDTO {
    int getIdPurchaseOrder();
    Double getTotal_price();
    String getEstado();
    String getEstadoPago();
    LocalDate getFecha();

}
