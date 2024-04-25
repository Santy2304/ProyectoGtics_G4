package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Query(value = "select d.idDoctor as idDoctor, d.name as nombre, d.lastName as lastName, d.dni as dni, d.headquarter as sede, d.email as email from doctor d inner join administrator a where (a.site=d.headquarter)",nativeQuery = true)
    List<DoctorPorSedeDTO> listaDoctorPorSede();
}
