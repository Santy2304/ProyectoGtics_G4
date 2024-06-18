package com.example.proyectogrupo4_gtics.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
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
    @PositiveOrZero
    private int idMedicine;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El nombre no debe tener más de 45 carácteres")
    private String name;

    @Column(name = "category", nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    private String category;

    @Column(nullable = false)
    @NotNull(message = "Este campo es obligatorio")
    @Digits(integer = 5, fraction = 2, message = "Se debe ingresar un número correcto")
    @Positive(message = "El precio debe ser un valor mayor a cero")
    private BigDecimal price;

    @Column(name = "timessaled")
    private int timesSaled;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 200, message = "La descripción no debe superar los 200 carácteres")
    private String description;

    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
