package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.Entity.ReplacementOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplacementOrderRepository extends JpaRepository<ReplacementOrder, Integer> {


    @Query(nativeQuery = true, value = "SELECT ro.idReplacementOrder AS replaceOrderID, m.name AS medicineName,SUM(l.stock) AS totalStock FROM replacementorder ro\n" +
            "JOIN lote l ON ro.idReplacementOrder = l.idPedidosReposicion  \n" +
            "JOIN medicine m ON l.idMedicine = m.idMedicine where ro.idReplacementOrder = ?1  \n" +
            "GROUP BY ro.idReplacementOrder, m.name\n" +
            "ORDER BY ro.idReplacementOrder,m.name")
    List<MedicamentosPorReposicionDTO> obtenerMedicamentosPorReposicion(int idReplacement);


    @Query(nativeQuery = true, value = "select * from replacementorder where site = 'Pando 1'")
    List<ReplacementOrder> obtenerSolicitudesRepoPando1();


    @Query(nativeQuery = true, value = "select * from replacementorder where site = 'Pando 2'")
    List<ReplacementOrder> obtenerSolicitudesRepoPando2();


    @Query(nativeQuery = true, value = "select * from replacementorder where site = 'Pando 3'")
    List<ReplacementOrder> obtenerSolicitudesRepoPando3();

    @Query(nativeQuery = true, value = "select * from replacementorder where site = 'Pando 4'")
    List<ReplacementOrder> obtenerSolicitudesRepoPando4();

    @Query(nativeQuery = true, value = "select * from replacementorder r where r.site = ?1 ")
    List<ReplacementOrder> getReplacementOrderBySede(String siteName);

}
