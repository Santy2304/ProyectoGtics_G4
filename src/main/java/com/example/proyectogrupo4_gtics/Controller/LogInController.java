package com.example.proyectogrupo4_gtics.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogInController {
    @GetMapping("/inicioSesion")
    public String InicioSesionController(){
        return "signin";
    }
    //Validar cuenta superadmin

}
