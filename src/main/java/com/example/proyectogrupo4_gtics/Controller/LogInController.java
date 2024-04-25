package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.example.proyectogrupo4_gtics.Entity.Site;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.SiteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller
@SessionAttributes("usuario")
public class LogInController {
    final SiteRepository siteRepository;
    final PatientRepository patientRepository;

    public LogInController (SiteRepository siteRepository ,PatientRepository patientRepository) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/inicioSesion")
    public String InicioSesionController(){
        return "signin";
    }
    //Validar cuenta superadmin
    @GetMapping("/ElegirSede")
    public String ElegirSede(Model model){
        //Se listan las sedes
        Patient usuario =  patientRepository.findByName("Hineill");
        model.addAttribute("usuario",usuario);
        System.out.println(usuario.getLastName());
        List<Site> listSite = siteRepository.findAll();
        model.addAttribute("listSite", listSite);
        return "elegirSede";
    }


}
