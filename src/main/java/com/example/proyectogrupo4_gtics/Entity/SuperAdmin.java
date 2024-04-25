package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;



@Entity
@Table(name="superadmin")
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idsuperadmin")
    private int idSuperAdmin;

    @Column(name="email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name="photo")
    private String photo;

}
