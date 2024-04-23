package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Repository.MedicineRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuperAdminController {


    final MedicineRepository medicineRepository;

    public SuperAdminController(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;

    }

    @GetMapping("/listaMedicamentos")
    public String listar(Model model) {

        model.addAttribute("listaMedicamentos", medicineRepository.findAll());
        return "superAdmin/listaMedicamentos";
    }



}
