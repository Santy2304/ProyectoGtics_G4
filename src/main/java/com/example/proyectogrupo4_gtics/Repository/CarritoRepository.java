package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer>  {
    @Query(nativeQuery = true, value = "select * from carrito where idPatient= ?1 ")
    List<Carrito> getMedicineListByPatient(int idPatient );

}
