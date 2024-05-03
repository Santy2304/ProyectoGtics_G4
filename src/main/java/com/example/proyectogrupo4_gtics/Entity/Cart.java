package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="cart")
public class Cart {
    @MapsId("patient_idPatient")
    private int patient_idPatient;
    @MapsId("medicine_idMedicine")
    private int medicine_idMedicine;

    private int quantity;


}
