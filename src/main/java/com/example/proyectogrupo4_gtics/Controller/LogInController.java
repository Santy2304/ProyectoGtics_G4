package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Site;
import com.example.proyectogrupo4_gtics.Repository.SiteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller
@SessionAttributes("Session")
public class LogInController {
    final SiteRepository siteRepository;

    public LogInController (SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @GetMapping("/inicioSesion")
    public String InicioSesionController(){
        return "signin";
    }
    //Validar cuenta superadmin
    @GetMapping("/ElegirSede")
    public String ElegirSede(Model model){
        //Se listan las sedes
        List<Site> listSite = siteRepository.findAll();
        model.addAttribute("listSite", listSite);
        return "elegirSede";
    }


}
