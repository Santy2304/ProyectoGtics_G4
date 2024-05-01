package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM Administrator where email= ?1 and password=?2")
    Administrator buscarAdmin (String email , String password);
}
