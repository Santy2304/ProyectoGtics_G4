package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import com.example.proyectogrupo4_gtics.Repository.DoctorRepository;
import com.example.proyectogrupo4_gtics.Repository.MedicineRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/listaFarmacistaAdminSede")
    public String listPharmacist(Model model) {
        model.addAttribute("listaFarmacista", pharmacistRepository.listaFarmacistaPorSede());
        return "admin_sede/pharmacistlist";
    }
    @GetMapping("/verAddPharmacist")
    public String verAddPharmacist() {
        return "admin_sede/addpharmacist";
    }

    @PostMapping("/agregarFarmacista")
    public String agregarFarmacista(@RequestParam("nameFarmacista") String nameFarmacista,
                                   @RequestParam("apellidoFarmacista")String apellidoFarmacista,
                                   @RequestParam("dniFarmacista") String dniFarmacista,
                                   @RequestParam("codigoFarmacista") String codeFarmacista,
                                   @RequestParam("correoFarmacista") String correoFarmacista,
                                   @RequestParam("distritoFarmacista") String distritoFarmacista,
                                   @RequestParam("contrasenaFarmacista") String contrasenaFarmacista,
                                   @RequestParam("photoFarmacista") String fotoFarmacista,
                                   /*@RequestParam("farmacistaId") int idFarmacista,*/
                                   Model model){
        Pharmacist farmacista = new Pharmacist();
        /*farmacista.setIdFarmacista(idFarmacista);*/
        farmacista.setName(nameFarmacista);
        farmacista.setLastName(apellidoFarmacista);
        farmacista.setDni(dniFarmacista);
        farmacista.setCode(codeFarmacista);
        farmacista.setEmail(correoFarmacista);
        farmacista.setDistrit(distritoFarmacista);
        farmacista.setPassword(contrasenaFarmacista);
        farmacista.setPhoto(fotoFarmacista);
        pharmacistRepository.save(farmacista);

        model.addAttribute("pharmacist", farmacista);

        return "redirect:/listaFarmacistaAdminSede";
    }
    /*Linkear las demás vistas*/
    @GetMapping("/dashboardAdminSede")
    public String verDashboard(Model model) {
        return "admin_sede/dashboard";
    }

    @GetMapping("/inventarioAdminSede")
    public String verInventario(Model model) {
        return "admin_sede/inventario";
    }






    @GetMapping("/verListaReposicion")
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
