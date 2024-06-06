package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "carrito", schema = "proyectogtics")
public class Carrito {
    @Id
    @Column(name = "idcarrito", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPatient", nullable = false)
    private Patient idPatient;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idMedicine", nullable = false)
    private Medicine idMedicine;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patient getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Patient idPatient) {
        this.idPatient = idPatient;
    }

    public Medicine getIdMedicine() {
        return idMedicine;
    }

    public void setIdMedicine(Medicine idMedicine) {
        this.idMedicine = idMedicine;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}