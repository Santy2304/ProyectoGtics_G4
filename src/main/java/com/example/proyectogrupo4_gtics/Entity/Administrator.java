package com.example.proyectogrupo4_gtics.Entity;
import com.mysql.cj.jdbc.Blob;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;


@Entity
@Table(name="administrator")
public class Administrator{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="idadministrator")
    private int idAdministrador;

    @Column(name="name")
    private String name;

    @Column(name="lastname")
    private String lastName;

    @Column(name="dni")
    private String dni;

    @Column(name = "site")
    private String site;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="banned")
    private Boolean banned;

    @Column(name="photo")
    private String photo; /*Persistence no me deja poner Blob*/

    @Column(name = "datecreationaccount", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creationDate;

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
