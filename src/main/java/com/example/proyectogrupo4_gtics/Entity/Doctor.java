package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="iddoctor")
    private int idDoctor;

    @Column(name="name")
    private String name;

    @Column(name="lastname")
    private String lastname;

    @Column(name="dni")
    private String dni;

    @Column(name="headquarter")
    private String headquarter;

    @Column(name="email")
    private String email;
}
