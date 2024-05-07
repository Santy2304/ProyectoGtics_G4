package com.example.proyectogrupo4_gtics.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.sql.Date;
import java.util.List;
@Entity
@Table(name="medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmedicine")
    @NotNull
    @Digits(integer = 10, fraction = 0)
    private int idMedicine;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El nombre no debe tener más de 45 carácteres")
    private String name;

    @Column(name = "category", nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    private String category;

    @NotNull(message = "Este campo es obligatorio")
    @Digits(integer = 10, fraction = 4, message = "Se debe ingresar un número")
    @Positive(message = "El precio debe ser un valor mayor a cero")
    private double price;

    @Column(name = "timessaled")
    private int timesSaled;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 200, message = "La descripción no debe superar los 200 carácteres")
    private String description;


    @OneToMany(mappedBy = "medicine")
    private List<Lote> lote;


    public int getIdMedicine() {
        return idMedicine;
    }

    public void setIdMedicine(int idMedicine) {
        this.idMedicine = idMedicine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTimesSaled() {
        return timesSaled;
    }

    public void setTimesSaled(int timesSaled) {
        this.timesSaled = timesSaled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
