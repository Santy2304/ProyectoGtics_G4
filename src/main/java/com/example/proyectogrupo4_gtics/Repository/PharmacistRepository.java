package com.example.proyectogrupo4_gtics.Repository;
import com.example.proyectogrupo4_gtics.DTOs.FarmacistaPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Integer> {

    @Query(nativeQuery = true,value="select p.idPharmacist as idPharmacist, p.name as nombre, p.lastName as apellido,p.site as sede,p.dni as dni,p.code as codigo,p.email as email,p.photo as foto, p.approvalState as estadoAprobacion, p.rejectedReason as rechazo, p.state as estado from pharmacist p inner join administrator a on (p.site=a.site) where p.approvalState='aceptado' and a.idAdministrator=?1")
    List<FarmacistaPorSedeDTO> listaFarmacistaPorSede(int idAdministrator);

    @Query(nativeQuery = true,value="select p.idPharmacist as idPharmacist, p.name as nombre, p.lastName as apellido,p.site as sede,p.dni as dni,p.code as codigo,p.email as email,p.photo as foto, p.approvalState as estadoAprobacion, p.rejectedReason as rechazo, p.state as estado from pharmacist p inner join administrator a on (p.site=a.site) where lower(p.name) like concat(?1,'%'), p.approvalState='aceptado' and a.idAdministrator=?2")
    List<FarmacistaPorSedeDTO> listaFarmacistaPorBuscador(String nombre, int idAdministrator);

 }



