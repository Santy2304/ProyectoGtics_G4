package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;



@Entity
@Table(name="superadmin")
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idsuperadmin")
    private int idSuperAdmin;

    @Column(name="email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name="photo")
    private String photo;

    public int getIdSuperAdmin() {
        return idSuperAdmin;
    }

    public void setIdSuperAdmin(int idSuperAdmin) {
        this.idSuperAdmin = idSuperAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
