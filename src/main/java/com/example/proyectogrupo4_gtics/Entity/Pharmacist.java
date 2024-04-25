package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;



@Entity
@Table(name="pharmacist")
public class Pharmacist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpharmacist")
    private int idFarmacista;

    private String name;

    private String lastName;

    @Column(name="site")
    private String sede;

    private String dni;

    private String distrit;

    private String code;

    private String email;

    private String password;

    private String approvalState;

    private String rejectedReason;

    private Boolean banned;

    private String photo;

}
