package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="purchaseOrder")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpharmacist")
    private int idPurchaseOrder;



}
