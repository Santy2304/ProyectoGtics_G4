package com.example.proyectogrupo4_gtics.Entity;
import com.mysql.cj.jdbc.Blob;
import jakarta.persistence.*;



@Entity
@Table(name="administrator")
public class Administrator{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="idadministrador")
    private int idAdministrador;

    @Column(name="name")
    private String name;

    @Column(name="lastname")
    private String lastName;

    @Column(name="dni")
    private String dni;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="banned")
    private Boolean banned;

    @Column(name="photo")
    private String photo; /*Persistence no me deja poner Blob*/

}
