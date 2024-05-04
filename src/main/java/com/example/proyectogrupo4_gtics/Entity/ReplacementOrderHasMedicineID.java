package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.Column;

import java.io.Serializable;

public class ReplacementOrderHasMedicineID implements Serializable {
    @Column(name = "idReplacementOrder", nullable = false)
    private int idReplacementOrder;

    @Column(name="idMedicine", nullable = false)
    private int idMedicine;

    public int getIdReplacementOrder() {
        return idReplacementOrder;
    }

    public void setIdReplacementOrder(int idReplacementOrder) {
        this.idReplacementOrder = idReplacementOrder;
    }

    public int getIdMedicine() {
        return idMedicine;
    }

    public void setIdMedicine(int idMedicine) {
        this.idMedicine = idMedicine;
    }
}
