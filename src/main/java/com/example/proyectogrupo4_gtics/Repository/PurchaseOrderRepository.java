package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchaseOrderPorSedeDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchasePorPatientDTO;
import com.example.proyectogrupo4_gtics.Entity.PurchaseOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "    po.idPurchaseOrder  ORDER BY po.releaseDate desc")
    List<PurchasePorPatientDTO> obtenerComprarPorPaciente(int idPatient);


    @Query(nativeQuery = true, value = "SELECT po.idPurchaseOrder,\n" +
            "SUM(m.price * phl.cantidad_comprar) AS total_price,\n" +
            "po.releaseDate as fecha,\n" +
            "po.approval as estado,\n" +
            "po.statePaid as estadoPago\n" +
            "FROM\n" +
            "purchaseorder po\n" +
            "INNER JOIN\n" +
            "purchasehaslot phl ON po.idPurchaseOrder = phl.idPurchase\n" +
            "INNER JOIN\n" +
            "lote l ON phl.idLote = l.idLote \n" +
            "INNER JOIN\n" +
            "medicine m ON l.idMedicine = m.idMedicine\n" +
            "where po.idPurchaseOrder = ?1")
    PurchasePorPatientDTO obtenerPurchasePorId(int idPurchase);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "    po.idPurchaseOrder,\n" +
            "    SUM(m.price * phl.cantidad_comprar) AS total_price,\n" +
            "    po.releaseDate as fecha,\n" +
            "    po.tracking as tracking\n" +
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


    @Transactional
    @Modifying
    @Query(value = "update purchaseorder set tracking = ?1  where idPurchaseOrder =?2" , nativeQuery = true)
    void actualizarTrackingPurchase(String estado,int id);


    /*Pharmacist*/
    @Query(nativeQuery = true,value = "select po.idPurchaseOrder as idPurchaseOrder, po.phoneNumber as numero, CONCAT(p.name,' ',p.lastName) as nombrePaciente, CONCAT(d.name,'',d.lastName) as nombreDoctor, po.prescription as  prescripcion, po.tracking as tracking, po.releaseDate as fechaRelease, sum(m.price*phl.cantidad_comprar) as monto, po.statePaid as estadoPago, po.tipo as tipoCompra,po.tipoPago as tipoPago, po.releaseDate as fecha from purchaseorder po inner join purchasehaslot phl on phl.idPurchase=po.idPurchaseOrder inner join lote l on phl.idLote = l.idLote inner join medicine m on m.idMedicine=l.idMedicine inner join doctor d on po.idDoctor=d.idDoctor inner join patient p on po.idPatient=p.idPatient where po.site=?1 and po.tipo='presencial'  group by po.idPurchaseOrder")
    List<PurchaseOrderPorSedeDTO> listaVentasPresencialPorSede(String sede);

    @Query(nativeQuery = true,value = "select po.idPurchaseOrder as idPurchaseOrder, po.phoneNumber as numero, CONCAT(p.name,' ',p.lastName) as nombrePaciente, CONCAT(d.name,'',d.lastName) as nombreDoctor, po.prescription as  prescripcion, po.tracking as tracking, po.releaseDate as fechaRelease, sum(m.price*phl.cantidad_comprar) as monto, po.statePaid as estadoPago, po.tipo as tipoCompra,po.tipoPago as tipoPago , po.releaseDate as fecha from purchaseorder po inner join purchasehaslot phl on phl.idPurchase=po.idPurchaseOrder inner join lote l on phl.idLote = l.idLote inner join medicine m on m.idMedicine=l.idMedicine inner join doctor d on po.idDoctor=d.idDoctor inner join patient p on po.idPatient=p.idPatient where po.site=?1 and po.tipo='web' and po.approval='aceptado' group by po.idPurchaseOrder")
    List<PurchaseOrderPorSedeDTO> listaVentasWEBPorSede(String sede);

    @Query(nativeQuery = true,value = "select po.idPurchaseOrder as idPurchaseOrder, po.phoneNumber as numero, CONCAT(p.name,' ',p.lastName) as nombrePaciente, CONCAT(d.name,'',d.lastName) as nombreDoctor, po.prescription as  prescripcion, po.tracking as tracking, po.releaseDate as fechaRelease, sum(m.price*phl.cantidad_comprar) as monto, po.statePaid as estadoPago, po.tipo as tipoCompra,po.tipoPago as tipoPago, po.releaseDate as fecha from purchaseorder po inner join purchasehaslot phl on phl.idPurchase=po.idPurchaseOrder inner join lote l on phl.idLote = l.idLote inner join medicine m on m.idMedicine=l.idMedicine inner join doctor d on po.idDoctor=d.idDoctor inner join patient p on po.idPatient=p.idPatient where po.site=?1 and po.tipo='bot' and po.approval='aceptado' group by po.idPurchaseOrder")
    List<PurchaseOrderPorSedeDTO> listaVentasBOTPorSede(String sede);



    @Query(nativeQuery = true,value = "select po.idPurchaseOrder as idPurchaseOrder, po.phoneNumber as numero, CONCAT(p.name,' ',p.lastName) as nombrePaciente, CONCAT(d.name,'',d.lastName) as nombreDoctor, po.prescription as  prescripcion, po.tracking as tracking, po.releaseDate as fechaRelease, sum(m.price*phl.cantidad_comprar) as monto, po.statePaid as estadoPago, po.tipo as tipoCompra,po.tipoPago as tipoPago, po.releaseDate as fecha from purchaseorder po inner join purchasehaslot phl on phl.idPurchase=po.idPurchaseOrder inner join lote l on phl.idLote = l.idLote inner join medicine m on m.idMedicine=l.idMedicine inner join doctor d on po.idDoctor=d.idDoctor inner join patient p on po.idPatient=p.idPatient where po.site=?1 and po.approval='pendiente' and tipo='web' group by po.idPurchaseOrder")
    List<PurchaseOrderPorSedeDTO> listaVentasSolicitudesWEBPorSede(String sede);


    @Query(nativeQuery = true,value = "select po.idPurchaseOrder as idPurchaseOrder, po.phoneNumber as numero, CONCAT(p.name,' ',p.lastName) as nombrePaciente, CONCAT(d.name,'',d.lastName) as nombreDoctor, po.prescription as  prescripcion, po.tracking as tracking, po.releaseDate as fechaRelease, sum(m.price*phl.cantidad_comprar) as monto, po.statePaid as estadoPago, po.tipo as tipoCompra,po.tipoPago as tipoPago, po.releaseDate as fecha from purchaseorder po inner join purchasehaslot phl on phl.idPurchase=po.idPurchaseOrder inner join lote l on phl.idLote = l.idLote inner join medicine m on m.idMedicine=l.idMedicine inner join doctor d on po.idDoctor=d.idDoctor inner join patient p on po.idPatient=p.idPatient where po.site=?1 and po.approval='pendiente' and tipo='bot' group by po.idPurchaseOrder")
    List<PurchaseOrderPorSedeDTO> listaVentasSolicitudesBOTPorSede(String sede);

    @Transactional
    @Modifying
    @Query(value = "update purchaseorder set approval = 'aceptado', tracking='solicitado', statePaid='por pagar'  where idPurchaseOrder =?1" , nativeQuery = true)
    void aceptarSolicitudPorId(int idSolicitud);



    @Transactional
    @Modifying
    @Query(value = "update purchaseorder set approval = 'rechazado'  where idPurchaseOrder =?1" , nativeQuery = true)
    void rechazarSolicitudPorId(int idSolicitud);


    @Transactional
    @Modifying
    @Query(value = "update purchaseorder set approval = 'invalidado'  where idPurchaseOrder =?1" , nativeQuery = true)
    void invalidarCompraPorId(int idSolicitud);


    @Query(value = "select idPatient from purchaseorder where idPurchaseOrder =?1" , nativeQuery = true)
    int encontrarIdPatient(int idSolicitud);

    @Transactional
    @Modifying
    @Query(value = "update purchaseorder set statePaid='pagado'  where idPurchaseOrder =?1" , nativeQuery = true)
    void pagarOrdenCompra(int idSolicitud);
}
