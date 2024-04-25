package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

import java.util.Date;
@Entity
@Table(name="lote")
public class Lote{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlote")
    private int idLote;

    private String site;

    @Column(name = "expiredate")
    private Date expireDate;

    private boolean expire;

    private int stock;

    private boolean visible;
    @ManyToOne
    @JoinColumn(name="idpedidosreposicion")
    private ReplacementOrder replacementOrder;

    @ManyToOne
    @JoinColumn(name="idmedicine")
    private Medicine medicine;

    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isExpire() {
        return expire;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ReplacementOrder getReplacementOrder() {
        return replacementOrder;
    }

    public void setReplacementOrder(ReplacementOrder replacementOrder) {
        this.replacementOrder = replacementOrder;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
