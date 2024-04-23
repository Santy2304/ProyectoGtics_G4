package com.example.proyectogrupo4_gtics.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.sql.Date;
import java.util.List;

public class Medicine {

    @Id
    @Column(name = "idmedicine")
    private int idMedicine;

    private String name;

    private String category;

    private double price;

    @Column(name = "timessaled")
    private int timesSaled;

    private String description;

    @OneToMany(mappedBy = "idmedicine")
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

    public List<Lote> getLote() {
        return lote;
    }

    public void setLote(List<Lote> lote) {
        this.lote = lote;
    }
}
