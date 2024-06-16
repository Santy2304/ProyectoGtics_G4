package com.example.proyectogrupo4_gtics.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

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
