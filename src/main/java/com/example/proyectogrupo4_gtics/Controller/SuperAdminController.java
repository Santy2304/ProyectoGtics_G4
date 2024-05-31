package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/superAdmin")
public class  SuperAdminController {
    final MedicineRepository medicineRepository;
    final PatientRepository patientRepository;
    final DoctorRepository doctorRepository;

    final UserRepository userRepository;

    final LoteRepository loteRepository;
    final AdministratorRepository administratorRepository;
    final SiteRepository siteRepository;

    final SuperAdminRepository superAdminRepository;
    final PharmacistRepository pharmacistRepository;
    private final ReplacementOrderRepository replacementOrderRepository;

    public SuperAdminController(MedicineRepository medicineRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, LoteRepository loteRepository, AdministratorRepository administratorRepository, SiteRepository siteRepository, PharmacistRepository pharmacistRepository,
                                ReplacementOrderRepository replacementOrderRepository, SuperAdminRepository superAdminRepository, UserRepository userRepository) {
        this.medicineRepository = medicineRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.loteRepository = loteRepository;
        this.administratorRepository = administratorRepository;
        this.siteRepository = siteRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.replacementOrderRepository = replacementOrderRepository;
        this.superAdminRepository = superAdminRepository;
        this.userRepository = userRepository;
    }


    //Medicamentos///////////////////////////

    @GetMapping("/listaMedicamentos")
    public String listarMedicamentos(Model model) {
        model.addAttribute("listaMedicamentos", medicineRepository.obtenerDatosMedicamentos());
        return "superAdmin/listaMedicamentos";
    }
    @GetMapping("/verAñadirMedicamento")
    public String verAddMedicamento(@ModelAttribute("medicine") Medicine medicine) {
        return "superAdmin/anadirMedicamento";
    }


