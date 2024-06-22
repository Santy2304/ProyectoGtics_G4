package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notifications {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnotifications", nullable = false)
    private Integer id;

    @Size(max = 120)
    @Column(name = "content", length = 120)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusers")
    private User idUsers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsite")
    private Site idSite;

    @Column(name = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime date;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Site getIdSite() {
        return idSite;
    }

    public void setIdSite(Site idSite) {
        this.idSite = idSite;
    }

    public User getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(User idUsers) {
        this.idUsers = idUsers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
