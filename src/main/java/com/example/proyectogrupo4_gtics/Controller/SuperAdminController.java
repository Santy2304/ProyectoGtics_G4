package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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

    final PharmacistRepository pharmacistRepository;
    private final ReplacementOrderRepository replacementOrderRepository;

    public SuperAdminController(MedicineRepository medicineRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, LoteRepository loteRepository, AdministratorRepository administratorRepository, SiteRepository siteRepository, PharmacistRepository pharmacistRepository,
                                ReplacementOrderRepository replacementOrderRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.loteRepository = loteRepository;
        this.administratorRepository = administratorRepository;
        this.siteRepository = siteRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.replacementOrderRepository = replacementOrderRepository;
    }


    //Medicamentos///////////////////////////

    @GetMapping("/listaMedicamentosSuperAdmin")
    public String listarMedicamentos(Model model) {
        model.addAttribute("listaMedicamentos", medicineRepository.obtenerDatosMedicamentos());
        return "superAdmin/listaMedicamentos";
    }
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
    @GetMapping("/verDetallesProductoSuperAdmin")
    public String verDetallesProducto(@RequestParam("idMedicine") int idMedicine, Model model) {

        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            List<LotesValidosporMedicamentoDTO> listaLotesporMedicamento =  loteRepository.obtenerLotesValidosPorMedicamento(idMedicine);

            model.addAttribute("listaLotes",listaLotesporMedicamento);
            return "superAdmin/detallesProducto";
        } else {
            return "redirect:/listaMedicamentosSuperAdmin";
        }

    }
    //////////////////////////////////////////////////////////7

    //LISTADOS DE USUARIOS
    @GetMapping("/verListadosSuperAdmin")
    public String verListados(Model model) {

        List<Doctor> listaDoctores = doctorRepository.listarDoctoresValidos();
        model.addAttribute("listaDoctores", listaDoctores);

        List<Administrator> listaAdminSede = administratorRepository.listarAdminValidos();
        model.addAttribute("listaAdminSede", listaAdminSede);

        List<Pharmacist> listaFarmacistas = pharmacistRepository.listarFarmacistasValidos();
        model.addAttribute("listaFarmacistas",listaFarmacistas);

        List<Patient> listaPacientes = patientRepository.listarPacientesValidos();
        model.addAttribute("listaPacientes",listaPacientes);

        return "superAdmin/listados";
    }

    ////////////////////////////////////////

    //Doctores/////////////////////7
    @PostMapping("/guardarCambiosDoctor")
    public String editarDoctor(Doctor doctor){
        //    void updateDatosPorId(String name , String lasName , int dni , String email , int idDoctor );
        doctorRepository.updateDatosPorId(doctor.getName(), doctor.getLastName(),  doctor.getDni() , doctor.getEmail(),doctor.getHeadquarter(),doctor.getState() ,doctor.getIdDoctor());
        return "redirect:/verListadosSuperAdmin";
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

    @GetMapping("/verAgregarDoctorSuperAdmin")
    public String verAgregarDoctor(Model model) {
        List<Site> listaSedes = siteRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superAdmin/AgregarDoctor";
    }

    @PostMapping("/agregarDoctor")
    public String agregarDoctor(Doctor doctor){
        doctor.setCreationDate(LocalDate.now());
        doctor.setState("activo");
        doctorRepository.save(doctor);
        return "redirect:/verListadosSuperAdmin";
    }

    @GetMapping("/EliminarDoctor")
    public String eliminarDoctor(@RequestParam("idDoctor") int idDoctor ) {
        doctorRepository.eliminarDoctorPorId(idDoctor);
        return "redirect:/verListadosSuperAdmin";
    }


////////////////////////////////

    //AdministradoresSede///////////////////////////7

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
        administrator.setState("activo");
        administratorRepository.save(administrator);
        return "redirect:/verListadosSuperAdmin";

    }

    @GetMapping("/editarAdminSede")
    public String verEditarAdminSede(@RequestParam("idAdminSede") int idAdminSede , Model model) {

        Optional<Administrator> administrator = administratorRepository.findById(idAdminSede);
        if(administrator.isPresent()){
            model.addAttribute("adminSede", administrator.get());
            return "superAdmin/EditarAdministrador";
        }else{
            return "redirect:/verListadosSuperAdmin";
        }
    }


    @PostMapping("/guardarCambiosAdminSede")
    public String editarAdminSede(Administrator administrator){
        //    void updateDatosPorId(String name , String lasName , int dni , String email , int idDoctor );
        administratorRepository.updateDatosPorId(administrator.getName(), administrator.getLastName(),administrator.getDni(), administrator.getEmail(),administrator.getSite(),administrator.getState(),administrator.getIdAdministrador());
        return "redirect:/verListadosSuperAdmin";
    }

    @GetMapping("/eliminarAdminSede")
    public String eliminarAdminSede(@RequestParam("idAdminSede") int idAdminSede) {
        administratorRepository.eliminarAdminPorId(idAdminSede);
        return "redirect:/verListadosSuperAdmin";
    }

    ////////////////////////////////


    //Farmacista///////////////////////////////

    @GetMapping("/editarFarmacista")
    public String verEditarFarmacista(@RequestParam("idFarmacista") int idFarmacista , Model model) {

        Optional<Pharmacist> pharmacist = pharmacistRepository.findById(idFarmacista);
        if(pharmacist.isPresent()){
            model.addAttribute("farmacista", pharmacist.get());
            return "superAdmin/EditarFarmacista";
        }else{
            return "redirect:/verListadosSuperAdmin";
        }
    }

    @PostMapping("/guardarCambiosFarmacista")
    public String editarFarmacista(Pharmacist pharmacist){
        pharmacistRepository.updateDatosPorId(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getDni(), pharmacist.getEmail(), pharmacist.getSite(), pharmacist.getState(), pharmacist.getDistrit(), pharmacist.getIdFarmacista());
        return "redirect:/verListadosSuperAdmin";
    }


    @GetMapping("/eliminarFarmacista")
    public String eliminarFarmacista(@RequestParam("idFarmacista") int idFarmacista) {
        pharmacistRepository.eliminarFarmacistaPorId(idFarmacista);
        return "redirect:/verListadosSuperAdmin";
    }


    //////////////////////////////////

    ////Paciente////////////////////

    @GetMapping("/eliminarPaciente")
    public String eliminarPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.eliminarPacientePorId(idPaciente);
        return "redirect:/verListadosSuperAdmin";
    }

    @GetMapping("/banearPaciente")
    public String banearPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.banearPacientePorId(idPaciente);
        return "redirect:/verListadosSuperAdmin";
    }


    ///////////////////////////////////



    //////////////////LISTADOS SEDES /////////////////////
    @GetMapping("/verSedeSuperAdminPando1")
    public String verSedePando1(Model model) {
        List<Pharmacist> listaSolicitudesFarmacistaPando1 = pharmacistRepository.listarSolicitudesFarmacistaPando1();
        model.addAttribute("listaSolicitudesFarmacistasPando1",listaSolicitudesFarmacistaPando1);
        List<ReplacementOrder> listarSolicitudesReposicionPando1 = replacementOrderRepository.obtenerSolicitudesRepoPando1();
        model.addAttribute("listaSolicitudesReposicionPando1",listarSolicitudesReposicionPando1);
        return "superAdmin/SedePando1";
    }

    @GetMapping("/verSedeSuperAdminPando2")
    public String verSedePando2(Model model) {
        List<Pharmacist> listarSolicitudesFarmacistaPando2 = pharmacistRepository.listarSolicitudesFarmacistaPando2();
        model.addAttribute("listaSolicitudesFarmacistasPando2",listarSolicitudesFarmacistaPando2);
        return "superAdmin/SedePando2";
    }

    @GetMapping("/verSedeSuperAdminPando3")
    public String verSedePando3(Model model) {
        List<Pharmacist> listarSolicitudesFarmacistaPando3 = pharmacistRepository.listarSolicitudesFarmacistaPando3();
        model.addAttribute("listaSolicitudesFarmacistasPando3",listarSolicitudesFarmacistaPando3);
        return "superAdmin/SedePando3";
    }

    @GetMapping("/verSedeSuperAdminPando4")
    public String verSedePando4(Model model) {
        List<Pharmacist> listarSolicitudesFarmacistaPando4 = pharmacistRepository.listarSolicitudesFarmacistaPando4();
        model.addAttribute("listaSolicitudesFarmacistasPando4",listarSolicitudesFarmacistaPando4);
        return "superAdmin/SedePando4";
    }


    ///////////////////////////////////////7
    @GetMapping("/verDetalleRepoSuperAdmin")
    public String verDetalleMedicamentos(@RequestParam("idRepo") int idRepo,Model model) {

        List<MedicamentosPorReposicionDTO> medicamentosPorReposicion =   replacementOrderRepository.obtenerMedicamentosPorReposicion(idRepo);
        model.addAttribute("listaMedicamentosPorRepo",medicamentosPorReposicion);
        return "superAdmin/DetalleRepo";
    }


    //Solo para poder saltar entre vistas auxiliar de momento

    @GetMapping("/verPerfilSuperAdmin")
    public String verPerfil() {
        return "superAdmin/perfil";
    }
    @GetMapping("/verNotificationsSuperAdmin")
    public String verNotifications() {
        return "superAdmin/notifications";
    }

}
