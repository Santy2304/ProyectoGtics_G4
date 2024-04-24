package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Repository.DoctorRepository;
import com.example.proyectogrupo4_gtics.Repository.MedicineRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SuperAdminController {


    final MedicineRepository medicineRepository;
    final PatientRepository patientRepository;
    final DoctorRepository doctorRepository;
    public SuperAdminController(MedicineRepository medicineRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/listaMedicamentos")
    public String listar(Model model) {

        model.addAttribute("listaMedicamentos", medicineRepository.obtenerDatosMedicamentos());

        return "superAdmin/listaMedicamentos";
    }





}
