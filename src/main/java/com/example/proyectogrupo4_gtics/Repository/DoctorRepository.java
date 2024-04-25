package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Transactional
    @Modifying
    @Query(value = "update doctor set name = ?1 , lastName =?2 , dni=?3 , email=?4 where idDoctor =?5" , nativeQuery = true)
    void updateDatosPorId(String name , String lasName , String dni , String email , int idDoctor );
}
