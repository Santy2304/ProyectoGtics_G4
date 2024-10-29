package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.ReplacementOrderHasMedicine;
import com.example.proyectogrupo4_gtics.Entity.ReplacementOrderHasMedicineID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplacementOrderHasMedicineRepository extends JpaRepository<ReplacementOrderHasMedicine, ReplacementOrderHasMedicineID> {
}
