package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Patient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM Patient where email= ?1 and password=?2")
    Patient buscarPatient (String email , String password);
    Patient findByName(String hineill);

    @Transactional
    @Modifying
    @Query(nativeQuery= true , value="update patient set distrit=?1 , location=?2 ,  insurance=?3 where idPatient= ?4"
            )
    void updatePatientData(String district , String location, String insurance , int idPatient);

}
