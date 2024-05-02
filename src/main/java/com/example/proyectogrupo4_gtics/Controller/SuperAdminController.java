package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.List;

@Controller
public class SuperAdminController {
    final MedicineRepository medicineRepository;
    final PatientRepository patientRepository;
    final DoctorRepository doctorRepository;

    final LoteRepository loteRepository;
    final AdministratorRepository administratorRepository;
    final SiteRepository siteRepository;

    public SuperAdminController(MedicineRepository medicineRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, LoteRepository loteRepository, AdministratorRepository administratorRepository, SiteRepository siteRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.loteRepository = loteRepository;
        this.administratorRepository = administratorRepository;
        this.siteRepository = siteRepository;
    }

    @GetMapping("/listaMedicamentosSuperAdmin")
    public String listarMedicamentos(Model model) {
        System.out.println("Estoy en superadmin");
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
    public String crearLoresNuevoMedicamento(
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
                    lote1.setVisible(true);
                    loteRepository.save(lote1);
                }

                if (stockPando2 != 0) {
                    Lote lote2 = new Lote();
                    lote2.setMedicine(medicine);
                    lote2.setExpireDate(expireDate);
                    lote2.setSite("Pando 2");
                    lote2.setStock(stockPando2);
                    lote2.setExpire(false);
                    lote2.setVisible(true);
                    loteRepository.save(lote2);
                }

                if (stockPando3 != 0) {
                    Lote lote3 = new Lote();
                    lote3.setMedicine(medicine);
                    lote3.setExpireDate(expireDate);
                    lote3.setSite("Pando 3");
                    lote3.setStock(stockPando3);
                    lote3.setExpire(false);
                    lote3.setVisible(true);
                    loteRepository.save(lote3);
                }

                if (stockPando4 != 0) {
                    Lote lote4 = new Lote();
                    lote4.setMedicine(medicine);
                    lote4.setExpireDate(expireDate);
                    lote4.setSite("Pando 4");
                    lote4.setStock(stockPando4);
                    lote4.setExpire(false);
                    lote4.setVisible(true);
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

            List<Lote> listaLotesporMedicamento =  loteRepository.findByMedicineIdMedicine(idMedicine);

            int contadorPando1 = 0;
            int contadorPando2 = 0;
            int contadorPando3 = 0;
            int contadorPando4 = 0;
            for (Lote loteEva : listaLotesporMedicamento) {

                String sede = loteEva.getSite();

                if (sede.equals("Pando 1") && loteEva.isVisible() ){
                    contadorPando1 = contadorPando1 +1;
                }
                if (sede.equals("Pando 2") && loteEva.isVisible() ){
                    contadorPando2 = contadorPando2 +1;
                }
                if (sede.equals("Pando 3") && loteEva.isVisible() ){
                    contadorPando3 = contadorPando3 +1;
                }
                if (sede.equals("Pando 4") && loteEva.isVisible() ){
                    contadorPando4 = contadorPando4 +1;
                }

            }
            model.addAttribute("contadorPando1",contadorPando1);
            model.addAttribute("contadorPando2",contadorPando2);
            model.addAttribute("contadorPando3",contadorPando3);
            model.addAttribute("contadorPando4",contadorPando4);


            return "superAdmin/editarMedicamento";
        } else {
            return "redirect:/listaMedicamentosSuperAdmin";
        }
    }


