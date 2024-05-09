package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name="pharmacist")
public class Pharmacist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpharmacist")
    @NotNull
    @Digits(integer = 8, fraction = 0)
    @PositiveOrZero
    private int idFarmacista;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El nombre no debe superar los 45 carácteres")
    private String name;

    @Column(name = "lastname")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El apellido no debe superar los 45 carácteres")
    private String lastName;

    @Column(name="site")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "La sede no debe superar los 45 carácteres")
    private String site;

    @Column(unique = true)
    @NotBlank(message = "Este campo es obligatorio")
    @Digits(integer = 8, fraction = 0, message = "El DNI debe ser un número y tener 8 dígitos")
    private String dni;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El distrito no debe superar los 45 carácteres")
    private String distrit;

    private String code;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El correo electrónico no debe superar los 45 carácteres")
    @Email(message = "Se debe ingresar un correo electrónico")
    private String email;

    private String password;

    @Column(name = "approvalstate")
    private String approvalState;
    @Column(name = "rejectedreason")
    private String rejectedReason;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El estado no debe superar los 45 carácteres")
    private String state;

    private String photo;


    @Column(name = "datecreationaccount")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;


    @Column(name = "daterequestaccount")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;

    public int getIdFarmacista() {
        return idFarmacista;
    }

    public void setIdFarmacista(int idFarmacista) {
        this.idFarmacista = idFarmacista;
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


    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDistrit() {
        return distrit;
    }

    public void setDistrit(String distrit) {
        this.distrit = distrit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getApprovalState() {
        return approvalState;
    }

    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
}
