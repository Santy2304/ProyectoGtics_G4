package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name="superadmin")
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idsuperadmin")
    private int idSuperAdmin;

    @Column(name="email")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=11,max = 45, message = "El correo no debe tener más de 45 carácteres y no puede ser nulo")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=5, max = 45, message = "La contraseña no debe tener más de 45 carácteres y ser mayor a 5")
    private String password;


    @Column(name="photo")
    private String photo;

    @Column(name="name")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1, max = 45, message = "El nombre no debe tener más de 45 carácteres y no puede ser nulo")
    private String name;

    @Column(name = "lastname")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(min=1,max = 45, message = "El apellido no debe tener más de 45 carácteres y no puede ser nulo")
    private String lastname;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
