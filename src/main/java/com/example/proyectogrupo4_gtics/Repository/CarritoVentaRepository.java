package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Carrito;
import com.example.proyectogrupo4_gtics.Entity.CarritoVenta;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CarritoVentaRepository extends JpaRepository<CarritoVenta, Integer>  {
    @Query(nativeQuery = true, value = "select * from carritoventa where idPharmacist= ?1 ")
    List<CarritoVenta> getMedicineListByPharmacist(int idPharmacist );
}
