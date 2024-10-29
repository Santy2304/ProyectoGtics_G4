package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="replacementorderhasmedicine")
public class ReplacementOrderHasMedicine {
    @EmbeddedId
    private ReplacementOrderHasMedicineID id;

    @MapsId("idReplacementOrder")
    @ManyToOne
    @JoinColumn(name="idReplacementOrder")
    private ReplacementOrder idReplacementOrder;

    @MapsId("idMedicine")
    @ManyToOne
    @JoinColumn(name="idMedicine")
    private Medicine idMedicine;

    @Column(name="amount", nullable = false)
    private int cantidad;
}
