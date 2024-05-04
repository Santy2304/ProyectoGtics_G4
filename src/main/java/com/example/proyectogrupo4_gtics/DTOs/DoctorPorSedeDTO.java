package com.example.proyectogrupo4_gtics.DTOs;

import org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl;

import java.time.LocalDate;

public interface DoctorPorSedeDTO {
    int getIdDoctor();
    String getNombre();
    String getLastName();
    String getDni();
    String getSede();
    String getEmail();
    String getState();

    LocalDate getCreationDate();
}
