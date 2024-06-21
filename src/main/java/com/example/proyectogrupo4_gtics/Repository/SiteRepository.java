package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Entity.Notifications;
import com.example.proyectogrupo4_gtics.Entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site,Integer>  {

    @Query(nativeQuery = true, value = "select * from site where name = ?1")
    Site encontrarSedePorNombre(String sede);
}
