package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.MeciamentosPorCompraDTO;
import com.example.proyectogrupo4_gtics.DTOs.CantidadMedicamentosDTO;
import com.example.proyectogrupo4_gtics.DTOs.medicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine,Integer> {
    @Query(nativeQuery = true, value = "select m.idMedicine as idMedicine,  m.name as nombreMedicamento, m.category as categoria, m.price as precio,m.photo as photo, l.stock as cantidad from medicine m inner join lote l on (m.idMedicine=l.idMedicine and l.site = (SELECT name FROM site where idSite= ?1))")
    List<medicamentosPorSedeDTO> getMedicineBySite(int idSede);


    List<Medicine> findByIdMedicine(int idMedicine);

    @Query(nativeQuery = true, value = "SELECT \n" +
            "    m.idMedicine AS idMedicine, \n" +
            "    m.name AS nombreMedicamento, \n" +
            "    m.category AS categoria, \n" +
            "    m.price AS precio, \n" +
            "    m.photo AS photo, \n" +
            "    COALESCE(SUM(l.stock), 0) AS cantidad \n" +
            "FROM \n" +
            "    medicine m \n" +
            "LEFT JOIN \n" +
            "    lote l ON m.idMedicine = l.idMedicine \n" +
            "GROUP BY \n" +
            "    m.idMedicine, m.name, m.category, m.price;")
    List<CantidadMedicamentosDTO> obtenerDatosMedicamentos();



    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update medicine set name=?1, category=?2, price=?3,description=?4, photo=?5  where idMedicine = ?6")
    void actualizarMedicine(String name, String category, BigDecimal price, String description, String photo, int idMedicine);


    /*Rol administrador de sede*/
    @Query(nativeQuery = true, value =
            "SELECT m.description AS description, m.idMedicine AS idMedicine, m.name AS nombreMedicamento, m.category AS categoria, m.photo AS photo, " +
                    "COUNT(m.idMedicine) AS cantLote, TRUNCATE(m.price, 2) AS precio, SUM(l.stock) AS cantidad " +
                    "FROM medicine m " +
                    "LEFT JOIN lote l ON m.idMedicine = l.idMedicine " +
                    "WHERE l.idLote IN ( " +
                    "    SELECT l2.idLote " +
                    "    FROM lote l2 " +
                    "    LEFT JOIN replacementorder r ON r.idreplacementorder = l2.idPedidosReposicion " +
                    "    LEFT JOIN trackings t ON r.idtrackings = t.idtrackings " +
                    "    WHERE l2.site = (SELECT site FROM administrator WHERE idAdministrator = ?1) " +
                    "    AND (r.trackingState = 'Entregado' OR l2.idPedidosReposicion IS NULL) " +
                    "    AND l2.visible = true " +
                    ") " +
                    "GROUP BY m.idMedicine, m.description, m.name, m.category, m.photo, m.price"
    )
    List<medicamentosPorSedeDTO> listaMedicamentosPorSede(int idAdmin);


    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, sum(l.stock) as cantidad,m.photo as photo \n" +
            "from medicine m \n" +
            "left join lote l on (m.idMedicine=l.idMedicine) \n" +
            "where (m.name like concat(?1,'%') and m.category like concat(?2 , '%') ) \n" +
            "and \n" +
            "l.idLote in ( \n" +
            "select l.idLote from lote  l  \n" +
            "inner join replacementorder r on (r.idReplacementOrder = l.idPedidosReposicion or l.idPedidosReposicion is null) \n" +
            "where l.site = (select site from administrator where idAdministrator=?3)  and r.trackingState = 'Entregado' and l.visible= true \n" +
            ") \n" +
            "group by m.idMedicine")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorDosParametros(String name , String category, int idAdmin);

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, sum(l.stock) as cantidad, m.photo as photo \n" +
            "from medicine m \n" +
            "left join lote l on (m.idMedicine=l.idMedicine) \n" +
            "where (m.name like concat(?1,'%') ) \n" +
            "and \n" +
            "l.idLote in ( \n" +
            "select l.idLote from lote  l  \n" +
            "inner join replacementorder r on (r.idReplacementOrder = l.idPedidosReposicion or l.idPedidosReposicion is null) \n" +
            "where l.site = (select site from administrator where idAdministrator=?2)  and r.trackingState = 'Entregado' and l.visible= true \n" +
            ") \n" +
            "group by m.idMedicine")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorNombre(String name , int idAdmin);


    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, sum(l.stock) as cantidad, m.photo as photo \n" +
            "from medicine m \n" +
            "left join lote l on (m.idMedicine=l.idMedicine) \n" +
            "where ( m.category like concat(?1 , '%') ) \n" +
            "and \n" +
            "l.idLote in ( \n" +
            "select l.idLote from lote  l  \n" +
            "inner join replacementorder r on (r.idReplacementOrder = l.idPedidosReposicion or l.idPedidosReposicion is null) \n" +
            "where l.site = (select site from administrator where idAdministrator=?2)  and r.trackingState = 'Entregado' and l.visible= true \n" +
            ") \n" +
            "group by m.idMedicine")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorCategory( String category, int idAdmin);


    @Query(nativeQuery = true, value =
            "SELECT m.idMedicine AS idMedicine, m.name AS nombreMedicamento, m.category AS categoria, " +
                    "COUNT(m.idMedicine) AS cantLote, TRUNCATE(m.price, 2) AS precio, SUM(l.stock) AS cantidad, m.photo AS photo " +
                    "FROM medicine m " +
                    "LEFT JOIN lote l ON m.idMedicine = l.idMedicine " +
                    "WHERE l.idLote IN ( " +
                    "    SELECT l2.idLote " +
                    "    FROM lote l2 " +
                    "    LEFT JOIN replacementorder r ON r.idReplacementOrder = l2.idPedidosReposicion " +
                    "    LEFT JOIN trackings t ON r.idtrackings = t.idtrackings " +
                    "    WHERE l2.site = (SELECT site FROM administrator WHERE idAdministrator = ?1) " +
                    "    AND (r.trackingState = 'Entregado' OR r.idReplacementOrder IS NULL) " +
                    "    AND l2.visible = true " +
                    ") " +
                    "GROUP BY m.idMedicine, m.name, m.category, m.price, m.photo " +
                    "HAVING SUM(l.stock) <= 25"
    )
    List<medicamentosPorSedeDTO> listaMedicamentosPocoStock(int idAdmin);
    /*and r.trackingState = 'Entregado'*/
    //listaMedicamentosBuscadorConStockLimintado

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, m.photo as photo \n" +
            ", sum(l.stock) as cantidad \n" +
            "from medicine m \n" +
            "left join lote l on (m.idMedicine=l.idMedicine) \n" +
            "where  (m.name like concat(?2,'%') and m.category like concat(?3 , '%') )  and l.idLote in ( \n" +
            "select l.idLote from lote  l  \n" +
            "inner join replacementorder r on (r.idReplacementOrder = l.idPedidosReposicion or l.idPedidosReposicion is null) \n" +
            "where l.site = (select site from administrator where idAdministrator=?1)  and r.trackingState = 'Entregado' and l.visible= true \n" +
            ")\n" +
            "group by m.idMedicine \n" +
            "having (sum(l.stock)<=25)")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorConStockLimitadoDosParametros(  int idAdmin , String nombre, String categoria);

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, m.photo as photo \n" +
            ", sum(l.stock) as cantidad \n" +
            "from medicine m \n" +
            "left join lote l on (m.idMedicine=l.idMedicine) \n" +
            "where  (m.name like concat(?2,'%') )  and l.idLote in ( \n" +
            "select l.idLote from lote  l  \n" +
            "inner join replacementorder r on (r.idReplacementOrder = l.idPedidosReposicion or l.idPedidosReposicion is null) \n" +
            "where l.site = (select site from administrator where idAdministrator=?1)  and r.trackingState = 'Entregado' and l.visible= true \n" +
            ")\n" +
            "group by m.idMedicine \n" +
            "having (sum(l.stock)<=25)")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorConStockLimitadoNombre(  int idAdmin , String nombre);

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, m.photo as photo \n" +
            ", sum(l.stock) as cantidad \n" +
            "from medicine m \n" +
            "left join lote l on (m.idMedicine=l.idMedicine) \n" +
            "where  ( m.category like concat(?2 , '%') )  and l.idLote in ( \n" +
            "select l.idLote from lote  l  \n" +
            "inner join replacementorder r on (r.idReplacementOrder = l.idPedidosReposicion or l.idPedidosReposicion is null) \n" +
            "where l.site = (select site from administrator where idAdministrator=?1)  and r.trackingState = 'Entregado' and l.visible= true \n" +
            ")\n" +
            "group by m.idMedicine \n" +
            "having (sum(l.stock)<=25)")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorConStockLimitadoCategory(  int idAdmin , String categoria);

    /*Rol Farmacista*/
    @Query(nativeQuery = true, value =
            "SELECT m.description AS description, m.idMedicine AS idMedicine, m.name AS nombreMedicamento, m.category AS categoria, m.photo AS photo, " +
                    "COUNT(m.idMedicine) AS cantLote, TRUNCATE(m.price, 2) AS precio, SUM(l.stock) AS cantidad " +
                    "FROM medicine m " +
                    "LEFT JOIN lote l ON m.idMedicine = l.idMedicine " +
                    "WHERE l.idLote IN ( " +
                    "    SELECT l2.idLote " +
                    "    FROM lote l2 " +
                    "    LEFT JOIN replacementorder r ON r.idreplacementorder = l2.idPedidosReposicion " +
                    "    LEFT JOIN trackings t ON r.idtrackings = t.idtrackings " +
                    "    WHERE l2.site = (SELECT site FROM pharmacist WHERE idPharmacist = ?1) " +
                    "    AND (r.trackingState = 'Entregado' OR l2.idPedidosReposicion IS NULL) " +
                    "    AND l2.visible = true " +
                    ") " +
                    "GROUP BY m.idMedicine, m.description, m.name, m.category, m.photo, m.price"
    )
    List<medicamentosPorSedeDTO> listaMedicamentosPorSedeFarmacista(int idPharmacist);




    @Query(nativeQuery = true, value="SELECT\n" +
            "    m.idMedicine,\n" +
            "    m.name AS medicineName,m.photo as photo,\n" +
            "    SUM(purchase_has_lot.cantidad_comprar) AS cantidad, m.price*SUM(purchase_has_lot.cantidad_comprar) as precio\n" +
            "FROM\n" +
            "purchaseorder po\n" +
            "INNER JOIN\n" +
            "    purchasehaslot purchase_has_lot ON po.idPurchaseOrder = purchase_has_lot.idPurchase\n" +
            "INNER JOIN\n" +
            "    lote l ON purchase_has_lot.idLote = l.idLote\n" +
            "INNER JOIN\n" +
            "    medicine m ON l.idMedicine = m.idMedicine\n" +
            "WHERE\n" +
            "    po.idPurchaseOrder = ?1\n" +
            "GROUP BY\n" +
            "    m.idMedicine, m.name")
    List<MeciamentosPorCompraDTO> listaMedicamentosPorCompra(int idPurchase);


    //admin Sede estadisticas

    @Query(nativeQuery = true, value ="select sum(phl.cantidad_comprar*m.price) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado'")
    Double gananciaTotal();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar*m.price) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 1'")
    Double gananciaTotalPando1();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar*m.price) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 2'")
    Double gananciaTotalPando2();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar*m.price) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 3'")
    Double gananciaTotalPando3();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar*m.price) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 4'")
    Double gananciaTotalPando4();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado'")
    int cantMedicamentosVendidos();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 1'")
    int cantMedicamentosVendidosPando1();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 2'")
    int cantMedicamentosVendidosPando2();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 3'")
    int cantMedicamentosVendidosPando3();

    @Query(nativeQuery = true, value="select sum(phl.cantidad_comprar) as Ganancia from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' and l.site='Pando 4'")
    int cantMedicamentosVendidosPando4();

    //select l.idMedicine, m.price, phl.cantidad_comprar,m.name from purchasehaslot phl left join purchaseorder po on  po.idPurchaseOrder = phl.idPurchase left join lote l on l.idLote = phl.idLote inner join medicine m on m.idMedicine=l.idMedicine where po.statePaid = 'pagado' order by phl.cantidad_comprar desc;

}
