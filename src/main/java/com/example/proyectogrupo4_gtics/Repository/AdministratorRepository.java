package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {
    @Transactional
    @Modifying
    @Query(value = "update administrator set name = ?1 , lastName =?2 , dni=?3 , email=?4, site=?5, state = ?6 where idAdministrator =?7" , nativeQuery = true)
    void updateDatosPorId(String name , String lasName , String dni , String email ,String site,String state ,int idAdminSede );

    @Transactional
    @Modifying
    @Query(value = "update administrator set state = 'eliminado'  where idAdministrator =?1" , nativeQuery = true)
    void eliminarAdminPorId(int idAdminSede);


    @Query(nativeQuery = true, value = "SELECT * FROM administrator WHERE state <> 'eliminado'")
    List<Administrator> listarAdminValidos();


    Administrator getByIdAdministrador(int idAdministrator);
    @Query(nativeQuery = true, value = "SELECT * FROM administrator where email= ?1 and password=?2")
    Administrator buscarAdmin(String email , String password);

    @Query(nativeQuery = true, value = "SELECT * FROM administrator where email= ?1 ")
    Administrator findByEmail(String email);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="update administrator set password= ?1 where email=?2")
    void actualizarContrasena(String pswrd, String email);

}
