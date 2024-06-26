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
    @Column(name="lastname")
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

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrit() {
        return distrit;
    }

    public void setDistrit(String distrit) {
        this.distrit = distrit;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getDateCreationAccount() {
        return dateCreationAccount;
    }

    public void setDateCreationAccount(Date dateCreationAccount) {
        this.dateCreationAccount = dateCreationAccount;
    }
}
