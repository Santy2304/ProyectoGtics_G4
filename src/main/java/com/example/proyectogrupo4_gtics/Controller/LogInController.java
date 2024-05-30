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
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public LogInController (SiteRepository siteRepository , PatientRepository patientRepository , PharmacistRepository pharmacistRepository , SuperAdminRepository superAdminRepository , AdministratorRepository administratorRepository, AdminSedeController adminSedeController) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.superAdminRepository = superAdminRepository;
        this.administratorRepository =administratorRepository;
        this.pharmacistRepository = pharmacistRepository;
    }
    @GetMapping("/inicioSesion")
    public String InicioSesionController(HttpSession http ){

        if(http.getAttribute("usuario") != null){
            if((http.getAttribute("usuario")) instanceof Administrator ){
                return "redirect:/adminSede/dashboardAdminSede";
            }
            if((http.getAttribute("usuario")) instanceof Pharmacist ){
                return "redirect:/pharmacist/verMedicinelist";
            }
            if((http.getAttribute("usuario")) instanceof Patient ){
                return "redirect:/patient/verPrincipalPaciente";
            }
            if((http.getAttribute("usuario")) instanceof SuperAdmin ){
                return "redirect:/superAdmin/verListados";
            }
        }else{
            return "signin";
        }
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/patient/ElegirSede?idUser=" + patient.getIdPatient());
            }
            if( !(admin == null) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/adminSede/dashboardAdminSede?idUser=" + admin.getIdAdministrador());
            }
            if( !(superAdmin == null) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/superAdmin/listaMedicamentosSuperAdmin?idUser=" + superAdmin.getIdSuperAdmin());
            }
            if( !(pharmacist == null) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("/pharmacist/verMedicinelistFarmacista?idUser=" + pharmacist.getIdFarmacista());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("isUser");
        }
    }

    @RequestMapping ("/validarUsuario")
    @ResponseBody
    public Map<String,String> validarUsuario(MyUser user ,Model  model){
        System.out.println(user);
        String correo = user.getEmail();
        String password = user.getPassword();
        Patient patient = patientRepository.buscarPatient(correo , password);
        Administrator admin = administratorRepository.buscarAdmin(correo , password) ;
        SuperAdmin superAdmin = superAdminRepository.buscarSuperAdmin(correo , password) ;
        Pharmacist pharmacist = pharmacistRepository.buscarPharmacist(correo, password);
        Map<String, String > response =  new HashMap<>();
        response.put("response" ,"noIsUser");
        if(!(patient == null)){
            if( ! patient.getState().equals("baneado")) {
                response.put("response", "/patient/sessionPatient?idUser=" + patient.getIdPatient());
                model.addAttribute("idUser", patient.getIdPatient());
                return response;
            }else{
                response.put("response", "baneado");
            }
        }
        if( !(admin == null) ) {
            if( ! admin.getState().equals("baneado")) {
                response.put("response" ,"/adminSede/sessionAdmin?idUser="+admin.getIdAdministrador());
                model.addAttribute("idUser" , admin.getIdAdministrador());
                return response;
            }else{
                response.put("response", "baneado");
            }
        }
        if( !(superAdmin == null) ) {
            response.put("response" ,"/superAdmin/verListadosSuperAdmin?idUser="+ superAdmin.getIdSuperAdmin());
            model.addAttribute("idUser" , superAdmin.getIdSuperAdmin());
            return response;
        }
        if( !(pharmacist == null) ) {
            if( ! pharmacist.getState().equals("baneado")) {
                response.put("response" ,"/pharmacist/sessionPharmacist?idUser="+pharmacist.getIdFarmacista());
                model.addAttribute("idUser" , pharmacist.getIdFarmacista());
                return response;
            }else{
                response.put("response", "baneado");
            }
        }
        return response;
    }
    @GetMapping("/forgetPassword")
    public String forgetPassword(){
        return "forgetpassword";
    }
    /*Cambiar contraseña sin enviar correo*/
    @GetMapping("/changePassword")
    public String verChangePassword(Model model){
        return "changePassword";
    }
    @PostMapping("/changingPassword")
    public String changingPassword(Model model, @RequestParam("email") String correo, @RequestParam("password") String newPassword) {
        //String correo = (String) model.getAttribute("email");
        Patient patient = patientRepository.buscarPatientEmail(correo);
        Pharmacist pharmacist = pharmacistRepository.findByEmail(correo);
        Administrator admin = administratorRepository.findByEmail(correo);
        SuperAdmin superAdmin = superAdminRepository.findByEmail(correo);
        if (!(patient == null)) {
            patientRepository.actualizarContrasena(newPassword, correo);

        }
        if (!(admin == null)) {
            administratorRepository.actualizarContrasena(newPassword, correo);

        }
        if (!(superAdmin == null)) {
            superAdminRepository.actualizarContrasena(newPassword, correo);

        }
        if (!(pharmacist == null)) {
            pharmacistRepository.actualizarContrasena(newPassword, correo);

        }
        return "redirect:/inicioSesion";
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
            patient.setDateCreationAccount( LocalDate.now());
            patient.setState("activo");
            patientRepository.save(patient);
            response.put("response" ,"Guardado");
        }else{
            response.put("response" ,"YaExiste");
        }

        return response;
    }
    //Vamos a crear un servicio Rest para consumir autenticacion


}
