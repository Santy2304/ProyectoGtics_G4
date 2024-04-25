package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine,Integer> {


    List<Medicine> findByIdMedicine(int idMedicine);

    @Query(nativeQuery = true, value = "SELECT \n" +
            "    m.idMedicine AS idMedicine, \n" +
            "    m.name AS nombreMedicamento, \n" +
            "    m.category AS categoria, \n" +
            "    m.price AS precio, \n" +
            "    COALESCE(SUM(l.stock), 0) AS cantidad \n" +
            "FROM \n" +
            "    medicine m \n" +
            "LEFT JOIN \n" +
            "    lote l ON m.idMedicine = l.idMedicine \n" +
            "GROUP BY \n" +
            "    m.idMedicine, m.name, m.category, m.price;")
    List<cantidadMedicamentosDTO> obtenerDatosMedicamentos();



}
