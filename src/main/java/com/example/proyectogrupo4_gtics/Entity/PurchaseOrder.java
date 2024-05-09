package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Timer;

@Entity
@Table(name="purchaseorder")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpurchaseorder", nullable = false)
    private int idOrdenCompra;

    @Column(name = "phoneNumber")
    private String numeroTelefono;

    @Column(name = "deliveryHour")
    private Date horaDelivery;

    @Column(name = "prescription")
    private String prescripcion;

    @ManyToOne
    @JoinColumn(name="idpatient",nullable = false)
    private Patient paciente;

    @ManyToOne
    @JoinColumn(name="iddoctor",nullable = false)
    private Doctor doctor;

    @Column(name="tracking")
    private String tracking;

    @Column(name="approval")
    private String aprovado;

    @Column(name="releaseDate", nullable = false)
    private Date fechaRelease;

    @Column(name="recurrent")
    private Boolean recurrente;

    @Column(name="statePaid")
    private String estadoPago;

    @Column(name="tipo")
    private String tipo;

    @Column(name="direccion")
    private String direccion;

    @Column(name="tipoPago")
    private String tipoPago;

    public int getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(int idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public Date getHoraDelivery() {
        return horaDelivery;
    }

    public void setHoraDelivery(Date horaDelivery) {
        this.horaDelivery = horaDelivery;
    }

    public String getPrescripcion() {
        return prescripcion;
    }

    public void setPrescripcion(String prescripcion) {
        this.prescripcion = prescripcion;
    }

    public Patient getPaciente() {
        return paciente;
    }

    public void setPaciente(Patient paciente) {
        this.paciente = paciente;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getAprovado() {
        return aprovado;
    }

    public void setAprovado(String aprovado) {
        this.aprovado = aprovado;
    }

    public Date getFechaRelease() {
        return fechaRelease;
    }

    public void setFechaRelease(Date fechaRelease) {
        this.fechaRelease = fechaRelease;
    }

    public Boolean getRecurrente() {
        return recurrente;
    }

    public void setRecurrente(Boolean recurrente) {
        this.recurrente = recurrente;
    }


    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
