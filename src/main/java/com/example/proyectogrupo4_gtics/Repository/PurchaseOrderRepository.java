package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchasePorPatientDTO;
import com.example.proyectogrupo4_gtics.Entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer>{


    @Query(nativeQuery = true, value = "SELECT\n" +
            "    po.idPurchaseOrder,\n" +
            "    SUM(m.price * phl.cantidad_comprar) AS total_price,\n" +
            "    po.releaseDate as fecha,\n" +
            "    po.approval as estado,\n" +
            "    po.statePaid as estadoPago\n" +
            "FROM\n" +
            "    purchaseorder po\n" +
            "INNER JOIN\n" +
            "    purchasehaslot phl ON po.idPurchaseOrder = phl.idPurchase\n" +
            "INNER JOIN\n" +
            "    lote l ON phl.idLote = l.idLote\n" +
            "INNER JOIN\n" +
            "    medicine m ON l.idMedicine = m.idMedicine\n" +
            " \n" +
            "where po.idPatient = ?1 and tipo='web'\n" +
            "    \n" +
            "GROUP BY\n" +
            "    po.idPurchaseOrder")
    List<PurchasePorPatientDTO> obtenerComprarPorPaciente(int idPatient);


    @Query(nativeQuery = true, value = "SELECT\n" +
            "    po.idPurchaseOrder,\n" +
            "    SUM(m.price * phl.cantidad_comprar) AS total_price,\n" +
            "    po.releaseDate as fecha,\n" +
            "    po.approval as estado,\n" +
            "    po.statePaid as estadoPago\n" +
            "FROM\n" +
            "    purchaseorder po\n" +
            "INNER JOIN\n" +
            "    purchasehaslot phl ON po.idPurchaseOrder = phl.idPurchase\n" +
            "INNER JOIN\n" +
            "    lote l ON phl.idLote = l.idLote\n" +
            "INNER JOIN\n" +
            "    medicine m ON l.idMedicine = m.idMedicine\n" +
            " \n" +
            "where po.idPatient = ?1 and po.statePaid='pagado' and tipo='web'\n" +
            "    \n" +
            "GROUP BY\n" +
            "    po.idPurchaseOrder")
    List<PurchasePorPatientDTO> obtenerComprarPorPacienteTracking(int idPatient);


}
