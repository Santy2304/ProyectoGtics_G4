package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Doctor;
import com.example.proyectogrupo4_gtics.Repository.DoctorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminSedeController {
    final DoctorRepository doctorRepository;

    public AdminSedeController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }


    @GetMapping("/listaDoctoresAdminSede")
    public String listDoctors(Model model) {
        model.addAttribute("listaDoctores", doctorRepository.listaDoctorPorSede());
        return "admin_sede/doctorlist";
    }

}
