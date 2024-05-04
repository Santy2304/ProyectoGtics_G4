package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name="replacementorder")
public class ReplacementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idreplacementorder")
    private int idReplacementOrder;

    @Column(name="trackingstate")
    private String trackingState;

    @Column(name = "releasedate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private String site;

    @ManyToOne
    @JoinColumn(name="idadministrator")
    private Administrator administrator;

    public int getIdReplacementOrder() {
        return idReplacementOrder;
    }

    public void setIdReplacementOrder(int idReplacementOrder) {
        this.idReplacementOrder = idReplacementOrder;
    }

    public String getTrackingState() {
        return trackingState;
    }

    public void setTrackingState(String trackingState) {
        this.trackingState = trackingState;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }
}
