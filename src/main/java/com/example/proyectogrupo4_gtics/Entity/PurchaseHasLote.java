package com.example.proyectogrupo4_gtics.Entity;


import jakarta.persistence.*;

@Entity
@Table(name = "purchasehaslot")
public class PurchaseHasLote {

    @EmbeddedId
    private PurchaseHasLotID id;

    @MapsId("idPurchase")
    @ManyToOne
    @JoinColumn(name = "idpurchase")
    private PurchaseOrder purchaseOrder;

    @MapsId("idLote")
    @ManyToOne
    @JoinColumn(name = "idlote")
    private Lote lote;


    @Column(name = "cantidad_comprar")
    private int cantidadComprar;

    public PurchaseHasLotID getId() {
        return id;
    }

    public void setId(PurchaseHasLotID id) {
        this.id = id;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public int getCantidadComprar() {
        return cantidadComprar;
    }

    public void setCantidadComprar(int cantidadComprar) {
        this.cantidadComprar = cantidadComprar;
    }
}
