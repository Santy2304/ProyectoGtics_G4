package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.PurchaseOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.proyectogrupo4_gtics.DTOs.PurchaseOrderPorSedeDTO;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    @Query(nativeQuery = true, value="select po.idPurchaseOrder as idPurchasOrder, po.phoneNumber as numero, CONCAT(p.name,'',p.lastName) as nombrePaciente,CONCAT(d.name,'',d.lastName) as nombreDoctor, po.prescription as prescripcion,po.tracking as tracking, po.approval as aprobado, po.releaseDate as fechaRelease, po.totalAmount as monto,po.statePaid as estadoPago,po.tipo as tipo from purchaseorder po inner join patient p on po.idPatient=p.idPatient inner join doctor d on po.idDoctor = d.idDoctor where po.site=?1 group by po.idPurchaseOrder;")
    List<PurchaseOrderPorSedeDTO> listaPurchaseOrderPorSede(String sede);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="insert into purchaseorder (phoneNumber,idPatient, idDoctor, approval, releaseDate,site,statePaid,tipo, tipoPago)\n" +
            "values (?1,?2,?3,'aceptado',now(),?4,'pagado','presencial',?5)")
    void compraPresencialSimple(String numero, int idPaciente, int idDoctor, String sede, String tipoPago);
}
