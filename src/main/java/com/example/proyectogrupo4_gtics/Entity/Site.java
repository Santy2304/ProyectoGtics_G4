package com.example.proyectogrupo4_gtics.Entity;


import jakarta.persistence.*;


@Entity
@Table(name="site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idsite")
    private int idSite;
    private String name;

    private String address;
}
