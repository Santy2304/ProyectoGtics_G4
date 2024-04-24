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

    @GetMapping("/listaMedicamentosSuperAdmin")
    public String listarMedicamentos(Model model) {

        model.addAttribute("listaMedicamentos", medicineRepository.obtenerDatosMedicamentos());

        return "superAdmin/listaMedicamentos";
    }
    //Solo para poder saltar entre vistas auxiliar de momento
    @GetMapping("/verSedeSuperAdmin")
    public String verSede(Model model) {
        return "superAdmin/Sede";
    }
    @GetMapping("/verUserListSuperAdmin")
    public String verUserList() {
        return "superAdmin/userlist";
    }
    @GetMapping("/verPerfilSuperAdmin")
    public String verPerfil() {
        return "superAdmin/perfil";
    }
    @GetMapping("/verNotificationsSuperAdmin")
    public String verNotifications() {
        return "superAdmin/notifications";
    }
    @GetMapping("/verListadosSuperAdmin")
    public String verListados() {
        return "superAdmin/listados";
    }
    @GetMapping("/verEditarMedicamentoSuperAdmin")
    public String verEditarMedicamento() {
        return "superAdmin/editarMedicamento";
    }
    @GetMapping("/verEditarFarmacistaSuperAdmin")
    public String verEditarFarmacista() {
        return "superAdmin/EditarFarmacista";
    }
    @GetMapping("/verEditarDoctorSuperAdmin")
    public String verEditarDoctor() {
        return "superAdmin/EditarDoctor";
    }
    @GetMapping("/verEditarAdministradorSuperAdmin")
    public String verEditarAdministrador() {
        return "superAdmin/EditarAdministrador";
    }
    @GetMapping("/verDetallesProductoSuperAdmin")
    public String verDetallesProducto() {
        return "superAdmin/detallesProducto";
    }
    @GetMapping("/verDetalleMedicamentosSuperAdmin")
    public String verDetalleMedicamentos() {
        return "superAdmin/DetalleMedicamentos";
    }
    @GetMapping("/verAñadirMedicamentoSuperAdmin")
    public String verAddMedicamento() {
        return "superAdmin/añadirMedicamento";
    }
    @GetMapping("/verAgregarDoctorSuperAdmin")
    public String verAgregarDoctor() {
        return "superAdmin/AgregarDoctor";
    }
    @GetMapping("/verAgregarAdminSedeSuperAdmin")
    public String verAgregarAdminSede() {
        return "superAdmin/AgregarAdminSede";
    }






}
