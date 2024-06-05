package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {


    User findByEmail(String email);


    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="update users set password= ?1, email=?2 where idUsers=?3")
    void actualizar(String pswrd, String email,int id);

    @Query(nativeQuery = true, value = "SELECT idUsers FROM users WHERE email=?1")
    int encontrarId(String email);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="update users set email=?1 where idUsers=?2")
    void actualizarEmail(String email,int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value="update users set password= ?1 where email=?2")
    void actualizarPassword(String pswrd, String email);


}
