package com.example.proyectogrupo4_gtics.Repository;

import com.example.proyectogrupo4_gtics.Entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {


    @Query(nativeQuery = true, value = "select n.* from notifications n inner join site s on (n.idSite=s.idSite) where s.name = ?1 order by n.date desc")
    List<Notifications> notificacionesSede(String sede);

    @Query(nativeQuery = true, value = "select n.* from notifications n inner join users u on (n.idUsers=u.idUsers) where n.idUsers = ?1 order by n.date desc")
    List<Notifications> notificacionesUser(int id);


}
