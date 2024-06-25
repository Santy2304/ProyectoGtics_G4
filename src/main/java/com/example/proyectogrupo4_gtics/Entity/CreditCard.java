package com.example.proyectogrupo4_gtics.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name="creditcard")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcreditcard", nullable = false)
    private Integer idCredit;

    @Size(max = 45)
    @NotNull
    @Column(name = "numbercard", nullable = false, length = 45)
    private String numberCard;


    @Size(max = 3)
    @NotNull
    @Column(name = "cvv", nullable = false, length = 45)
    private String cvv;

    @ManyToOne
    @JoinColumn(name = "idpatient")
    private Patient idPatient;

    @Column(name = "prefered")
    private Boolean prefered;

    @Size(max = 45)
    @Column(name = "bank", nullable = false, length = 45)
    private String bank;

    @Column(name = "expiremonth")
    @Positive
    private Integer expireMonth;

    @Column(name = "expireyear")
    @Positive
    private Integer expireYear;

    public Integer getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(Integer expireYear) {
        this.expireYear = expireYear;
    }

    public Integer getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(Integer expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Boolean getPrefered() {
        return prefered;
    }

    public void setPrefered(Boolean prefered) {
        this.prefered = prefered;
    }

    public Patient getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Patient idPatient) {
        this.idPatient = idPatient;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }


    public String getNumberCard() {
        return numberCard;
    }

    public void setNumberCard(String numberCard) {
        this.numberCard = numberCard;
    }

    public Integer getIdCredit() {
        return idCredit;
    }

    public void setIdCredit(Integer idCredit) {
        this.idCredit = idCredit;
    }
}
