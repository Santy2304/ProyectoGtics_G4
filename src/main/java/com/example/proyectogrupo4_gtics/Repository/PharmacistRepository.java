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
    @Query(value = "update pharmacist set name = ?1 , lastName =?2 , email=?3, site=?4, state = ?5, distrit = ?6 where idPharmacist =?7" , nativeQuery = true)
    void updateDatosPorId(String name , String lasName  , String email ,String site,String state,String distrit ,int idFarmacista );


    @Transactional
    @Modifying
    @Query(value = "update pharmacist set state = 'eliminado'  where idPharmacist =?1" , nativeQuery = true)
    void eliminarFarmacistaPorId(int idFarmacista);

    @Transactional
    @Modifying
    @Query(value = "update pharmacist set approvalState = 'aceptado', dateCreationAccount=now() , state='activo'  where idPharmacist =?1" , nativeQuery = true)
    void aceptarFarmacistaPorId(int idFarmacista);

    @Transactional
    @Modifying
    @Query(value = "update pharmacist set approvalState = 'rechazado'  where idPharmacist =?1" , nativeQuery = true)
    void rechazarFarmacistaPorId(int idFarmacista);



    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' or approvalState='rechazado' and site = 'Pando 1'")
    List<Pharmacist> listarSolicitudesFarmacistaPando1();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' or approvalState='rechazado' and site = 'Pando 2'")
    List<Pharmacist> listarSolicitudesFarmacistaPando2();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' or approvalState='rechazado' and site = 'Pando 3'")
    List<Pharmacist> listarSolicitudesFarmacistaPando3();

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist WHERE approvalState = 'pendiente' or approvalState='rechazado' and site = 'Pando 4'")
    List<Pharmacist> listarSolicitudesFarmacistaPando4();


    @Query(nativeQuery = true,value="select p.idPharmacist as idPharmacist, p.name as nombre, p.lastName as apellido,p.site as sede,p.dni as dni,p.code as codigo,p.email as email,p.photo as foto, p.approvalState as estadoAprobacion, p.rejectedReason as rechazo, p.state as estado, p.dateCreationAccount as creationDate from pharmacist p inner join administrator a on (p.site=a.site) where p.approvalState='aceptado' and p.state!='eliminado' and a.idAdministrator=?1")
    List<FarmacistaPorSedeDTO> listaFarmacistaPorSede(int idAdministrator);

    @Query(nativeQuery = true,value="select p.idPharmacist as idPharmacist, p.name as nombre, p.lastName as apellido,p.site as sede,p.dni as dni,p.code as codigo,p.email as email,p.photo as foto, p.approvalState as estadoAprobacion, p.rejectedReason as rechazo, p.state as estado from pharmacist p inner join administrator a on (p.site=a.site) where lower(p.name) like concat(?1,'%'), p.approvalState='aceptado' and a.idAdministrator=?2")
    List<FarmacistaPorSedeDTO> listaFarmacistaPorBuscador(String nombre, int idAdministrator);

    @Query(nativeQuery = true, value = "SELECT * FROM pharmacist where email= ?1 and password=?2")
    Pharmacist buscarPharmacist (String email , String password);


    /*Farmacista view*/
    @Transactional
    @Modifying
    @Query(value="update pharmacist set email = ?1, distrit = ?2 where idPharmacist = ?3", nativeQuery = true)
    void updateEmailAndDistritById(String email, String distrit, int idPharmacist);

    Pharmacist getByIdFarmacista(int idPharmacist);

    /*-----*/

 }



