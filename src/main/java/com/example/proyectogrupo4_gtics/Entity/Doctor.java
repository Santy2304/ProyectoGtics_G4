package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;


@Entity
@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="iddoctor")
    @Digits(integer=10, fraction=0)
    @Positive
    private int idDoctor;

    @Column(name="name")
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El nombre no debe tener más de 45 carácteres")
    private String name;

    @Column(name="lastname", nullable=false)
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max = 45, message = "El apellido no debe tener más de 45 carácteres")
    private String lastName;

    @Column(name="dni")
    @NotNull(message = "Este campo es obligatorio")
    @Digits(integer=8, fraction=0, message = "El DNI debe ser un número")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Column(name="headquarter")
    @NotBlank(message = "Este campo es obligatorio")
    private String headquarter;

    @Column(name="email")
    @NotBlank(message = "Este campo es obligatorio")
    @Email(message = "Se debe ingresar un correo electrónico")
    private String email;

    @Column(name = "state", nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    private String state;

    @Column(name = "datecreationaccount", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
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

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
