package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.AdministratorRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.SiteRepository;
import com.example.proyectogrupo4_gtics.Repository.SuperAdminRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@SessionAttributes("usuario")
public class LogInController {
    final SiteRepository siteRepository;
    final PatientRepository patientRepository;
    final SuperAdminRepository superAdminRepository;
    final AdministratorRepository administratorRepository;
    final PharmacistRepository pharmacistRepository;

    public LogInController (SiteRepository siteRepository ,PatientRepository patientRepository , PharmacistRepository pharmacistRepository ,SuperAdminRepository superAdminRepository , AdministratorRepository administratorRepository ) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.superAdminRepository = superAdminRepository;
        this.administratorRepository =administratorRepository;
        this.pharmacistRepository = pharmacistRepository;
    }
    @GetMapping("/inicioSesion")
    public String InicioSesionController(){
        return "signin";
    }
    //Validar cuenta superadmin

    @PostMapping("/validarUsuario")
    public Object validarUsuario(@RequestParam String correo , @RequestParam String password , Model model ){
        Patient patient = patientRepository.buscarPatient(correo , password);
        Administrator admin = administratorRepository.buscarAdmin(correo , password) ;
        SuperAdmin superAdmin = superAdminRepository.buscarSuperAdmin(correo , password) ;
        Pharmacist pharmacist = pharmacistRepository.buscarPharmacist(correo, password);
        if((patient == null)  || (admin == null) || (superAdmin == null) || (pharmacist == null)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else{

            if(!(patient == null)){
                model.addAttribute("idUser" , patient.getIdPatient());
                return "redirect:/elegirSedePrimeraVez";
            }
            if( !(admin == null) ) {
                model.addAttribute("idUser" , admin.getIdAdministrador());
                return "redirect:/listaDoctoresAdminSede";
            }
            if( !(superAdmin == null) ) {
                model.addAttribute("idUser" , superAdmin.getIdSuperAdmin());
                return "redirect:/listaMedicamentosSuperAdmin";
            }
            if( !(pharmacist == null) ) {
                model.addAttribute("idUser" , pharmacist.getIdFarmacista());
                return "redirect:/verMedicinelistFarmacista";
            }


        }
        return "";
    }


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
