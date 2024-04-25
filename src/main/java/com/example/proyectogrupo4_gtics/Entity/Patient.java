package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name="patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idpatient")
    private int idPatient;

    private String name;

    private String lastName;

    private String dni;

    private String location;

    private String email;

    private String distrit;

    private String insurance;

    private String password;

    @Column(name="changepassword")
    private String changePassword;

    private String photo;

    @Column(name="datecreationaccount")
    private Date dateCreationAccount;

}
