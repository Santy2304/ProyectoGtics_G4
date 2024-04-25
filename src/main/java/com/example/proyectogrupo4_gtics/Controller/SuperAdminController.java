package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Doctor;
import com.example.proyectogrupo4_gtics.Entity.Lote;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Controller
public class SuperAdminController {
    final MedicineRepository medicineRepository;
    final PatientRepository patientRepository;
    final DoctorRepository doctorRepository;

    final LoteRepository loteRepository;
    final AdministratorRepository administratorRepository;

    public SuperAdminController(MedicineRepository medicineRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, LoteRepository loteRepository, AdministratorRepository administratorRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.loteRepository = loteRepository;
        this.administratorRepository = administratorRepository;
    }

    @GetMapping("/listaMedicamentosSuperAdmin")
    public String listarMedicamentos(Model model) {
        model.addAttribute("listaMedicamentos", medicineRepository.obtenerDatosMedicamentos());
        return "superAdmin/listaMedicamentos";
    }



    //Agregar Nuevo Medicamento
    @GetMapping("/verAñadirMedicamentoSuperAdmin")
    public String verAddMedicamento() {
        return "superAdmin/anadirMedicamento";
    }


    @PostMapping("/crearMedicamento")
    public String crearMedicamento(@RequestParam("nameMedicine") String nameMedicine,
                                   @RequestParam("category") String category,
                                   @RequestParam("description") String description,
                                   @RequestParam("priceMedicine") double priceMedicine,
                                   Model model) {
        Medicine medicine = new Medicine();
        medicine.setName(nameMedicine);
        medicine.setCategory(category);
        medicine.setDescription(description);
        medicine.setPrice(priceMedicine);
        medicine.setTimesSaled(0);
        medicineRepository.save(medicine);


        model.addAttribute("medicine", medicine);

        return "superAdmin/anadirLotesNuevoMedicamento";
    }



    @PostMapping("/crearLotesNuevoMedicamento")
    public String crearMedicamento(
                                   @RequestParam("expireDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date expireDate,
                                   @RequestParam(value = "stockPando1", required = false, defaultValue = "0") int stockPando1,
                                   @RequestParam(value = "stockPando2",required = false, defaultValue = "0") int stockPando2,
                                   @RequestParam(value = "stockPando3",required = false, defaultValue = "0") int stockPando3,
                                   @RequestParam(value = "stockPando4",required = false, defaultValue = "0") int stockPando4,
                                   @RequestParam("medicineId") int medicineId) {

        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);


                if (stockPando1 != 0) {
                    Lote lote1 = new Lote();
                    lote1.setMedicine(medicine);
                    lote1.setExpireDate(expireDate);
                    lote1.setSite("Pando 1");
                    lote1.setStock(stockPando1);
                    lote1.setExpire(false);
                    loteRepository.save(lote1);
                }

                if (stockPando2 != 0) {
                    Lote lote2 = new Lote();
                    lote2.setMedicine(medicine);
                    lote2.setExpireDate(expireDate);
                    lote2.setSite("Pando 2");
                    lote2.setStock(stockPando2);
                    lote2.setExpire(false);
                    loteRepository.save(lote2);
                }

                if (stockPando3 != 0) {
                    Lote lote3 = new Lote();
                    lote3.setMedicine(medicine);
                    lote3.setExpireDate(expireDate);
                    lote3.setSite("Pando 3");
                    lote3.setStock(stockPando3);
                    lote3.setExpire(false);
                    loteRepository.save(lote3);
                }

                if (stockPando4 != 0) {
                    Lote lote4 = new Lote();
                    lote4.setMedicine(medicine);
                    lote4.setExpireDate(expireDate);
                    lote4.setSite("Pando 4");
                    lote4.setStock(stockPando4);
                    lote4.setExpire(false);
                    loteRepository.save(lote4);
                }

        return "redirect:/listaMedicamentosSuperAdmin";
    }


    //Editar medicamento

    @GetMapping("/editarMedicamento")
    public String editarMedicamento(@RequestParam("idMedicine") int idMedicine, Model model) {

        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            return "superAdmin/editarMedicamento";
        } else {
            return "redirect:/listaMedicamentosSuperAdmin";
        }
    }


    @PostMapping("/guardarCambiosMedicamento")
    public String guardarCambiosMedicamento(Medicine medicine) {
        medicineRepository.actualizarMedicine(medicine.getName(),medicine.getCategory(),medicine.getPrice(),medicine.getDescription(),medicine.getIdMedicine());
        return "redirect:/listaMedicamentosSuperAdmin";
    }




    //Detalle

    @GetMapping("/verDetallesProductoSuperAdmin")
    public String verDetallesProducto(@RequestParam("idMedicine") int idMedicine, Model model) {

        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            return "superAdmin/detallesProducto";
        } else {
            return "redirect:/listaMedicamentosSuperAdmin";
        }

    }





    @GetMapping("/verDetalleMedicamentosSuperAdmin")
    public String verDetalleMedicamentos() {
        return "superAdmin/DetalleMedicamentos";
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
    public String verListados(Model model) {

        List<Doctor> listaDoctores = doctorRepository.findAll();
        model.addAttribute("listaDoctores", listaDoctores);
        List<Administrator> listaAdminSede = administratorRepository.findAll();
        model.addAttribute("listaAdminSede", listaAdminSede);

        return "superAdmin/listados";
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


    @PostMapping("/formNewMedicamento")
    public String addNewMedicine(Medicine medicine){

        return "redirect:listarMedicamentos";
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
