package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.AdministratorRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.SiteRepository;
import com.example.proyectogrupo4_gtics.Repository.SuperAdminRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

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

    public class MyUser {
        private String correo;
        private String password;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/iniciarSesion")
    public Object iniciarSesion(@RequestBody String user , Model model){
        System.out.println(user);
        String correo = null;
        String password = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(user);
            correo = node.get("correo").asText();password = node.get("password").asText();

        } catch (JsonProcessingException e) {
            return "signin";
        }
        Patient patient = patientRepository.buscarPatient(correo , password);
        Administrator admin = administratorRepository.buscarAdmin(correo , password) ;
        SuperAdmin superAdmin = superAdminRepository.buscarSuperAdmin(correo , password) ;
        Pharmacist pharmacist = pharmacistRepository.buscarPharmacist(correo, password);
        if((patient == null)  && (admin == null) && (superAdmin == null) && (pharmacist == null)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("noIsUser");
        }else {
            if(!(patient == null)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/ElegirSede?idUser=" + patient.getIdPatient());
            }
            if( !(admin == null) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/listaDoctoresAdminSede?idUser=" + admin.getIdAdministrador());
            }
            if( !(superAdmin == null) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/listaMedicamentosSuperAdmin?idUser=" + superAdmin.getIdSuperAdmin());
            }
            if( !(pharmacist == null) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/verMedicinelistFarmacista?idUser=" + pharmacist.getIdFarmacista());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("isUser");
        }/*else{
            if(!(patient == null)){
                model.addAttribute("usuario" , patient);
                return "redirect:/ElegirSede";
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
        }*/
    }

    @PostMapping("/validarUsuario")
    public Object validarUsuario(@RequestBody String user ,Model  model){
        System.out.println(user);
        String correo = null;
        String password = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(user);
            correo = node.get("correo").asText();password = node.get("password").asText();

        } catch (JsonProcessingException e) {
            return "signin";
        }
        Patient patient = patientRepository.buscarPatient(correo , password);
        Administrator admin = administratorRepository.buscarAdmin(correo , password) ;
        SuperAdmin superAdmin = superAdminRepository.buscarSuperAdmin(correo , password) ;
        Pharmacist pharmacist = pharmacistRepository.buscarPharmacist(correo, password);

        if(!(patient == null)){
            model.addAttribute("usuario" , patient);
            return "redirect:/ElegirSede";
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
        return "signin";
    }
    @GetMapping("/forgetPassword")
    public String forgetPassword(){
        return "forgetpassword";
    }
    @GetMapping("/crearCuenta")
    public String signUp(){
        return "signup";
    }


}
