package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="saleorder")
public class SaleOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="saleorder", nullable = false)
    private int saleOrderId;

    @ManyToOne
    @JoinColumn(name="iddoctor")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name="idpharmacist")
    private Pharmacist farmacista;

    @ManyToOne
    @JoinColumn(name="idpatient")
    private Patient paciente;

    @Column(name="paidType", nullable = false)
    private String tipoPago;

    @Column(name="saleDate", nullable = false)
    private Date fechaVenta;

    @Column(name="site", nullable = false)
    private String sede;

    @Column(name="invalidated", nullable = false)
    private Boolean invalido;

    @Column(name="reason")
    private String razonInvalido;

    public int getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(int saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Pharmacist getFarmacista() {
        return farmacista;
    }

    public void setFarmacista(Pharmacist farmacista) {
        this.farmacista = farmacista;
    }

    public Patient getPaciente() {
        return paciente;
    }

    public void setPaciente(Patient paciente) {
        this.paciente = paciente;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public Boolean getInvalido() {
        return invalido;
    }

    public void setInvalido(Boolean invalido) {
        this.invalido = invalido;
    }

    public String getRazonInvalido() {
        return razonInvalido;
    }

    public void setRazonInvalido(String razonInvalido) {
        this.razonInvalido = razonInvalido;
    }
}
