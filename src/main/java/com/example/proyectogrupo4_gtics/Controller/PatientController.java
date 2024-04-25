package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.example.proyectogrupo4_gtics.Entity.Site;
import com.example.proyectogrupo4_gtics.Repository.MedicineRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.SiteRepository;
import com.example.proyectogrupo4_gtics.Repository.medicamentosPorSedeDTO;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@SessionAttributes({"usuario","sede"})
public class PatientController {
    final SiteRepository siteRepository;
    final PatientRepository patientRepository;
    final MedicineRepository medicineRepository;

    public PatientController (SiteRepository siteRepository ,PatientRepository patientRepository , MedicineRepository medicineRepository) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.medicineRepository = medicineRepository;
    }

    @GetMapping("/elegirSedePrimeraVez")
    public String llevarVistaPrincipal(@RequestParam("idSede") String idSede,@ModelAttribute("usuario") Patient patient, Model model){
        model.addAttribute("usuario", patient);
        model.addAttribute("sede", siteRepository.findById(Integer.parseInt(idSede)));
        List<medicamentosPorSedeDTO> listMedicineBySede = medicineRepository.getMedicineBySite(Integer.parseInt(idSede));
        model.addAttribute("listaMedicinas" , listMedicineBySede) ;
        return "pacient/principal";
    }
    @GetMapping("/")
    public String listarMedicamentos(){
        return"";
    }
    //El chat no es responsive
    @GetMapping("/verChatPaciente")
    public String verChatPaciente(){
        return "pacient/chat";
    }
    @GetMapping("/verDatosPagoPaciente")
    public String verDatosPago(){
        return "pacient/datos_pago";
    }
    //No funciona bien
    @GetMapping("/verDetallePaciente")
    public String verDetalle(){
        return "pacient/detalle";
    }
    @GetMapping("/verGenerarOrdenCompraPaciente")
    public String verGenerarOrdenCompra(){
        return "pacient/generar_orden_compra";
    }
    @GetMapping("/verHistorialPaciente")
    public String verHistorial(){
        return "pacient/historial";
    }
    @GetMapping("/verNumeroOrdenPaciente")
    public String verNumeroOrdenPaciente(){
        return "pacient/numero_de_orden";
    }

    @GetMapping("/verPerfilPaciente")
    public String verPerfilPaciente( Model model){
        return "pacient/perfil";
    }
    @GetMapping("/verPrincipalPaciente")
    public String verPrincipalPaciente(Model model){
        return "pacient/principal";
    }
    //No funciona bien
    @GetMapping("/verProductListPaciente")
    public String verProductListPaciente(){
        return "pacient/productlist";
    }
    @GetMapping("/verSeleccionarSedePaciente")
    public String verSeleccionarSedePaciente(Model model) {
        List<Site> listaSedes=  siteRepository.findAll();
        model.addAttribute("listaSede", listaSedes);
        return "pacient/seleccionarSede";
    }
    //Falta corregir
    @GetMapping("/verSingleProductPaciente")
    public String verSingleProductPaciente(){return "pacient/single_product";}
    @GetMapping("/verTrackingPaciente")
    public String verTrackingPaciente(){
        return "pacient/tracking";
    }
    @PostMapping("/editarPerfilPaciente")
    public String editarDatosPaciente(Patient patient){
        //Actualizar datos cambiados
        System.out.println(patient.getIdPatient());
        System.out.println(patient.getLocation());
        System.out.println(patient.getInsurance());
        patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), patient.getIdPatient());
        return "redirect:verPerfilPaciente";
    }

}
