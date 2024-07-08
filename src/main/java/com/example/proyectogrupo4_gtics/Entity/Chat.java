package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "chat", schema = "proyectogtics")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idchat", nullable = false)
    private Integer idChat;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idfarmacist", nullable = false)
    private Pharmacist idFarmacist;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpacient", nullable = false)
    private Patient idPacient;



    public Integer getIdChat() {
        return idChat;
    }

    public void setIdChat(Integer idChat) {
        this.idChat = idChat;
    }

    public Pharmacist getIdFarmacist() {
        return idFarmacist;
    }

    public void setIdFarmacist(Pharmacist idFarmacist) {
        this.idFarmacist = idFarmacist;
    }

    public Patient getIdPacient() {
        return idPacient;
    }

    public void setIdPacient(Patient idPacient) {
        this.idPacient = idPacient;
    }


}