    @PostMapping("/crearMedicamento")
    public String crearMedicamento(/*@RequestParam("nameMedicine") String nameMedicine,
                                   @RequestParam("category") String category,
                                   @RequestParam("description") String description,
                                   @RequestParam("priceMedicine") BigDecimal priceMedicine,*/
                                   @ModelAttribute("medicine") @Valid Medicine medicine,
                                   BindingResult bindingResult, Model model, @RequestParam("medicineFile")MultipartFile imagen) {
        /*Medicine medicine = new Medicine();
        medicine.setName(nameMedicine);
        medicine.setCategory(category);
        medicine.setDescription(description);
        medicine.setPrice(priceMedicine);*/
        if (bindingResult.hasErrors()) {
            return "superAdmin/anadirMedicamento";
        } else {
            medicine.setTimesSaled(0);
            if(!imagen.isEmpty()){
                //ruta relativa para la imagen
                Path directorioImagenMedicine= Paths.get("src//main//resources//static//assets_superAdmin//ImagenesMedicina");
                //ruta relativa para la imagen
                String rutaAbsoluta =  directorioImagenMedicine.toFile().getAbsolutePath();

                //imagen a flujo bytes y poder guardarlo en la base de datos para poder extraerlo después
                try {
                    byte[] bytesImgMedicine = imagen.getBytes();
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta,bytesImgMedicine);
                    medicine.setPhoto(imagen.getOriginalFilename());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            medicineRepository.save(medicine);
            model.addAttribute("medicine", medicine);

            return "superAdmin/anadirLotesNuevoMedicamento";
        }
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
                    lote1.setInitialQuantity(stockPando1);
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
                    lote2.setInitialQuantity(stockPando2);
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
                    lote3.setInitialQuantity(stockPando3);
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
                    lote4.setInitialQuantity(stockPando4);
                    loteRepository.save(lote4);
                }

        return "redirect:listaMedicamentos";
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
            return "redirect:listaMedicamentos";
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
                lote1.setInitialQuantity(50);
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
                lote2.setInitialQuantity(50);
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
                lote3.setInitialQuantity(50);
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
                lote4.setInitialQuantity(50);
                loteRepository.save(lote4);
            }else{
                loteRepository.actualizarVisibilidadSede(visibilidad4, medicine.getIdMedicine(),"Pando 4");

            }
        }else{
            visibilidad4 = false;
            loteRepository.actualizarVisibilidadSede(visibilidad4, medicine.getIdMedicine(),"Pando 4");

        }

        return "redirect:listaMedicamentos";
    }
    @GetMapping("/verDetallesProducto")
    public String verDetallesProducto(@RequestParam("idMedicine") int idMedicine, Model model) {

        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            List<LotesValidosporMedicamentoDTO> listaLotesporMedicamento =  loteRepository.obtenerLotesValidosPorMedicamento(idMedicine);

            model.addAttribute("listaLotes",listaLotesporMedicamento);
            return "superAdmin/detallesProducto";
        } else {
            return "redirect:listaMedicamentos";
        }

    }
    //////////////////////////////////////////////////////////7

    //LISTADOS DE USUARIOS
    @GetMapping("/verListados")
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
    public String editarDoctor(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult, RedirectAttributes attributes){
        //    void updateDatosPorId(String name , String lasName , int dni , String email , int idDoctor );
        if (bindingResult.hasErrors()) {
            return "superAdmin/EditarDoctor";
        } else {
            attributes.addFlashAttribute("msg", "Doctor actualizado correctamente");
            doctorRepository.updateDatosPorId(doctor.getName(), doctor.getLastName(), doctor.getDni(), doctor.getEmail(), doctor.getHeadquarter(), doctor.getState(), doctor.getIdDoctor());
            return "redirect:verListados";
        }
    }

    @GetMapping("/editarDoctor")
    public String verEditarDoctor(@ModelAttribute("doctor") Doctor doctor, @RequestParam("idDoctor") int idDoctor, Model model) {
        Optional<Doctor> optDoctor =  doctorRepository.findById(idDoctor);
        if(optDoctor.isPresent()){
            doctor = optDoctor.get();
            model.addAttribute("doctor", doctor);
            return "superAdmin/EditarDoctor";
        }else{
            return "redirect:verListados";
        }
    }

    @GetMapping("/verAgregarDoctor")
    public String verAgregarDoctor(@ModelAttribute("doctor") Doctor doctor, Model model) {
        List<Site> listaSedes = siteRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superAdmin/AgregarDoctor";
    }

    public boolean verificarUnicidadDni(String dni, String rol) {
        switch (rol) {
            case "Doctor":
                List<Doctor> listaDoctores = doctorRepository.findAll();
                for (Doctor doctor : listaDoctores) {
                    if (doctor.getDni().equals(dni)) {
                        return true;
                    }
                }
            case "Administrator":
                List<Administrator> listaAdminSede = administratorRepository.findAll();
                for (Administrator administrator : listaAdminSede) {
                    if (administrator.getDni().equals(dni)) {
                        return true;
                    }
                }
            default: return false;
        }
    }

    @PostMapping("/agregarDoctor")
    public String agregarDoctor(@ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResult, RedirectAttributes attributes, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("listaSedes", siteRepository.findAll());
            return "superAdmin/AgregarDoctor";
        } else {
            attributes.addFlashAttribute("msg", "Doctor agregado correctamente");
            doctor.setCreationDate(LocalDate.now());
            doctor.setState("activo");
            if (verificarUnicidadDni(doctor.getDni(), "Doctor")) { //En caso se repita el dni
                model.addAttribute("listaSedes", siteRepository.findAll());
                model.addAttribute("error", "El DNI ingresado ya existe");
                return "superAdmin/AgregarDoctor";
            } else { //En caso sea único
                doctorRepository.save(doctor);
                return "redirect:verListados";
            }
        }
    }

    @GetMapping("/EliminarDoctor")
    public String eliminarDoctor(@RequestParam("idDoctor") int idDoctor ) {
        doctorRepository.eliminarDoctorPorId(idDoctor);
        return "redirect:verListados";
    }


