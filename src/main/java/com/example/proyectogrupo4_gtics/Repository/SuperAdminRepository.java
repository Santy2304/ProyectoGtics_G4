package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Integer> {

    //Listar administradores
    @Query(value ="select * from administrator",nativeQuery = true)
    List<Administrator> listarAdministradores();

    //Listar doctores
    @Query(value ="select * from doctor", nativeQuery = true)
    List<Doctor> listarDoctores();

    //Listar farmacista
    //@Query(value = "select * from pharmacist", nativeQuery = true)
    //List<Pharmacist> listaFarmacista();

    //Listar paciente
    @Query(value = "select * from patient", nativeQuery = true)
    List<Patient> listarPacientes();


    //Buscar administradores por nombre
    @Query(value="select * from administrator where name=?", nativeQuery = true)
    List<Administrator> listarAdminPorNombre(String name);

    //Buscar doctores por nombre
    @Query(value="select * from doctor where name=?", nativeQuery = true)
    List<Doctor> listarDocsPorNombre(String name);

    //Buscar pacientes por nombre
    @Query(value="select * from patients where name=?", nativeQuery = true)
    List<Patient> listarPacientePorNombre(String name);

    //Listar medicamentos
    @Query(value="select * from medicine", nativeQuery = true)
    List<Medicine> listaMedicamentos();

    //Listar medicamento por nombre
    @Query(value ="select * from medicine where name = ?", nativeQuery = true)
    List<Medicine> listarMedicamentoPorNombre(String name);







}
