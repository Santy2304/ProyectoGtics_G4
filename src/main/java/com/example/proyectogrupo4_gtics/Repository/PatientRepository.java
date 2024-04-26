package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Patient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Patient findByName(String hineill);

    @Transactional
    @Modifying
    @Query(nativeQuery= true , value="update patient set distrit=?1 , location=?2 ,  insurance=?3 where idPatient= ?4"
            )
    void updatePatientData(String district , String location, String insurance , int idPatient);

}
