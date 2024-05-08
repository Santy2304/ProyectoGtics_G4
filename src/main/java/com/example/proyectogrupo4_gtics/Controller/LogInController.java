package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.AdministratorRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.SiteRepository;
import com.example.proyectogrupo4_gtics.Repository.SuperAdminRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;



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
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String correo) {
            this.email = correo;
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
        System.out.println(user);
        try {
            JsonNode node = mapper.readTree(user);
            correo = node.get("correo").asText();
            password = node.get("password").asText();
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/dashboardAdminSede?idUser=" + admin.getIdAdministrador());
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

    @RequestMapping ("/validarUsuario")
    @ResponseBody
    public Map<String,String> validarUsuario(MyUser user ,Model  model){
        System.out.println("HOLAAA");
        System.out.println(user);
        String correo = user.getEmail();
        String password = user.getPassword();
        Patient patient = patientRepository.buscarPatient(correo , password);
        Administrator admin = administratorRepository.buscarAdmin(correo , password) ;
        SuperAdmin superAdmin = superAdminRepository.buscarSuperAdmin(correo , password) ;
        Pharmacist pharmacist = pharmacistRepository.buscarPharmacist(correo, password);
        Map<String, String > response =  new HashMap<>();

        if(!(patient == null)){
            response.put("response" ,"/sessionPatient?idUser="+patient.getIdPatient());
            model.addAttribute("idUser" , patient.getIdPatient());
            return response;
        }
        if( !(admin == null) ) {
            response.put("response" ,"/sessionAdmin?idUser="+admin.getIdAdministrador());
            model.addAttribute("idUser" , admin.getIdAdministrador());
            return response;
        }
        if( !(superAdmin == null) ) {
            response.put("response" ,"/verListadosSuperAdmin?idUser="+ superAdmin.getIdSuperAdmin());
            model.addAttribute("idUser" , superAdmin.getIdSuperAdmin());
            return response;
        }
        if( !(pharmacist == null) ) {
            response.put("response" ,"/sessionPharmacist?idUser="+pharmacist.getIdFarmacista());
            model.addAttribute("idUser" , pharmacist.getIdFarmacista());
            return response;
        }
        response.put("response" ,"noIsUser");
        return response;
    }
    @GetMapping("/forgetPassword")
    public String forgetPassword(){
        return "forgetpassword";
    }
    @GetMapping("/crearCuenta")
    public String signUp(){
        return "signup";
    }


    @RequestMapping(value = "/formNuevaCuenta")
    @ResponseBody
    public Map<String,String > formNuevaCuenta(Patient patient){
        System.out.println("Holaa");
        Map<String, String > response =  new HashMap<>();
        Optional<Patient> patientOpt1 =  patientRepository.findByEmail(patient.getEmail());
        Optional<Patient> patientOpt2 =  patientRepository.findByDni(patient.getDni());
        if(!patientOpt1.isPresent() && !patientOpt2.isPresent()){
            patient.setPassword("DefaultPassword");
            patient.setChangePassword(1);
            patient.setDateCreationAccount( LocalDateTime.now());
            patientRepository.save(patient);
            response.put("response" ,"Guardado");
        }else{
            response.put("response" ,"YaExiste");
        }

        return response;
    }


}
