package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Patient findByName(String hineill);

    @Query(nativeQuery = true, value = "SELECT * FROM patient WHERE state <> 'eliminado'")
    List<Patient> listarPacientesValidos();

    @Transactional
    @Modifying
    @Query(nativeQuery= true , value="update patient set distrit=?1 , location=?2 ,  insurance=?3 where idPatient= ?4")
    void updatePatientData(String district , String location, String insurance , int idPatient);

    @Transactional
    @Modifying
    @Query(value = "update patient set state = 'eliminado'  where idPatient =?1" , nativeQuery = true)
    void eliminarPacientePorId(int idPaciente);

    @Transactional
    @Modifying
    @Query(value = "update patient set state = 'baneado'  where idPatient =?1" , nativeQuery = true)
    void banearPacientePorId(int idPaciente);

    @Query(nativeQuery = true, value = "SELECT * FROM patient where email= ?1 and password=?2")
    Patient buscarPatient (String email , String password);


    //findByEmail
    @Query(nativeQuery = true, value = "SELECT * FROM patient where email=?1")
    Optional<Patient> findByEmail(String email );

    @Query(nativeQuery = true, value = "SELECT * FROM patient where dni=?1")
    Optional<Patient> findByDni(String dni );

    Patient getByEmail(String email);

    @Query(nativeQuery = true, value="select * from patient where email=?1")
    Patient buscarPatientEmail(String email);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="update patient set password= ?1 where email=?2")
    void actualizarContrasena(String pswrd, String email);



    @Transactional
    @Modifying
    @Query(nativeQuery= true , value="update patient set changePassword=1 where idPatient= ?1")
    void updateChangePasswrod(int idPatient);

}
