package com.example.proyectogrupo4_gtics.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "purchaseorder")
public class PurchaseOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpurchaseorder", nullable = false)
    private Integer id;


    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "deliveryhour", nullable = false)
    private LocalTime deliveryHour;

    @Column(name = "prescription")
    private byte[] prescription;

    @ManyToOne
    @JoinColumn(name = "idpatient", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "iddoctor")
    private Doctor idDoctor;

    @Column(name = "tracking")
    private String tracking;

    @Column(name = "approval")
    private String approval;

    @Column(name = "releasedate")
    private LocalDate releaseDate;

    @Column(name = "recurrent")
    private Byte recurrent;

    @Column(name = "site")
    private String site;

    @Column(name = "statepaid")
    private String statePaid;

    private String direccion;

    private String tipo;

    @Column(name="tipopago")
    private String tipoPago;

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getStatePaid() {
        return statePaid;
    }

    public void setStatePaid(String statePaid) {
        this.statePaid = statePaid;
    }


    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Byte getRecurrent() {
        return recurrent;
    }

    public void setRecurrent(Byte recurrent) {
        this.recurrent = recurrent;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public Doctor getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(Doctor idDoctor) {
        this.idDoctor = idDoctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public byte[] getPrescription() {
        return prescription;
    }

    public void setPrescription(byte[] prescription) {
        this.prescription = prescription;
    }

    public LocalTime getDeliveryHour() {
        return deliveryHour;
    }

    public void setDeliveryHour(LocalTime deliveryHour) {
        this.deliveryHour = deliveryHour;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

