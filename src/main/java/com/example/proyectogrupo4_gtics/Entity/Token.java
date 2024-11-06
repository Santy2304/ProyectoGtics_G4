package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "tokens", schema = "proyectogtics")
public class Token implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtoken", nullable = false)
    private Integer id;

    @Size(max = 450)
    @NotNull
    @Column(name = "token", nullable = false, length = 450)
    private String token;
    @Size(max = 45)
    @NotNull
    @Column(name = "estado", nullable = false, length = 45)
    private String estado;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private User idUsuario;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(User idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}