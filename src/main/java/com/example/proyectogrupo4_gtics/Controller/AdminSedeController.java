package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Doctor;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Repository.DoctorRepository;
import com.example.proyectogrupo4_gtics.Repository.MedicineRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminSedeController {
    final DoctorRepository doctorRepository;
    final PharmacistRepository pharmacistRepository;
    final MedicineRepository medicineRepository;
    public AdminSedeController(DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, MedicineRepository medicineRepository) {
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.medicineRepository = medicineRepository;
    }


    @GetMapping("/listaDoctoresAdminSede")
    public String listDoctors(Model model) {
        model.addAttribute("listaDoctores", doctorRepository.listaDoctorPorSede());
        return "admin_sede/doctorlist";
    }
    @GetMapping("listaFarmacistaAdminSede")
    public String listPharmacist(Model model) {
        model.addAttribute("listaFarmacista", pharmacistRepository.listaFarmacistaPorSede());
        return "admin_sede/pharmacistlist";
    }
    /*Linkear las demás vistas*/
    @GetMapping("dashboardAdminSede")
    public String verDashboard(Model model) {
        return "admin_sede/Dashboard";
    }

    @GetMapping("inventarioAdminSede")
    public String verInventario(Model model) {
        return "admin_sede/inventario";
    }

    @GetMapping("verAddPharmacist")
    public String verAddPharmacist(Model model) {
        return "admin_sede/addpharmacist";
    }

    @GetMapping("verListaReposicion")
    public String listaReposicion(Model model) {
        return "admin_sede/listaReposicion";
    }

    @GetMapping("/verNotificaciones")
    public String notificaciones(Model model) {
        return "admin_sede/notifications";
    }
    @GetMapping("/verPerfil")
    public String profile(Model model){
        return "admin_sede/profile";
    }
    @GetMapping("/verSolicitudReposicion")
    public String solicitudReposicion(Model model){
        return "admin_sede/reposicion";
    }
}
