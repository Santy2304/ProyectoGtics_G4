package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.MeciamentosPorCompraDTO;
import com.example.proyectogrupo4_gtics.DTOs.cantidadMedicamentosDTO;
import com.example.proyectogrupo4_gtics.DTOs.medicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine,Integer> {
    @Query(nativeQuery = true, value = "select m.idMedicine as idMedicine,  m.name as nombreMedicamento, m.category as categoria, m.price as precio, l.stock as cantidad from medicine m inner join lote l on (m.idMedicine=l.idMedicine and l.site = (SELECT name FROM site where idSite= ?1))")
    List<medicamentosPorSedeDTO> getMedicineBySite(int idSede);


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



    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update medicine set name=?1, category=?2, price=?3,description=?4  where idMedicine = ?5")
    void actualizarMedicine(String name,String category,Double price,String description ,int idMedicine);


    /*Rol administrador de sede*/
    @Query(nativeQuery = true, value="select m.description as description, m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, sum(l.stock) as cantidad from medicine m left join lote l on (m.idMedicine=l.idMedicine) where l.site = (select site from administrator where idAdministrator=?1) group by m.idMedicine\n")
    List<medicamentosPorSedeDTO> listaMedicamentosPorSede(int idAdmin);

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio, sum(l.stock) as cantidad \n" +
            "from medicine m left join lote l on (m.idMedicine=l.idMedicine) where (m.name like concat(?1,'%') or m.category like concat(?2 , '%') ) and l.site = (select site from administrator where idAdministrator=?3) group by m.idMedicine\n")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscador(String name , String category, int idAdmin);

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio , sum(l.stock) as cantidad from medicine m left join lote l on (m.idMedicine=l.idMedicine) where l.site = (select site from administrator where idAdministrator=?1) group by m.idMedicine having sum(l.stock)<=25")
    List<medicamentosPorSedeDTO> listaMedicamentosPocoStock(int idAdmin);
    //listaMedicamentosBuscadorConStockLimintado

    @Query(nativeQuery = true, value="select m.idMedicine as idMedicine, m.name as nombreMedicamento,m.category as categoria, count(m.name) as cantLote, TRUNCATE(m.price,2) as precio , sum(l.stock) as cantidad from medicine m left join lote l on (m.idMedicine=l.idMedicine) where (m.name like concat(?2,'%') or m.category like concat(?3 , '%') ) and l.site = (select site from administrator where idAdministrator=?1) group by m.idMedicine having (sum(l.stock)<=25)")
    List<medicamentosPorSedeDTO> listaMedicamentosBuscadorConStockLimintado(  int idAdmin , String nombre, String categoria);


    @Query(nativeQuery = true, value="SELECT\n" +
            "    m.idMedicine,\n" +
            "    m.name AS medicineName,\n" +
            "    SUM(purchase_has_lot.cantidad_comprar) AS cantidad\n" +
            "FROM\n" +
            "    purchaseorder po\n" +
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

}