////////////////////////////////

    //AdministradoresSede///////////////////////////7

    @GetMapping("/verAgregarAdminSede")
    public String verAgregarAdminSede(@ModelAttribute("adminSede") Administrator administrator, Model model) {
        List<Site> listaSedes = siteRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superAdmin/AgregarAdminSede";
    }

    @PostMapping("/agregarAdminSede")
    public String agregarAdminSede(@ModelAttribute("adminSede") @Valid Administrator administrator, BindingResult bindingResult, RedirectAttributes attributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("listaSedes", siteRepository.findAll());
            return "superAdmin/AgregarAdminSede";
        } else {
            administrator.setPassword("passworDefault");
            administrator.setCreationDate(LocalDate.now());
            administrator.setState("activo");
            if (verificarUnicidadDni(administrator.getDni(), "Administrator")) {
                model.addAttribute("listaSedes", siteRepository.findAll());
                model.addAttribute("error", "El DNI del administrador ingresado ya existe");
                return "superAdmin/AgregarAdminSede";
            } else {
                attributes.addFlashAttribute("msg", "Administrador agregado correctamente");
                administratorRepository.save(administrator);
                return "redirect:verListados";
            }
        }
    }

    @GetMapping("/editarAdminSede")
    public String verEditarAdminSede(@ModelAttribute("adminSede") Administrator administrator, @RequestParam("idAdminSede") int idAdminSede , Model model) {

        Optional<Administrator> optionalAdministrator = administratorRepository.findById(idAdminSede);
        if(optionalAdministrator.isPresent()){
            administrator = optionalAdministrator.get();
            model.addAttribute("adminSede", administrator);
            return "superAdmin/EditarAdministrador";
        }else{
            return "redirect:verListados";
        }
    }


    @PostMapping("/guardarCambiosAdminSede")
    public String editarAdminSede(@ModelAttribute("adminSede") @Valid Administrator administrator, BindingResult bindingResult, RedirectAttributes attributes){
        //    void updateDatosPorId(String name , String lasName , int dni , String email , int idDoctor );
        if (bindingResult.hasErrors()) {
            return "superAdmin/EditarAdministrador";
        }else {
            attributes.addFlashAttribute("msg", "Administrador actualizado correctamente");
            administratorRepository.updateDatosPorId(administrator.getName(), administrator.getLastName(), administrator.getDni(), administrator.getEmail(), administrator.getSite(), administrator.getState(), administrator.getIdAdministrador());
            return "redirect:verListados";
        }
    }

    @GetMapping("/eliminarAdminSede")
    public String eliminarAdminSede(@RequestParam("idAdminSede") int idAdminSede) {
        administratorRepository.eliminarAdminPorId(idAdminSede);
        return "redirect:verListados";
    }

    ////////////////////////////////


    //Farmacista///////////////////////////////

    @GetMapping("/editarFarmacista")
    public String verEditarFarmacista(@ModelAttribute("farmacista") Pharmacist pharmacist, @RequestParam("idFarmacista") int idFarmacista , Model model) {

        Optional<Pharmacist> optionalPharmacist = pharmacistRepository.findById(idFarmacista);
        if(optionalPharmacist.isPresent()){
            pharmacist = optionalPharmacist.get();
            model.addAttribute("farmacista", pharmacist);
            return "superAdmin/EditarFarmacista";
        }else{
            return "redirect:verListados";
        }
    }

    @PostMapping("/guardarCambiosFarmacista")
    public String editarFarmacista(@ModelAttribute("farmacista") @Valid Pharmacist pharmacist, BindingResult bindingResult, RedirectAttributes attributes){
        if (bindingResult.hasErrors()) {
            return "superAdmin/EditarFarmacista";
        } else {
            attributes.addFlashAttribute("msg", "Farmacista actualizado correctamente");
            pharmacistRepository.updateDatosPorId(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getEmail(), pharmacist.getSite(), pharmacist.getState(), pharmacist.getDistrit(),pharmacist.getIdFarmacista());
            return "redirect:verListados";
        }
    }


    @GetMapping("/eliminarFarmacista")
    public String eliminarFarmacista(@RequestParam("idFarmacista") int idFarmacista) {
        pharmacistRepository.eliminarFarmacistaPorId(idFarmacista);
        return "redirect:verListados";
    }

    @GetMapping("/rechazarFarmacista")
    public String rechazarFarmacista(@RequestParam("idFarmacista") int idFarmacista) {
        pharmacistRepository.rechazarFarmacistaPorId(idFarmacista);
        return "redirect:verListados";
    }

    @GetMapping("/aceptarFarmacista")
    public String aceptarFarmacista(@RequestParam("idFarmacista") int idFarmacista) {
        pharmacistRepository.aceptarFarmacistaPorId(idFarmacista);

        return "redirect:verSedeSuperAdminPando1";
    }

    //////////////////////////////////

    ////Paciente////////////////////

    @GetMapping("/eliminarPaciente")
    public String eliminarPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.eliminarPacientePorId(idPaciente);
        return "redirect:verListados";
    }

    @GetMapping("/banearPaciente")
    public String banearPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.banearPacientePorId(idPaciente);
        return "redirect:verListados";
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
        List<ReplacementOrder> listarSolicitudesReposicionPando2 = replacementOrderRepository.obtenerSolicitudesRepoPando2();
        model.addAttribute("listaSolicitudesReposicionPando2",listarSolicitudesReposicionPando2);
        return "superAdmin/SedePando2";
    }

    @GetMapping("/verSedeSuperAdminPando3")
    public String verSedePando3(Model model) {
        List<Pharmacist> listarSolicitudesFarmacistaPando3 = pharmacistRepository.listarSolicitudesFarmacistaPando3();
        model.addAttribute("listaSolicitudesFarmacistasPando3",listarSolicitudesFarmacistaPando3);
        List<ReplacementOrder> listarSolicitudesReposicionPando3 = replacementOrderRepository.obtenerSolicitudesRepoPando3();
        model.addAttribute("listaSolicitudesReposicionPando3",listarSolicitudesReposicionPando3);
        return "superAdmin/SedePando3";
    }

    @GetMapping("/verSedeSuperAdminPando4")
    public String verSedePando4(Model model) {
        List<Pharmacist> listarSolicitudesFarmacistaPando4 = pharmacistRepository.listarSolicitudesFarmacistaPando4();
        model.addAttribute("listaSolicitudesFarmacistasPando4",listarSolicitudesFarmacistaPando4);
        List<ReplacementOrder> listarSolicitudesReposicionPando4 = replacementOrderRepository.obtenerSolicitudesRepoPando4();
        model.addAttribute("listaSolicitudesReposicionPando4",listarSolicitudesReposicionPando4);
        return "superAdmin/SedePando4";
    }


    ///////////////////////////////////////7
    @GetMapping("/verDetalleRepo")
    public String verDetalleMedicamentos(@RequestParam("idRepo") int idRepo,Model model) {

        List<MedicamentosPorReposicionDTO> medicamentosPorReposicion =   replacementOrderRepository.obtenerMedicamentosPorReposicion(idRepo);
        model.addAttribute("listaMedicamentosPorRepo",medicamentosPorReposicion);
        return "superAdmin/DetalleRepo";
    }


    //Solo para poder saltar entre vistas auxiliar de momento

    @GetMapping("/verPerfil")
    public String verPerfilSuper( Model model){
        Optional<SuperAdmin>superAdmin=  superAdminRepository.findById(2);
        model.addAttribute("superAdmin" , superAdmin.get());
        return "superAdmin/perfil";
    }

    @PostMapping("/editarPerfilSuper")
    public String editarDatosSuper(@ModelAttribute("superAdmin") @Valid SuperAdmin superAdmin, BindingResult bindingResult, Model model, RedirectAttributes attr, HttpSession httpSession){
        //Actualizar datos cambiados
        System.out.println(superAdmin.getIdSuperAdmin());
        SuperAdmin sessionSuper = (SuperAdmin) httpSession.getAttribute("usuario");

        int idUser = userRepository.encontrarId(sessionSuper.getEmail());


        if (bindingResult.hasErrors()) {
            return "superAdmin/perfil";
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encryptedPassword = passwordEncoder.encode(superAdmin.getPassword());
            attr.addFlashAttribute("msg", "SuperAdmin actualizado correctamente");
            superAdminRepository.actualizarPerfilSuperAdmin(superAdmin.getEmail(), superAdmin.getName(), superAdmin.getLastname(), superAdmin.getPassword());
            userRepository.actualizar(encryptedPassword,superAdmin.getEmail(),idUser);
            return "redirect:verPerfil";
        }

    }


}
