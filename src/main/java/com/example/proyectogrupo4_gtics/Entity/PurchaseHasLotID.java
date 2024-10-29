package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class PurchaseHasLotID implements Serializable {
    @Column(name = "idpurchase", nullable = false)
    private Integer idPurchase;


    @Column(name = "idlote", nullable = false)
    private Integer idLote;


    public Integer getIdPurchase() {
        return idPurchase;
    }

    public void setIdPurchase(Integer idPurchase) {
        this.idPurchase = idPurchase;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }
}
