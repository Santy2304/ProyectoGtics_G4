package com.example.proyectogrupo4_gtics.Entity;
import com.mysql.cj.jdbc.Blob;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;


@Entity
@Table(name="administrator")
public class Administrator{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="idadministrator")
    @Digits(integer = 10, fraction = 0)
    @Positive
    private int idAdministrador;

    @Column(name="name")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El nombre no puede pasar más de 45 carácteres")
    private String name;

    @Column(name="lastname")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El apellido no puede pasara más de 45 carácteres")
    private String lastName;

    @Column(name="dni")
    @NotNull(message = "Este campo es obligatorio")
    @Digits(integer = 8, fraction = 0, message = "El DNI debe ser un número y tener 8 dígitos")
    private String dni;

    @Column(name = "site")
    @NotBlank(message = "Este campo es obligatorio")
    private String site;

    @Column(name="email")
    @NotBlank(message = "Este campo es obligatorio")
    @Email(message = "Se debe ingresar un correo electrónico")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="state")
    @NotBlank(message = "Este campo es obligatorio")
    private String state;

    @Column(name="photo")
    private String photo; /*Persistence no me deja poner Blob*/

    @Column(name = "datecreationaccount", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
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


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}
