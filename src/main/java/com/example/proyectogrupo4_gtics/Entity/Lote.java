package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

import java.util.Date;
@Entity
@Table(name="lote")
public class Lote{


    @Id
    @Column(name = "idlote")
    private int idLote;

    private String site;

    @Column(name = "expiredate")
    private Date expireDate;


    @Column(name = "initialstock")
    private int initialStock;

    private boolean expire;

    private int stock;

    @ManyToOne
    @JoinColumn(name="idpedidosreposicion")
    private ReplacementOrder replacementOrder;

    @ManyToOne
    @JoinColumn(name="idmedicine")
    private Medicine medicine;

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
