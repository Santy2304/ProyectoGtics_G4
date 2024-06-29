package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Dao.DniDao;
import com.example.proyectogrupo4_gtics.Dao.PersonaDni;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.Service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public String InicioSesionController(HttpSession http , Model model){
        model.addAttribute("FechaNube", LocalDate.now());

        if(http.getAttribute("usuario") != null){
            if((http.getAttribute("usuario")) instanceof Administrator ){
                return "redirect:/adminSede/dashboardAdminSede";
            }
            if((http.getAttribute("usuario")) instanceof Pharmacist ){
                return "redirect:/pharmacist/verMedicinelist";
            }
            if((http.getAttribute("usuario")) instanceof Patient ){
                return "redirect:/patient/ElegirSede";
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



    @GetMapping("/forgetPassword")
    public String forgetPassword(){
        return "forgetpassword";
    }


    @PostMapping("/enviarEmailForget")
    public String enviarCorreoForgot(@RequestParam("email") String email,HttpSession httpSession){
        Map<String, String > response =  new HashMap<>();
        try {
            httpSession.setAttribute("resetEmail", email);

            emailService.sendHtmlForgetPassword(email, "Recuperación de Contraseña");
            response.put("response", "Guardado");

        } catch (MessagingException | IOException e) {
            response.put("response", "Error al enviar el correo");
            e.printStackTrace();
        }
        return "redirect:inicioSesion";
    }

    /*Cambiar contraseña sin enviar correo*/
    @GetMapping("/changePassword")
    public String verChangePassword(Model model){
        return "changePassword";
    }
    @PostMapping("/changingPassword")
    public String changingPassword(HttpSession httpSession,@RequestParam("confirmarContrasena") String newPassword) {
        //String correo = (String) model.getAttribute("email");
        String email = (String) httpSession.getAttribute("resetEmail");

        Patient patient = patientRepository.buscarPatientEmail(email);
        Pharmacist pharmacist = pharmacistRepository.findByEmail(email);
        Administrator admin = administratorRepository.findByEmail(email);
        SuperAdmin superAdmin = superAdminRepository.findByEmail(email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(newPassword);

        if (!(patient == null)) {
          //  patientRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,email);
        }
        if (!(admin == null)) {
           // administratorRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,email);
        }
        if (!(superAdmin == null)) {
            superAdminRepository.actualizarContrasena(newPassword, email);
            userRepository.actualizarPassword(encryptedPassword,email);
        }
        if (!(pharmacist == null)) {
           // pharmacistRepository.actualizarContrasena(newPassword, correo);
            userRepository.actualizarPassword(encryptedPassword,email);
        }
        return "redirect:/inicioSesion";
    }

    @GetMapping("/crearCuenta")
    public String signUp(){
        return "signup";
    }

    @RequestMapping(value = "/formNuevaCuenta")
    @ResponseBody
    public Map<String,String> formNuevaCuenta(Patient patient){
        System.out.println("Holaa");
        Map<String, String > response =  new HashMap<>();
        Optional<Patient> patientOpt1 =  patientRepository.findByEmail(patient.getEmail());
        Optional<Patient> patientOpt2 =  patientRepository.findByDni(patient.getDni());
        if(!patientOpt1.isPresent() && !patientOpt2.isPresent()){
            patient.setChangePassword(false);
            patient.setDateCreationAccount( LocalDate.now());
            patient.setState("activo");
            patient.setExpirationDate(LocalDateTime.now().plusMinutes(4)); // Expira en 10 minutos
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

          //  emailService.sendSimpleMessage(
        //            patient.getEmail(),
      //              "Bienvenido a Nuestro Servicio",
    //                "Su cuenta ha sido creada exitosamente. Su contraseña inicial es: " + password
  //          );

            try {
                emailService.sendHtmlMessage(patient.getEmail(), "Bienvenido a SaintMedic", patient.getName(), password);
                response.put("response", "Guardado");
            } catch (MessagingException | IOException e) {
                response.put("response", "Error al enviar el correo");
                e.printStackTrace();
            }
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

    @GetMapping(value="/estaBaneado")
    public Object estaBaneado(@RequestParam("email") String  email , @RequestParam("password") String  password ){
        System.out.println("Hola Santiago");
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encryptedPassword = passwordEncoder.encode(password);
            int idUser = userRepository.encontrarId(email);
            System.out.println("Hola ");
            if((userRepository.findById(idUser)).isPresent()){
                User u = (userRepository.findById(idUser)).get();
                System.out.println(u.getEmail());
                System.out.println("Santiago");
                return ResponseEntity.ok(u);
            }else{
                System.out.println("ErrorFatal");
                HashMap<String, Object> er = new HashMap<>();
                er.put("error", "errorHola");
                er.put("date", "" + LocalDateTime.now());
                return ResponseEntity.badRequest().body(er);
            }
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest();
        }
    }


    @GetMapping(value="/getDni")
    public Object getDni(@RequestParam("dni") String  dni , Model model ) {
        try {
            PersonaDni p = new DniDao().buscarDatosPorDNI(dni);
            model.addAttribute("santiago",  p);
            return ResponseEntity.ok(p);
        } catch (Exception err) {
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "No se encontro el DNI");
            return ResponseEntity.badRequest().body(er);
        }
    }

}
