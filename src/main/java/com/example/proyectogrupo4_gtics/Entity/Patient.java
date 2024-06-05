package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name="patient")
public class Patient implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idpatient")
    private int idPatient;

    @Column(name="name")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "El nombre no debe superar los 45 carácteres")
    private String name;

    @Column(name="lastname")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "El apellido no debe superar los 45 carácteres")
    private String lastName;

    @NotBlank(message = "Este campo es obligatorio")
    @Digits(integer = 8, fraction = 0, message = "El DNI debe ser un número")
    @Size(min = 8, max = 8, message = "El DNI tiene que tener 8 dígitos")
    private String dni;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "La dirección no debe superar los 45 carácteres")
    private String location;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "El correo no debe superar los 45 carácteres")
    private String email;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "El distrito no debe superar los 45 carácteres")
    private String distrit;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "El seguro no debe superar los 45 carácteres")
    private String insurance;



    @Column(name="changepassword")
    private Boolean changePassword;

    private String photo;

    @Column(name = "datecreationaccount", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreationAccount;


    private String state;

    @Column(name = "expirationdate")
    private LocalDateTime expirationDate;

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

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


    public Boolean getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(Boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getDateCreationAccount() {
        return dateCreationAccount;
    }

    public void setDateCreationAccount(LocalDate dateCreationAccount) {
        this.dateCreationAccount = dateCreationAccount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
