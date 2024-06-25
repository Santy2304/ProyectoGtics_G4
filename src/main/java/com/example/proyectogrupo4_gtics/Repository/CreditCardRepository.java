package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.DTOs.DoctorPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.CreditCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    @Query(nativeQuery = true, value = "select c.* from creditcard c inner join patient p on p.idPatient = c.idPatient where p.idPatient=?1")
    List<CreditCard> listaCreditCards(int idpatient);
    @Query(nativeQuery = true, value = "select * from creditcard where numberCard=?1")
    CreditCard encontrarCreditCard(String number);

    @Transactional
    @Modifying
    @Query(value = "update creditcard set prefered = 1  where idCreditCard=?1" , nativeQuery = true)
    void preferirPorid(int id);


}
