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

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
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
}
