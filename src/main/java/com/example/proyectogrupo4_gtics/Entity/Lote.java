package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.util.Date;

public class Lote{


    @Getter
    @Id
    @Column(name = "idlote")
    private int idLote;

    @Getter
    private String site;

    @Getter
    @Column(name = "expiredate")
    private Date expireDate;

    @Getter
    @Column(name = "initialstock")
    private int initialStock;

    @Getter
    private boolean expire;

    @Getter
    private int stock;

    @ManyToOne
    @JoinColumn(name="idpedidosreposicion")
    private ReplacementOrder replacementOrder;

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
