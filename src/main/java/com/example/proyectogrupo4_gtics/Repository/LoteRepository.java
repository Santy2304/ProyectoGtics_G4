package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.lotesPorReposicion;
import com.example.proyectogrupo4_gtics.Entity.Lote;
import com.example.proyectogrupo4_gtics.Entity.ReplacementOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LoteRepository extends JpaRepository<Lote, Integer> {

    List<Lote> findByMedicineIdMedicine(int idMedicine);

    @Query(nativeQuery = true, value = "SELECT l.site as sede, SUM(l.stock) as stock, l.visible FROM lote l left join medicine m  on m.idMedicine= l.idMedicine AND l.visible = true where m.idMedicine=?1 GROUP BY l.site")
    List<LotesValidosporMedicamentoDTO> obtenerLotesValidosPorMedicamento(int idMedicine);


    @Query(nativeQuery = true, value = "select l.site from lote l inner join medicine m on m.idMedicine = l.idMedicine where l.idMedicine = ?1 and l.site =?2 GROUP BY l.site\n")
    List<String> obtenerLoteporSede(int idMedicine, String sede);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE lote SET visible = ?1 WHERE idMedicine = ?2 and site=?3")
    void actualizarVisibilidadSede(boolean visibilidad, int idMedicine,String sede);


    @Query(nativeQuery = true, value = "SELECT l.idLote as id , m.name as name, m.category as category , m.price as price , m.description as description, l.initial_quantity as  initial FROM lote l inner join medicine m  WHERE (idPedidosReposicion =?1 and l.idMedicine = m.idMedicine )")
    List<lotesPorReposicion> getLoteByReplacementOrderId(int idReplacementOrder);


    @Query(nativeQuery = true, value = "SELECT * FROM lote WHERE idMedicine=?1 and stock>=?2 and site = ?3 and visible=1")
    List<Lote> listarLotesPosibles(int idMedicine, int stock, String site);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE lote SET initial_quantity = ?2 WHERE  idLote=?1")
    void actualizarCantidadInicial(int idLote , int initial);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE lote SET stock = (stock - ?2) WHERE  idLote=?1")
    void actualizarStockLote(int idLote , int cantidadVendida);


    @Query(nativeQuery = true, value = "SELECT l.* FROM lote l left join replacementorder r on (l.idPedidosReposicion = r.idReplacementOrder)" +
            "WHERE l.idMedicine=?1 and l.stock>=?2 and l.site = ?3 and l.visible=1 AND (r.trackingState = 'Entregado' OR l.idPedidosReposicion IS NULL)")
    List<Lote> listarLotesPosiblesV2(int idMedicine, int stock, String site);

}


