package com.example.proyectogrupo4_gtics.Entity;


import jakarta.persistence.*;

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

    @Column(name = "prescription", nullable = false)
    private byte[] prescription;

    @ManyToOne
    @JoinColumn(name = "idpatient", nullable = false)
    private Patient idPatient;

    @ManyToOne
    @JoinColumn(name = "iddoctor")
    private Doctor idDoctor;

    @Column(name = "tracking")
    private String tracking;

    @Column(name = "approval")
    private String approval;

    @Column(name = "releaseDate")
    private LocalDate releaseDate;

    @Column(name = "recurrent")
    private Byte recurrent;

    @Column(name = "site")
    private String site;

    @Column(name = "totalamount", nullable = false, precision = 10)
    private BigDecimal totalAmount;

    @Column(name = "statePaid")
    private String statePaid;

    public String getStatePaid() {
        return statePaid;
    }

    public void setStatePaid(String statePaid) {
        this.statePaid = statePaid;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Patient getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Patient idPatient) {
        this.idPatient = idPatient;
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
}