    @PostMapping("/guardarCambiosMedicamento")
    public String guardarCambiosMedicamento(Medicine medicine,
                                            @RequestParam("disponibilidadPando1") String disponible1,
                                            @RequestParam("disponibilidadPando2") String disponible2,
                                            @RequestParam("disponibilidadPando3") String disponible3,
                                            @RequestParam("disponibilidadPando4") String disponible4) {
        medicineRepository.actualizarMedicine(medicine.getName(),medicine.getCategory(),medicine.getPrice(),medicine.getDescription(),medicine.getIdMedicine());

        Calendar calendar = Calendar.getInstance();

        // Obtener la fecha actual
        Date fechaActual = new Date();
        calendar.setTime(fechaActual);

        // Agregar tres años a la fecha actual
        calendar.add(Calendar.YEAR, 3);

        // Obtener la nueva fecha después de agregar tres años
        Date nuevaFecha = calendar.getTime();

        boolean visibilidad1;
        boolean visibilidad2;
        boolean visibilidad3;
        boolean visibilidad4;

        //Para sede 1
        if (disponible1.equals("si")){
            visibilidad1 = true;

            List<String> listaLotesPando1 = loteRepository.obtenerLoteporSede(medicine.getIdMedicine(), "Pando 1");

            if (listaLotesPando1.isEmpty()){
                Lote lote1 = new Lote();
                lote1.setExpireDate(nuevaFecha);
                lote1.setStock(50);
                lote1.setSite("Pando 1");
                lote1.setMedicine(medicine);
                lote1.setExpire(false);
                lote1.setVisible(true);
                loteRepository.save(lote1);
            }else{
                loteRepository.actualizarVisibilidadSede(visibilidad1, medicine.getIdMedicine(),"Pando 1");
            }

        }else{
            visibilidad1 = false;
            loteRepository.actualizarVisibilidadSede(visibilidad1, medicine.getIdMedicine(),"Pando 1");

        }


        //Para sede 2
        if (disponible2.equals("si")){
            visibilidad2 = true;

            List<String> listaLotesPando2 = loteRepository.obtenerLoteporSede(medicine.getIdMedicine(), "Pando 2");

            if (listaLotesPando2.isEmpty()){
                Lote lote2 = new Lote();
                lote2.setExpireDate(nuevaFecha);
                lote2.setStock(50);
                lote2.setSite("Pando 2");
                lote2.setMedicine(medicine);
                lote2.setExpire(false);
                lote2.setVisible(true);
                loteRepository.save(lote2);

            }else{
                loteRepository.actualizarVisibilidadSede(visibilidad2, medicine.getIdMedicine(),"Pando 2");

            }
        }else{
            visibilidad2 = false;
            loteRepository.actualizarVisibilidadSede(visibilidad2, medicine.getIdMedicine(),"Pando 2");

        }

        //Para sede 3
        if (disponible3.equals("si")){
            visibilidad3 = true;
            List<String> listaLotesPando3 = loteRepository.obtenerLoteporSede(medicine.getIdMedicine(), "Pando 3");

            if (listaLotesPando3.isEmpty()){
                Lote lote3 = new Lote();
                lote3.setExpireDate(nuevaFecha);
                lote3.setStock(50);
                lote3.setSite("Pando 3");
                lote3.setMedicine(medicine);
                lote3.setExpire(false);
                lote3.setVisible(true);
                loteRepository.save(lote3);
            }else{
                loteRepository.actualizarVisibilidadSede(visibilidad3, medicine.getIdMedicine(),"Pando 3");

            }

        }else{
            visibilidad3 = false;
            loteRepository.actualizarVisibilidadSede(visibilidad3, medicine.getIdMedicine(),"Pando 3");

        }

        //Para sede4
        if (disponible4.equals("si")){
            visibilidad4 = true;
            List<String> listaLotesPando4 = loteRepository.obtenerLoteporSede(medicine.getIdMedicine(), "Pando 4");

            if (listaLotesPando4.isEmpty()){
                Lote lote4 = new Lote();
                lote4.setExpireDate(nuevaFecha);
                lote4.setStock(50);
                lote4.setSite("Pando 4");
                lote4.setMedicine(medicine);
                lote4.setExpire(false);
                lote4.setVisible(true);
                loteRepository.save(lote4);
            }else{
                loteRepository.actualizarVisibilidadSede(visibilidad4, medicine.getIdMedicine(),"Pando 4");

            }
        }else{
            visibilidad4 = false;
            loteRepository.actualizarVisibilidadSede(visibilidad4, medicine.getIdMedicine(),"Pando 4");

        }

        return "redirect:/listaMedicamentosSuperAdmin";
    }





    //Detalle

    @GetMapping("/verDetallesProductoSuperAdmin")
    public String verDetallesProducto(@RequestParam("idMedicine") int idMedicine, Model model) {

        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            List<LotesValidosporMedicamento> listaLotesporMedicamento =  loteRepository.obtenerLotesValidosPorMedicamento(idMedicine);

            model.addAttribute("listaLotes",listaLotesporMedicamento);
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
    @GetMapping("/editarDoctor")
    public String verEditarDoctor(@RequestParam("idDoctor") int idDoctor , Model model) {
        Optional<Doctor> doctor =  doctorRepository.findById(idDoctor);
        if(doctor.isPresent()){
            model.addAttribute("doctor", doctor.get());
            return "superAdmin/EditarDoctor";
        }else{
            return "redirect:/verListadosSuperAdmin";
        }
    }


    @GetMapping("/verEditarAdministradorSuperAdmin")
    public String verEditarAdministrador() {
        return "superAdmin/EditarAdministrador";
    }

    @PostMapping("/editarDoctor")
    public String editarDoctor(Doctor doctor){
        //    void updateDatosPorId(String name , String lasName , int dni , String email , int idDoctor );
        doctorRepository.updateDatosPorId(doctor.getName(), doctor.getLastName(),  doctor.getDni() , doctor.getEmail(), doctor.getIdDoctor());
        return "redirect:/verListadosSuperAdmin";
    }

    @PostMapping("/formNewMedicamento")
    public String addNewMedicine(Medicine medicine){

        return "redirect:listarMedicamentos";
    }
    @GetMapping("/verAgregarDoctorSuperAdmin")
    public String verAgregarDoctor(Model model) {
        List<Site> listaSedes = siteRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superAdmin/AgregarDoctor";
    }

    @PostMapping("/agregarDoctor")
    public String agregarDoctor(Doctor doctor){
        doctor.setCreationDate(LocalDate.now());
        doctorRepository.save(doctor);
        return "redirect:/verListadosSuperAdmin";
    }

    @GetMapping("/verAgregarAdminSedeSuperAdmin")
    public String verAgregarAdminSede(Model model) {

        List<Site> listaSedes = siteRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superAdmin/AgregarAdminSede";
    }

    @PostMapping("/agregarAdminSede")
    public String agregarAdminSede(Administrator administrator) {
        administrator.setPassword("passworDefault");
        administrator.setCreationDate(LocalDate.now());
        administratorRepository.save(administrator);
        return "redirect:/verListadosSuperAdmin";

    }


}
