package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Lote;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LoteRepository extends JpaRepository<Lote, Integer> {

    List<Lote> findByMedicineIdMedicine(int idMedicine);

    @Query(nativeQuery = true, value = "SELECT l.site as sede, SUM(l.stock) as stock, l.visible FROM Lote l left join medicine m  on m.idMedicine= l.idMedicine AND l.visible = true where m.idMedicine=?1 GROUP BY l.site")
    List<LotesValidosporMedicamento> obtenerLotesValidosPorMedicamento(int idMedicine);


    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE lote SET visible = ?1 WHERE idMedicine = ?2 and site=?3")
    void actualizarVisibilidadSede(boolean visibilidad, int idMedicine,String sede);

}


