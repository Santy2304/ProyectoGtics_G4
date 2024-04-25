package com.example.proyectogrupo4_gtics.Entity;


import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name="creditcart")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idcreditcart")
    private int idCreditCart;

    @Column(name="numbercard")
    private int numberCard;

    @Column(name="expiredate")
    private Date expiredate;

    private String cvv;

    @ManyToOne
    @JoinColumn(name="idpatient") //*Checar estooo]
    private Patient patient;

    private Boolean prefered;

    private String bank;
}
