package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.Service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
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

    final RolRepository rolRepository;

    final UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    public LogInController (SiteRepository siteRepository , PatientRepository patientRepository , PharmacistRepository pharmacistRepository ,
                            SuperAdminRepository superAdminRepository , AdministratorRepository administratorRepository,
                            UserRepository userRepository, RolRepository rolRepository ) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.superAdminRepository = superAdminRepository;
        this.administratorRepository =administratorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
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
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(newPassword);

        if (!(patient == null)) {
            patientRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,correo);
        }
        if (!(admin == null)) {
            administratorRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,correo);
        }
        if (!(superAdmin == null)) {
            superAdminRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,correo);
        }
        if (!(pharmacist == null)) {
            pharmacistRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,correo);
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
            patient.setChangePassword(false);
            patient.setDateCreationAccount( LocalDate.now());
            patient.setState("activo");
            patientRepository.save(patient);
            User user = new User();
            Rol rol = new Rol();
            String password = generateRandomWord();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encryptedPassword = passwordEncoder.encode(password);
            user.setEmail(patient.getEmail());
            user.setPassword(encryptedPassword);
            user.setState(true);
            rol = rolRepository.findById(4).get();
            user.setIdRol(rol);
            userRepository.save(user);

            emailService.sendSimpleMessage(
                    patient.getEmail(),
                    "Bienvenido a Nuestro Servicio",
                    "Su cuenta ha sido creada exitosamente. Su contraseña inicial es: " + password
            );


            response.put("response" ,"Guardado");
        }else{
            response.put("response" ,"YaExiste");
        }

        return response;
    }
    //Vamos a crear un servicio Rest para consumir autenticacion

    public String generateRandomWord() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String characters = letters + numbers;
        int wordLength = 8;
        Random random = new SecureRandom();
        StringBuilder word = new StringBuilder(wordLength);

        // Ensure at least one letter
        word.append(letters.charAt(random.nextInt(letters.length())));

        // Ensure at least one number
        word.append(numbers.charAt(random.nextInt(numbers.length())));

        // Fill the rest of the word with random characters
        for (int i = 2; i < wordLength; i++) {
            word.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Shuffle the characters to ensure randomness
        char[] wordArray = word.toString().toCharArray();
        for (int i = 0; i < wordArray.length; i++) {
            int randomIndex = random.nextInt(wordArray.length);
            char temp = wordArray[i];
            wordArray[i] = wordArray[randomIndex];
            wordArray[randomIndex] = temp;
        }

        return new String(wordArray);
    }


}
