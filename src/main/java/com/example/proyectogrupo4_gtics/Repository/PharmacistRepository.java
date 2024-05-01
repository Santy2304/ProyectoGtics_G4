package com.example.proyectogrupo4_gtics.Repository;
import com.example.proyectogrupo4_gtics.DTOs.FarmacistaPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Integer> {
    @Query(nativeQuery = true,value="select p.idPharmacist as idPharmacist, p.name as name,p.lastName as apellido,p.site as sede,p.dni as dni,p.code as codigo,p.email as email,p.photo as foto from pharmacist p inner join administrator a where a.site=p.site")
    List<FarmacistaPorSedeDTO> listaFarmacistaPorSede();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE state <> 'eliminado'")
    List<Pharmacist> listarFarmacistasValidos();

 }
