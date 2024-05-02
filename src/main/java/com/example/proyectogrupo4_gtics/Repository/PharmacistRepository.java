package com.example.proyectogrupo4_gtics.Repository;
import com.example.proyectogrupo4_gtics.DTOs.FarmacistaPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Integer> {
    @Query(nativeQuery = true,value="select p.idPharmacist as idPharmacist, p.name as name,p.lastName as apellido,p.site as sede,p.dni as dni,p.code as codigo,p.email as email,p.photo as foto from pharmacist p inner join administrator a where a.site=p.site")
    List<FarmacistaPorSedeDTO> listaFarmacistaPorSede();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE state <> 'eliminado' and approvalState = 'aceptado'")
    List<Pharmacist> listarFarmacistasValidos();

    @Transactional
    @Modifying
    @Query(value = "update pharmacist set name = ?1 , lastName =?2 , dni=?3 , email=?4, site=?5, state = ?6, distrit = ?7 where idPharmacist =?8" , nativeQuery = true)
    void updateDatosPorId(String name , String lasName , String dni , String email ,String site,String state,String distrit ,int idFarmacista );

    @Transactional
    @Modifying
    @Query(value = "update pharmacist set state = 'eliminado'  where idPharmacist =?1" , nativeQuery = true)
    void eliminarFarmacistaPorId(int idFarmacista);

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' and site = 'Pando 1'")
    List<Pharmacist> listarSolicitudesFarmacistaPando1();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' and site = 'Pando 2'")
    List<Pharmacist> listarSolicitudesFarmacistaPando2();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' and site = 'Pando 3'")
    List<Pharmacist> listarSolicitudesFarmacistaPando3();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' and site = 'Pando 4'")
    List<Pharmacist> listarSolicitudesFarmacistaPando4();

 }
