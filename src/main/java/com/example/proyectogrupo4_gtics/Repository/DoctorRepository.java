package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.DoctorPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Transactional
    @Modifying
    @Query(value = "update doctor set name = ?1 , lastName =?2 , dni=?3 , email=?4, headquarter=?5, state = ?6 where idDoctor =?7" , nativeQuery = true)
    void updateDatosPorId(String name , String lasName , String dni , String email ,String headquarter,String state ,int idDoctor );

    @Query(nativeQuery = true, value = "select d.idDoctor as idDoctor, d.name as nombre, d.lastName as lastName, d.dni as dni, d.headquarter as sede, d.email as email from doctor d inner join administrator a where (a.site=d.headquarter)")
    List<DoctorPorSedeDTO> listaDoctorPorSede();

    @Transactional
    @Modifying
    @Query(value = "update doctor set state = 'eliminado'  where idDoctor =?1" , nativeQuery = true)
    void eliminarDoctorPorId(int idDoctor);

    @Query(nativeQuery = true, value = "SELECT * FROM doctor WHERE state <> 'eliminado'")
    List<Doctor> listarDoctoresValidos();

}
