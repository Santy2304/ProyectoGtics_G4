package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM superadmin where email= ?1 and password=?2")
    SuperAdmin buscarSuperAdmin (String email , String password);

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
    @Query(value="select * from administrator where name=?1", nativeQuery = true)
    List<Administrator> listarAdminPorNombre(String name);

    //Buscar doctores por nombre
    @Query(value="select * from doctor where name=?1", nativeQuery = true)
    List<Doctor> listarDocsPorNombre(String name);

    //Buscar pacientes por nombre
    @Query(value="select * from patients where name=?1", nativeQuery = true)
    List<Patient> listarPacientePorNombre(String name);

    //Listar medicamentos
    @Query(value="select * from medicine", nativeQuery = true)
    List<Medicine> listaMedicamentos();

    //Listar medicamento por nombre
    @Query(value ="select * from medicine where name = ?1", nativeQuery = true)
    List<Medicine> listarMedicamentoPorNombre(String name);


    //Editar sede a administrador, pero falta un campo de fecha de edición en la bd del proyecto
    @Transactional
    @Modifying
    @Query(value = "update administrator set site = ?1 where idadministrator=?2", nativeQuery = true)
    void actualizarSedeAdmin(String sede, int idAdmin);

    //Editar correo a admin
    @Transactional
    @Modifying
    @Query(value ="update administrator set email = ?1 where idadministrator=?2", nativeQuery = true)
    void actualizarEmailAdmin(String email, int idAdmin);

    //Banear administrador
    @Transactional
    @Modifying
    @Query(value = "update administrator set banned=TRUE where idAdministrator=?1", nativeQuery= true)
    void banearAdmin(int idAdmin);

    //Registrar administrador
    @Transactional
    @Modifying
    @Query(value = "insert into administrator (name,lastname,dni,site,email,password,dateCreationAccount) values (?1,?2,?3,?4,?5,?6,now())",nativeQuery = true)
    void agregarAdmin(String name, String lastname, String dni, String site,String email, String password);

    //Editar perfil de SuperAdmin
    @Transactional
    @Modifying
    @Query(value = "update superadmin set email = ?1, name=?2 , lastName=?3, password=?4", nativeQuery = true)
    void actualizarPerfilSuperAdmin(String email, String name, String lasName, String password);

    SuperAdmin findByEmail(String email);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="update superadmin set password= ?1 where email=?2")
    void actualizarContrasena(String pswrd, String email);
}
