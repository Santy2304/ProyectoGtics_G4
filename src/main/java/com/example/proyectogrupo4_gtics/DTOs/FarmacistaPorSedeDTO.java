package com.example.proyectogrupo4_gtics.DTOs;

import java.time.LocalDate;

public interface FarmacistaPorSedeDTO {
    int getIdPharmacist();
    String getNombre();
    String getApellido();
    String getSede();
    String getDni();
    String getCodigo();
    String getEmail();
    String getPhoto();
    String getEstadoAprobacion();
    String getRechazo();
    String getEstado();

    LocalDate getCreationDate();

}
