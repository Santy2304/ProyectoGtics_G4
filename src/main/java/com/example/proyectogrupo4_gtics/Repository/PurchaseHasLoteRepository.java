package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.PurchaseHasLotID;
import com.example.proyectogrupo4_gtics.Entity.PurchaseHasLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseHasLoteRepository extends JpaRepository<PurchaseHasLote, PurchaseHasLotID> {

    @Query(nativeQuery = true, value = "SELECT ctl.*\n" +
            "FROM purchasehaslot AS ctl\n" +
            "JOIN purchaseorder AS p ON ctl.idPurchase = p.idPurchaseOrder\n" +
            "JOIN patient AS c ON p.idPatient = c.idPatient WHERE c.idPatient = ?1")
    List<PurchaseHasLote> listarLotesPorCompra(int idPatient);

}
