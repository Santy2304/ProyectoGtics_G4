package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="replacementorder")
public class ReplacementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idreplacementorder")
    private int idReplacementOrder;

    @Column(name="trackingstate")
    private String trackingState;

}
