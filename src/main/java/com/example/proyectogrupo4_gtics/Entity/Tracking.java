package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "Trackings")
public class Tracking {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="idtrackings")
    private int idtracking;


    @Column(name="solicituddate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime solicitudDate;

    @Column(name="enprocesodate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime enProcesoDate;

    @Column(name="empaquetadodate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime empaquetadoDate;


    @Column(name="enrutadate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime enRutaDate;

    @Column(name="entregadodate")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime entregadoDate;

    public int getIdtracking() {
        return idtracking;
    }

    public void setIdtracking(int idtracking) {
        this.idtracking = idtracking;
    }

    public LocalDateTime getSolicitudDate() {
        return solicitudDate;
    }

    public void setSolicitudDate(LocalDateTime solicitudDate) {
        this.solicitudDate = solicitudDate;
    }

    public LocalDateTime getEnProcesoDate() {
        return enProcesoDate;
    }

    public void setEnProcesoDate(LocalDateTime enProcesoDate) {
        this.enProcesoDate = enProcesoDate;
    }

    public LocalDateTime getEmpaquetadoDate() {
        return empaquetadoDate;
    }

    public void setEmpaquetadoDate(LocalDateTime empaquetadoDate) {
        this.empaquetadoDate = empaquetadoDate;
    }

    public LocalDateTime getEnRutaDate() {
        return enRutaDate;
    }

    public void setEnRutaDate(LocalDateTime enRutaDate) {
        this.enRutaDate = enRutaDate;
    }

    public LocalDateTime getEntregadoDate() {
        return entregadoDate;
    }

    public void setEntregadoDate(LocalDateTime entregadoDate) {
        this.entregadoDate = entregadoDate;
    }
}
