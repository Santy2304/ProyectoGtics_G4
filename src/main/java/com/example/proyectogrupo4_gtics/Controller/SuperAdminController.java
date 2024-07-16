package com.example.proyectogrupo4_gtics.Controller;
import com.example.proyectogrupo4_gtics.Config.ImpersonationAuthToken;
import com.example.proyectogrupo4_gtics.DTOs.CantidadMedicamentosDTO;
import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.Dao.DniDao;
import com.example.proyectogrupo4_gtics.Dao.PersonaDni;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Reportes.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.Service.EmailService;
import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.security.SecureRandom;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
@RequestMapping("/superAdmin")
public class  SuperAdminController {
    final MedicineRepository medicineRepository;
    final PatientRepository patientRepository;
    final DoctorRepository doctorRepository;

    @Autowired
    private EmailService emailService;

    //Clase para implementar el superlogueo
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private SessionRepository<? extends Session> sessionRepository;

    final UserRepository userRepository;

    final RolRepository rolRepository;

    final LoteRepository loteRepository;
    final AdministratorRepository administratorRepository;
    final SiteRepository siteRepository;

    final SuperAdminRepository superAdminRepository;
    final PharmacistRepository pharmacistRepository;
    private final ReplacementOrderRepository replacementOrderRepository;

    public SuperAdminController(MedicineRepository medicineRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, LoteRepository loteRepository, AdministratorRepository administratorRepository, SiteRepository siteRepository, PharmacistRepository pharmacistRepository,
                                ReplacementOrderRepository replacementOrderRepository,
                                SuperAdminRepository superAdminRepository, UserRepository userRepository,
                                RolRepository rolRepository) {
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
        this.rolRepository = rolRepository;
    }
    private String rutaAbsoluta = "//SaintMedic//imagenes";

    //Superlogueo//
    @GetMapping("/superlogueo")
    public String superlogueo(@RequestParam("id") String idUser, @RequestParam("rol") String rol, HttpSession originalSession, HttpServletRequest request) {
        //Obtener el usuario a suplantar para setearlo a la nueva sesión y obtener su username (email)
        String username = null;
        switch (rol){
            case "paciente":
                username = patientRepository.findById(Integer.parseInt(idUser)).get().getEmail();
                break;
            case "farmacista":
                username = pharmacistRepository.findById(Integer.parseInt(idUser)).get().getEmail();
                break;
            case "admin":
                username = administratorRepository.findById(Integer.parseInt(idUser)).get().getEmail();
                break;
        }
        if (username != null) {
            //Guardar la sesión del superadmin y su autenticación
            System.out.println(username);
            Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();

            //Realizar el superlogueo (suplantación)
            //System.out.println(username);
            UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
            Authentication impersonatedAuth = new ImpersonationAuthToken(userDetails);

            //Invalidar sesión actual y crear una nueva
            originalSession.invalidate();

            //Establecer la nueva sesión
            HttpSession impersonatedSession = request.getSession();
            SecurityContext newContext = SecurityContextHolder.createEmptyContext();
            newContext.setAuthentication(impersonatedAuth);
            SecurityContextHolder.setContext(newContext);
            impersonatedSession.setAttribute("SPRING_SECURITY_CONTEXT", newContext);
            //sesión para las validaciones de inicio de sesión
            switch (rol) {
                case "paciente":
                    Patient patient = patientRepository.findByEmail(username).get();
                    impersonatedSession.setAttribute("usuario", patient);
                    //Seteamos por defecto la sede Pando 1
                    impersonatedSession.setAttribute("idSede", 1);
                    break;
                case "farmacista":
                    impersonatedSession.setAttribute("usuario", pharmacistRepository.findByEmail(username));
                    break;
                case "admin":
                    impersonatedSession.setAttribute("usuario", administratorRepository.findByEmail(username));
                    System.out.println(administratorRepository.findByEmail(username).getName());
                    break;
            }

            return "redirect:/inicioSesion";
        } else {
            return "redirect:verListados";
        }
    }
    //Medicamentos///////////////////////////

    @GetMapping("/listaMedicamentos")
    public String listarMedicamentos(Model model) {
        model.addAttribute("listaMedicamentos", medicineRepository.obtenerDatosMedicamentos());
        return "superAdmin/listaMedicamentos";
    }
    //Filtro de la vista de medicamentos
    @PostMapping("listaMedicamentos")
    public String filtrosMedicamentos(@RequestParam("categoria") String categoria,
                                      @RequestParam("cantidad") String cantidad,
                                      @RequestParam("precio") String precio, Model model) {
        if (categoria.isEmpty() && cantidad.isEmpty() && precio.isEmpty()) {
            return "redirect:/listaMedicamentos"; //En caso dejen en blanco los valores del filtro
        }
        if (categoria.isEmpty()) {
            categoria = "%";
        }

        //Valores predeterminados de intervalos de cantidad y precio
        int cantInf = 0;
        int cantSup = 500;//No creo que hayan más de 500 unidades de un medicamento xd
        int precioInf = 0;
        int precioSup = 100; //No creo que un medicamento cueste más de 100 xd
        //Verificación de los parámetros del filtro para modificar los valores de los intervalos
        if (cantidad.equals("1")) { //Se eligió la opción de 0 - 25
            cantSup = 25;
        } else if (cantidad.equals("2")) { //Se eligió la opción de 25 - 50
            cantInf = 25;
            cantSup = 50;
        } else { //Se alteró el valor seleccionado por inspección
            return "redirect:/listaMedicamentos";
        }
        if (precio.equals("1")) { //Se eligió la opción de 0.0 - 25.0
            precioSup = 25;
        } else if (precio.equals("2")) { //Se eligió la opción de 25.0 - 50.0
            precioInf = 25;
            precioSup = 50;
        } else { //Se alteró el valor seleccionado por inspección
            return "redirect:/listaMedicamentos";
        }
        model.addAttribute("listaMedicamentos", medicineRepository.filtrarDatosMedicamentos(categoria, cantInf, cantSup, precioInf, precioSup));
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
                                    @RequestParam("medicineFile")MultipartFile imagen,
                                   @ModelAttribute("medicine") @Valid Medicine medicine,
                                   BindingResult bindingResult, Model model) {
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
                //Path directorioImagenMedicine= Paths.get("src//main//resources//static//assets_superAdmin//ImagenesMedicina");
                //String rutaAbsoluta =  directorioImagenMedicine.toFile().getAbsolutePath();
                //imagen a flujo bytes y poder guardarlo en la base de datos para poder extraerlo después
                try {
                    byte[] bytesImgMedicine = imagen.getBytes();
                    String fileOriginalName = imagen.getOriginalFilename();

                    long fileSize = imagen.getSize();
                    long maxFileSize  = 5*1024*1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                    //Nombre unico para la imagen

                    String nameFotoUnico = Calendar.getInstance().getTimeInMillis()+fileExtension;
                    if(fileSize>maxFileSize){
                        model.addAttribute("imageError","El tamaño de la imagen excede a 5MB");
                        return "superAdmin/anadirMedicamento";
                    }
                    if(
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                            !fileExtension.equalsIgnoreCase(".png") &&
                            !fileExtension.equalsIgnoreCase(".jpeg")
                    ){
                        model.addAttribute("imageError","El formato de la imagen debe ser jpg, jpeg o png");
                        return "superAdmin/anadirMedicamento";
                    }

                    //Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + nameFotoUnico);
                    Files.write(rutaCompleta,bytesImgMedicine);
                    //medicine.setPhoto(imagen.getOriginalFilename());
                    medicine.setPhoto(nameFotoUnico);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            medicineRepository.save(medicine);
            model.addAttribute("medicine", medicine);

            return "superAdmin/anadirLotesNuevoMedicamento";
        }
    }

    //Faltan validaciones de los stock
    @PostMapping("/crearLotesNuevoMedicamento")
    public String crearLoresNuevoMedicamento(
                                   @RequestParam("expireDate") String expireDateString,
                                   @RequestParam(value = "stockPando1", required = false, defaultValue = "0") int stockPando1,
                                   @RequestParam(value = "stockPando2",required = false, defaultValue = "0") int stockPando2,
                                   @RequestParam(value = "stockPando3",required = false, defaultValue = "0") int stockPando3,
                                   @RequestParam(value = "stockPando4",required = false, defaultValue = "0") int stockPando4,
                                   @RequestParam("medicineId") int medicineId, Model model) {

        Medicine medicine = medicineRepository.findById(medicineId).orElse(null);

        if (expireDateString.matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$")) {

            LocalDate expireDate = LocalDate.parse(expireDateString);

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
        } else {
            model.addAttribute("error", "Se debe ingresar una fecha válida y con el formato yyyy-MM-dd");
            model.addAttribute("medicine", medicine);
            return "superAdmin/anadirLotesNuevoMedicamento";
        }
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
                                            @RequestParam("disponibilidadPando4") String disponible4,
                                            @RequestParam("medicineFile") MultipartFile imagenEdit, Model model) {

        if(!imagenEdit.isEmpty()) {
            //ruta relativa para la imagen
            //Path directorioImagenMedicine = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesMedicina");

            //imagen a flujo bytes y poder guardarlo en la base de datos para poder extraerlo después
            String fileOriginalName = imagenEdit.getOriginalFilename();
            try {
                byte[] bytesImgMedicine = imagenEdit.getBytes();

                long fileSize = imagenEdit.getSize();
                long maxFileSize = 5 * 1024 * 1024;

                String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                //Edición nombre  único foto
                String nameFotoUnico = Calendar.getInstance().getTimeInMillis()+fileExtension;


                if (fileSize > maxFileSize) {
                    model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                    return "superAdmin/editarMedicamento";
                }
                if (
                        !fileExtension.equalsIgnoreCase(".jpg") &&
                                !fileExtension.equalsIgnoreCase(".png") &&
                                !fileExtension.equalsIgnoreCase(".jpeg")
                ) {
                    model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                    return "superAdmin/editarMedicamento";
                }

                //Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagenEdit.getOriginalFilename());
                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + nameFotoUnico);

                Files.write(rutaCompleta, bytesImgMedicine);
                //medicineRepository.actualizarMedicine(medicine.getName(), medicine.getCategory(), medicine.getPrice(), medicine.getDescription(), fileOriginalName, medicine.getIdMedicine());
                medicineRepository.actualizarMedicine(medicine.getName(), medicine.getCategory(), medicine.getPrice(), medicine.getDescription(), nameFotoUnico, medicine.getIdMedicine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            medicineRepository.actualizarMedicineSinFoto(medicine.getName(), medicine.getCategory(), medicine.getPrice(), medicine.getDescription(), medicine.getIdMedicine());
        }
        //Calendar calendar = Calendar.getInstance();

        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
        ;

        // Agregar tres años a la fecha actual
        //calendar.add(Calendar.YEAR, 3);

        // Obtener la nueva fecha después de agregar tres años
        LocalDate nuevaFecha = fechaActual.plusYears(3);

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
    //Filtros de la lista de usuarios
    @PostMapping("/verListados")
    public String filtroAdminSede(@RequestParam("rol") String rol,@RequestParam(value = "site", required = false) String site,
                                  @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
                                  @RequestParam(value = "fechaFinal", required = false) String fechaFinal,
                                  @RequestParam(value = "seguro", required = false) String seguro, Model model) {
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (site.isEmpty() && fechaInicio.isEmpty() && fechaFinal.isEmpty() && seguro.isEmpty()) {
            System.out.println(":(");
            return "redirect:verListados"; //En caso dejen en blanco los inputs y hagan click en filtrar
        } else {
            System.out.println(site);
            System.out.println("hola");
            //Definición de la variables de acuerdo a si se enviaron o dejaron en blanco
            if (site.isEmpty()) {
                site = "%"; //Para que el query devuelva la lista considerando todas las sedes
            }
            if (seguro.isEmpty()) {
                seguro = "%"; //Para que el query devuelva la lista considerando todos los seguros
            }
            //Casos predeterminados de los valores de fecha
            String initialDate = "";
            LocalDate finalDate = LocalDate.now();
            //Verificación de si se enviaron las fechas
            if (!fechaInicio.isEmpty()) {
                initialDate = LocalDate.parse(fechaInicio, frmt).toString();
            } else if (!fechaFinal.isEmpty()) {
                finalDate = LocalDate.parse(fechaFinal, frmt);
            }
            //Listas a enviar, algunas se actualizarán de acuerdo al filtro que se use
            List<Administrator> listaAdminSede = administratorRepository.listarAdminValidos();
            List<Pharmacist> listaFarmacistas = pharmacistRepository.listarFarmacistasValidos();
            List<Patient> listaPacientes = patientRepository.listarPacientesValidos();
            List<Doctor> listaDoctores = doctorRepository.listarDoctoresValidos();
            //Verficación de que filtro se está usando
            switch (rol) {
                case "adminSede":
                    listaAdminSede = administratorRepository.filtrarAdministradores(site, initialDate, finalDate);
                    model.addAttribute("adminSite",site);
                    break;
                case "farmacista":
                    listaFarmacistas = pharmacistRepository.filtrarFarmacistas(site, initialDate, finalDate);
                    model.addAttribute("farmaSite",site);
                    break;
                case "paciente":
                    listaPacientes = patientRepository.filtrarPacientes(seguro, initialDate, finalDate);
                    model.addAttribute("pacientSeg",seguro);
                    break;
                case "doctor":
                    listaDoctores = doctorRepository.filtrarDoctores(site, initialDate, finalDate);
                    model.addAttribute("doctorSite",site);
                    break;
                default:
                    return "redirect:verListados"; //En caso se haya modificado el valor de rol por inspección
            }
            model.addAttribute("listaDoctores", listaDoctores);
            model.addAttribute("listaAdminSede", listaAdminSede);
            model.addAttribute("listaFarmacistas",listaFarmacistas);
            model.addAttribute("listaPacientes",listaPacientes);
            //System.out.println(initialDate);
            return "superAdmin/Listados";
        }
    }
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
    //AdministradoresSede///////////////////////////7
    @GetMapping("/verAgregarAdminSede")
    public String verAgregarAdminSede(@ModelAttribute("adminSede") Administrator administrator, Model model) {
        List<Site> listaSedes = siteRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "superAdmin/AgregarAdminSede";
    }
    @PostMapping("/agregarAdminSede")
    public String agregarAdminSede(@RequestParam("adminFile")MultipartFile adminFoto, @ModelAttribute("adminSede") @Valid Administrator administrator
            , BindingResult bindingResult
            , RedirectAttributes attributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("listaSedes", siteRepository.findAll());
            return "superAdmin/AgregarAdminSede";
        } else {
            String siteCorreccion = administrator.getSite().replaceAll("^,", "");
            administrator.setSite(siteCorreccion);
            administrator.setCreationDate(LocalDate.now());
            administrator.setState("activo");
            administrator.setChangePassword(false);
            if (verificarUnicidadDni(administrator.getDni(), "Administrator")) {
                model.addAttribute("listaSedes", siteRepository.findAll());
                model.addAttribute("error", "El DNI del administrador ingresado ya existe");
                return "superAdmin/AgregarAdminSede";
            } else {
                if (adminFoto.isEmpty()) {
                    model.addAttribute("imageError", "Debe agregar una imagen");
                    return "superAdmin/AgregarAdminSede";
                }
                else{

                    //Path directorioImagenPerfil= Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");


                    try {
                        byte[] bytesImgPerfil = adminFoto.getBytes();
                        String fileOriginalName = adminFoto.getOriginalFilename();

                        long fileSize = adminFoto.getSize();
                        long maxFileSize  = 5*1024*1024;

                        String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                        //Foto de administrador unico
                        String fotoUnique = Calendar.getInstance().getTimeInMillis() + fileExtension;
                        if(fileSize>maxFileSize){
                            model.addAttribute("imageError","El tamaño de la imagen excede a 5MB");
                            return "superAdmin/AgregarAdminSede";
                        }
                        if(
                                !fileExtension.equalsIgnoreCase(".jpg") &&
                                        !fileExtension.equalsIgnoreCase(".png") &&
                                        !fileExtension.equalsIgnoreCase(".jpeg")
                        ){
                            model.addAttribute("imageError","El formato de la imagen debe ser jpg, jpeg o png");
                            return "superAdmin/AgregarAdminSede";
                        }

                        //Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + adminFoto.getOriginalFilename());
                        Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + fotoUnique);

                        Files.write(rutaCompleta,bytesImgPerfil);
                        //administrator.setPhoto(adminFoto.getOriginalFilename());
                        administrator.setPhoto(fotoUnique);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    attributes.addFlashAttribute("msg", "Administrador agregado correctamente");

                    administratorRepository.save(administrator);
                    User user = new User();
                    user.setEmail(administrator.getEmail());
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

                    String password = generateRandomWord();

                    String encryptedPassword = passwordEncoder.encode(password);
                    user.setPassword(encryptedPassword);
                    user.setIdRol(rolRepository.findById(2).get());
                    user.setState(true);
                    userRepository.save(user);

                    try {
                        emailService.sendHtmlMessage(user.getEmail(), "Bienvenido a SaintMedic", administrator.getName(), password);
                    } catch (MessagingException | IOException e) {
                        e.printStackTrace();
                    }

                    return "redirect:verListados";
                }

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
    public String editarAdminSede(@RequestParam("adminFile")MultipartFile adminFoto, @ModelAttribute("adminSede") @Valid Administrator administrator, BindingResult bindingResult, RedirectAttributes attributes, Model model){
        //    void updateDatosPorId(String name , String lasName , int dni , String email , int idDoctor );
        if (bindingResult.hasErrors()) {
            return "superAdmin/EditarAdministrador";
        }else {

            Administrator administratorPasado = administratorRepository.findById(administrator.getIdAdministrador()).get();

            if (!administratorPasado.getSite().equals(administrator.getSite()) || !administratorPasado.getName().equals(administrator.getName()) || !administratorPasado.getLastName().equals(administrator.getLastName()) || !administratorPasado.getEmail().equals(administrator.getEmail())){
                try {
                    emailService.sendHtmlEditAdmin(administrator.getEmail(),"Cambios en tu usuario",administrator.getName(),administrator.getLastName(),administrator.getSite(),administrator.getEmail());
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }

            }

            if (administratorPasado.getState().equals("baneado") && administrator.getState().equals("activo")){
                try {
                    emailService.sendHtmlBanDele(administrator.getEmail(), "Regreso a Saint Medic", administrator.getName(),"Ha sido desbaneado de Saint Medic","Hemos tomado la decisión de devolverle el acceso a la plataforma, a partir de ahora ya puede ingresar.");
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }



            if (administrator.getState().equals("baneado")){
                userRepository.banear(administrator.getEmail());
                try {
                    emailService.sendHtmlBanDele(administrator.getEmail(), "Ha sido baneado de Saint Medic", administrator.getName(),"Ha sido baneado temporalmente de Saint Medic","Lo sentimos pero hemos decidido restringirle el acceso a la plataforma hasta nuevo aviso, por lo tanto ya no podrá acceder a ella.");
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (administrator.getState().equals("activo")){
                userRepository.desbanear(administrator.getEmail());
            }

            if (adminFoto.isEmpty()) {
                administratorRepository.updateDatosPorIdSinFoto(administrator.getName(), administrator.getLastName(), administrator.getDni(), administrator.getEmail(), administrator.getSite(), administrator.getState(), administrator.getIdAdministrador());

            }
            else {
                //Path directorioImagenPerfil= Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");


                try {
                    byte[] bytesImgPerfil = adminFoto.getBytes();
                    String fileOriginalName = adminFoto.getOriginalFilename();

                    long fileSize = adminFoto.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                    //Foto unico
                    String editFotoUnique = Calendar.getInstance().getTimeInMillis() + fileExtension;
                    if (fileSize > maxFileSize) {
                        model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                        return "superAdmin/EditarAdministrador";
                    }
                    if (
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                                    !fileExtension.equalsIgnoreCase(".png") &&
                                    !fileExtension.equalsIgnoreCase(".jpeg")
                    ) {
                        model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                        return "superAdmin/EditarAdministrador";
                    }

                    //Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + adminFoto.getOriginalFilename());
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + editFotoUnique);

                    Files.write(rutaCompleta, bytesImgPerfil);
                    //administrator.setPhoto(adminFoto.getOriginalFilename());

                    //administratorRepository.updateDatosPorId(administrator.getName(), administrator.getLastName(), administrator.getDni(), administrator.getEmail(), administrator.getSite(), administrator.getState(), adminFoto.getOriginalFilename(), administrator.getIdAdministrador());
                    administratorRepository.updateDatosPorId(administrator.getName(), administrator.getLastName(), administrator.getDni(), administrator.getEmail(), administrator.getSite(), administrator.getState(), editFotoUnique, administrator.getIdAdministrador());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                attributes.addFlashAttribute("msg", "Administrador actualizado correctamente");
            }

            return "redirect:verListados";
        }
    }
    @GetMapping("/eliminarAdminSede")
    public String eliminarAdminSede(@RequestParam("idAdminSede") int idAdminSede) {
        administratorRepository.eliminarAdminPorId(idAdminSede);
        Administrator administrator = administratorRepository.findById(idAdminSede).get();
        User user = userRepository.findByEmail(administrator.getEmail());
        userRepository.delete(user);
        try {
            emailService.sendHtmlBanDele(administrator.getEmail(), "Ha sido eliminado de Saint Medic", administrator.getName(),"Ha sido eliminado permanentemente de Saint Medic","Lo sentimos pero hemos decidido eliminarlo de forma permanente de la plataforma, por lo tanto ya no podrá acceder a ella. Agradecemos sus servicios a la compañia.");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return "redirect:verListados";
    }

    //Farmacista///////////////////////////////
    @GetMapping("/editarFarmacista")
    public String verEditarFarmacista( @ModelAttribute("farmacista") Pharmacist pharmacist, @RequestParam("idFarmacista") int idFarmacista , Model model) {

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
    public String editarFarmacista(@RequestParam("fotoFarm")MultipartFile farmFoto, @ModelAttribute("farmacista") @Valid Pharmacist pharmacist, BindingResult bindingResult, RedirectAttributes attributes, Model model){
        if (bindingResult.hasErrors()) {
            return "superAdmin/EditarFarmacista";
        } else {

            Pharmacist pharmacistPasado = pharmacistRepository.findById(pharmacist.getIdFarmacista()).get();

            if (!pharmacistPasado.getSite().equals(pharmacist.getSite()) || !pharmacistPasado.getName().equals(pharmacist.getName()) || !pharmacistPasado.getLastName().equals(pharmacist.getLastName()) || !pharmacistPasado.getEmail().equals(pharmacist.getEmail()) || !pharmacistPasado.getDistrit().equals(pharmacist.getDistrit())){
                try {
                    emailService.sendHtmlEditPharma(pharmacist.getEmail(),"Cambios en tu usuario",pharmacist.getName(),pharmacist.getLastName(),pharmacist.getSite(),pharmacist.getEmail(),pharmacist.getDistrit());
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }

            }

            if (pharmacistPasado.getState().equals("baneado") && pharmacist.getState().equals("activo")){
                try {
                    emailService.sendHtmlBanDele(pharmacist.getEmail(), "Regreso a Saint Medic", pharmacist.getName(),"Ha sido desbaneado de Saint Medic","Hemos tomado la decisión de devolverle el acceso a la plataforma, a partir de ahora ya puede ingresar.");
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }


            if (pharmacist.getState().equals("baneado")){
                userRepository.banear(pharmacist.getEmail());
                try {
                    emailService.sendHtmlBanDele(pharmacist.getEmail(), "Ha sido baneado de Saint Medic", pharmacist.getName(),"Ha sido baneado temporalmente de Saint Medic","Lo sentimos pero hemos decidido restringirle el acceso a la plataforma hasta nuevo aviso, por lo tanto ya no podrá acceder a ella.");
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (pharmacist.getState().equals("activo")){
                userRepository.desbanear(pharmacist.getEmail());
            }

            if (farmFoto.isEmpty()) {
                pharmacistRepository.updateDatosPorIdSinFoto(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getEmail(), pharmacist.getSite(), pharmacist.getState(), pharmacist.getDistrit(),pharmacist.getIdFarmacista());
            }
            else {
                //Path directorioImagenPerfil= Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");


                try {
                    byte[] bytesImgPerfil = farmFoto.getBytes();
                    String fileOriginalName = farmFoto.getOriginalFilename();

                    long fileSize = farmFoto.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                    //foto unica
                    String editFotoUnique = Calendar.getInstance().getTimeInMillis()+fileExtension;
                    if (fileSize > maxFileSize) {
                        model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                        return "superAdmin/EditarAdministrador";
                    }
                    if (
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                                    !fileExtension.equalsIgnoreCase(".png") &&
                                    !fileExtension.equalsIgnoreCase(".jpeg")
                    ) {
                        model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                        return "superAdmin/EditarAdministrador";
                    }

                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + farmFoto.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImgPerfil);
                    //administrator.setPhoto(adminFoto.getOriginalFilename());

                    pharmacistRepository.updateDatosPorId(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getEmail(), pharmacist.getSite(), pharmacist.getState(), pharmacist.getDistrit(), farmFoto.getOriginalFilename() ,pharmacist.getIdFarmacista());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                attributes.addFlashAttribute("msg", "Farmacista actualizado correctamente");
            }

            return "redirect:verListados";
        }
    }
    @GetMapping("/eliminarFarmacista")
    public String eliminarFarmacista(@RequestParam("idFarmacista") int idFarmacista) {
        pharmacistRepository.eliminarFarmacistaPorId(idFarmacista);
        Pharmacist pharmacist = pharmacistRepository.findById(idFarmacista).get();
        User user = userRepository.findByEmail(pharmacist.getEmail());
        userRepository.delete(user);
        try {
            emailService.sendHtmlBanDele(pharmacist.getEmail(), "Eliminado de Saint Medic", pharmacist.getName(),"Ha sido eliminado permanente mente de Saint Medic","Lo sentimos pero hemos decidido eliminarlo de forma permanente de la plataforma, por lo tanto ya no podrá acceder a ella. Agradecemos sus servicios a la compañia.");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return "redirect:verListados";
    }
    /*
    @GetMapping("/rechazarFarmacista")
    public String rechazarFarmacista(@RequestParam("idFarmacista") int idFarmacista) {
        pharmacistRepository.rechazarFarmacistaPorId(idFarmacista);
        //pharmacistRepository.deleteById(idFarmacista);
        Pharmacist pharmacist = pharmacistRepository.findById(idFarmacista).get();
        try {
            emailService.sendHtmlRechazo(pharmacist.getEmail(), "Ha sido rechazado de Saint Medic", pharmacist.getName(),"me llego al pincho");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return "redirect:verListados";
    }
*/


    @GetMapping("/aceptarFarmacista")
    public String aceptarFarmacista(@RequestParam("idFarmacista") int idFarmacista, HttpServletRequest request) {
        pharmacistRepository.aceptarFarmacistaPorId(idFarmacista);
        User user = new User();
        Pharmacist pharmacist = pharmacistRepository.getByIdFarmacista(idFarmacista);
        user.setEmail(pharmacist.getEmail());
        user.setState(true);
        String password = generateRandomWord();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setIdRol(rolRepository.findById(3).get());
        userRepository.save(user);
        try {
            emailService.sendHtmlMessage(user.getEmail(), "Bienvenido a SaintMedic", pharmacist.getName(), password);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "verListados");
    }

    ////Paciente////////////////////
    @GetMapping("/eliminarPaciente")
    public String eliminarPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.eliminarPacientePorId(idPaciente);
        return "redirect:verListados";
    }
    @GetMapping("/banearPaciente")
    public String banearPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.banearPacientePorId(idPaciente);
        Patient patient = patientRepository.findById(idPaciente).get();
        userRepository.banear(patient.getEmail());
        return "redirect:verListados";
    }
    @GetMapping("/desbanearPaciente")
    public String desbanearPaciente(@RequestParam("idPaciente") int idPaciente) {
        patientRepository.desbanearPacientePorId(idPaciente);
        Patient patient = patientRepository.findById(idPaciente).get();
        userRepository.desbanear(patient.getEmail());
        return "redirect:verListados";
    }
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
    //FIltro de las vistas de sede
    @PostMapping("/verSedeSuperAdminPando1")
    public String filtrosPando1(@RequestParam(value = "estado", required = false) String estado,
                                @RequestParam("fechaInicio") String fechaInicio,
                                @RequestParam("fechaFinal") String fechaFinal, Model model) {
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (estado.isEmpty() && fechaInicio.isEmpty() && fechaFinal.isEmpty()) {
            return "redirect:verSedeSuperAdminPando1";
        }
        //Casos predeterminados de los valores de fecha
        String initialDate = "";
        LocalDate finalDate = LocalDate.now();
        //Verificación de si se enviaron las fechas
        if (!fechaInicio.isEmpty()) {
            initialDate = LocalDate.parse(fechaInicio, frmt).toString();
        } else if (!fechaFinal.isEmpty()) {
            finalDate = LocalDate.parse(fechaFinal, frmt);
        }
        //Listas a enviar en la vista, pueden cambiar de acuerdo al filtro
        List<Pharmacist> listaSolicitudesFarmacistaPando1 = pharmacistRepository.listarSolicitudesFarmacistaPando1();
        List<ReplacementOrder> listarSolicitudesReposicionPando1 = replacementOrderRepository.obtenerSolicitudesRepoPando1();
        if (estado.isEmpty()) { //Filtro de lista de solicitudes
            listaSolicitudesFarmacistaPando1 = pharmacistRepository.filtrarSolicitudesDeFarmacistas("Pando 1", initialDate, finalDate);
        } else { //Filtro de lista de reposición
            listarSolicitudesReposicionPando1 = replacementOrderRepository.filtrarSolicitudesRepo("Pando 1", estado, initialDate, finalDate);
        }
        model.addAttribute("listaSolicitudesFarmacistasPando1",listaSolicitudesFarmacistaPando1);
        model.addAttribute("listaSolicitudesReposicionPando1",listarSolicitudesReposicionPando1);
        return "superAdmin/SedePando1";
    }
    @PostMapping("/verSedeSuperAdminPando2")
    public String filtrosPando2(@RequestParam(value = "estado", required = false) String estado,
                                @RequestParam("fechaInicio") String fechaInicio,
                                @RequestParam("fechaFinal") String fechaFinal, Model model) {
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (estado.isEmpty() && fechaInicio.isEmpty() && fechaFinal.isEmpty()) {
            return "redirect:verSedeSuperAdminPando2";
        }
        //Casos predeterminados de los valores de fecha
        String initialDate = "";
        LocalDate finalDate = LocalDate.now();
        //Verificación de si se enviaron las fechas
        if (!fechaInicio.isEmpty()) {
            initialDate = LocalDate.parse(fechaInicio, frmt).toString();
        } else if (!fechaFinal.isEmpty()) {
            finalDate = LocalDate.parse(fechaFinal, frmt);
        }
        //Listas a enviar en la vista, pueden cambiar de acuerdo al filtro
        List<Pharmacist> listaSolicitudesFarmacistaPando2 = pharmacistRepository.listarSolicitudesFarmacistaPando2();
        List<ReplacementOrder> listarSolicitudesReposicionPando2 = replacementOrderRepository.obtenerSolicitudesRepoPando2();
        if (estado.isEmpty()) { //Filtro de lista de solicitudes
            listaSolicitudesFarmacistaPando2 = pharmacistRepository.filtrarSolicitudesDeFarmacistas("Pando 2", initialDate, finalDate);
        } else { //Filtro de lista de reposición
            listarSolicitudesReposicionPando2 = replacementOrderRepository.filtrarSolicitudesRepo("Pando 2", estado, initialDate, finalDate);
        }
        model.addAttribute("listaSolicitudesFarmacistasPando2",listaSolicitudesFarmacistaPando2);
        model.addAttribute("listaSolicitudesReposicionPando2",listarSolicitudesReposicionPando2);
        return "superAdmin/SedePando2";
    }
    @PostMapping("/verSedeSuperAdminPando3")
    public String filtrosPando3(@RequestParam(value = "estado", required = false) String estado,
                                @RequestParam("fechaInicio") String fechaInicio,
                                @RequestParam("fechaFinal") String fechaFinal, Model model) {
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (estado.isEmpty() && fechaInicio.isEmpty() && fechaFinal.isEmpty()) {
            return "redirect:verSedeSuperAdminPando3";
        }
        //Casos predeterminados de los valores de fecha
        String initialDate = "";
        LocalDate finalDate = LocalDate.now();
        //Verificación de si se enviaron las fechas
        if (!fechaInicio.isEmpty()) {
            initialDate = LocalDate.parse(fechaInicio, frmt).toString();
        } else if (!fechaFinal.isEmpty()) {
            finalDate = LocalDate.parse(fechaFinal, frmt);
        }
        //Listas a enviar en la vista, pueden cambiar de acuerdo al filtro
        List<Pharmacist> listaSolicitudesFarmacistaPando3 = pharmacistRepository.listarSolicitudesFarmacistaPando3();
        List<ReplacementOrder> listarSolicitudesReposicionPando3 = replacementOrderRepository.obtenerSolicitudesRepoPando3();
        if (estado.isEmpty()) { //Filtro de lista de solicitudes
            listaSolicitudesFarmacistaPando3 = pharmacistRepository.filtrarSolicitudesDeFarmacistas("Pando 3", initialDate, finalDate);
        } else { //Filtro de lista de reposición
            listarSolicitudesReposicionPando3 = replacementOrderRepository.filtrarSolicitudesRepo("Pando 3", estado, initialDate, finalDate);
        }
        model.addAttribute("listaSolicitudesFarmacistasPando3",listaSolicitudesFarmacistaPando3);
        model.addAttribute("listaSolicitudesReposicionPando3",listarSolicitudesReposicionPando3);
        return "superAdmin/SedePando3";
    }
    @PostMapping("/verSedeSuperAdminPando4")
    public String filtrosPando4(@RequestParam(value = "estado", required = false) String estado,
                                @RequestParam("fechaInicio") String fechaInicio,
                                @RequestParam("fechaFinal") String fechaFinal, Model model) {
        DateTimeFormatter frmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (estado.isEmpty() && fechaInicio.isEmpty() && fechaFinal.isEmpty()) {
            return "redirect:verSedeSuperAdminPando4";
        }
        //Casos predeterminados de los valores de fecha
        String initialDate = "";
        LocalDate finalDate = LocalDate.now();
        //Verificación de si se enviaron las fechas
        if (!fechaInicio.isEmpty()) {
            initialDate = LocalDate.parse(fechaInicio, frmt).toString();
        } else if (!fechaFinal.isEmpty()) {
            finalDate = LocalDate.parse(fechaFinal, frmt);
        }
        //Listas a enviar en la vista, pueden cambiar de acuerdo al filtro
        List<Pharmacist> listaSolicitudesFarmacistaPando4 = pharmacistRepository.listarSolicitudesFarmacistaPando4();
        List<ReplacementOrder> listarSolicitudesReposicionPando4 = replacementOrderRepository.obtenerSolicitudesRepoPando4();
        if (estado.isEmpty()) { //Filtro de lista de solicitudes
            listaSolicitudesFarmacistaPando4 = pharmacistRepository.filtrarSolicitudesDeFarmacistas("Pando 4", initialDate, finalDate);
        } else { //Filtro de lista de reposición
            listarSolicitudesReposicionPando4 = replacementOrderRepository.filtrarSolicitudesRepo("Pando 4", estado, initialDate, finalDate);
        }
        model.addAttribute("listaSolicitudesFarmacistasPando4",listaSolicitudesFarmacistaPando4);
        model.addAttribute("listaSolicitudesReposicionPando4",listarSolicitudesReposicionPando4);
        return "superAdmin/SedePando4";
    }

    @GetMapping("/verTrackingPersonal")
    public String verTrackingPersonal(@RequestParam("idRepo") int idReplacementeOrder , Model model){
        String activeTab = replacementOrderRepository.findById(idReplacementeOrder).get().getSite();
        Tracking tracking = replacementOrderRepository.findById(idReplacementeOrder).get().getIdTracking();
        model.addAttribute("idReplacement",idReplacementeOrder);
        model.addAttribute("listaMedicamentos", replacementOrderRepository.obtenerMedicamentosPorReposicion(idReplacementeOrder));
        model.addAttribute("solicitudDate", tracking.getSolicitudDate().minusHours(5));
        model.addAttribute("enProcesoDate", tracking.getEnProcesoDate().minusHours(5));
        model.addAttribute("empaquetadoDate", tracking.getEmpaquetadoDate().minusHours(5));
        model.addAttribute("enRutaDate", tracking.getEnRutaDate().minusHours(5));
        model.addAttribute("entregadoDate", tracking.getEntregadoDate().minusHours(5));
        model.addAttribute("activeTab", activeTab);
        return "superAdmin/TrackingPersonalSuperAdmin";
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
        Optional<SuperAdmin>superAdmin=  superAdminRepository.findById(1);
        model.addAttribute("superAdmin" , superAdmin.get());
        return "superAdmin/perfil";
    }
    @PostMapping("/editarPerfilSuper")
    public String editarDatosSuper(@RequestParam("superAdminFile") MultipartFile imagen,@ModelAttribute("superAdmin") @Valid SuperAdmin superAdmin, BindingResult bindingResult, Model model, RedirectAttributes attr, HttpSession httpSession){
        //Actualizar datos cambiados
        System.out.println(superAdmin.getIdSuperAdmin());
        SuperAdmin sessionSuper = (SuperAdmin) httpSession.getAttribute("usuario");

        int idUser = userRepository.encontrarId(sessionSuper.getEmail());


        if (bindingResult.hasErrors()) {
            return "superAdmin/perfil";
        } else {
            if (imagen.isEmpty()) {
                model.addAttribute("imageError", "Debe agregar una imagen");
                return "superAdmin/perfil";
            }
            else {

                //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");

                //String rutaAbsoluta = directorioImagenPerfil.toFile().getAbsolutePath();

                try {
                    byte[] bytesImgPerfil = imagen.getBytes();
                    String fileOriginalName = imagen.getOriginalFilename();

                    long fileSize = imagen.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                    if (fileSize > maxFileSize) {
                        model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                        return "superAdmin/perfil";
                    }
                    if (
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                                    !fileExtension.equalsIgnoreCase(".png") &&
                                    !fileExtension.equalsIgnoreCase(".jpeg")
                    ) {
                        model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                        return "superAdmin/perfil";
                    }

                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImgPerfil);
                    //patient.setPhoto(imagen.getOriginalFilename());
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encryptedPassword = passwordEncoder.encode(superAdmin.getPassword());
                    attr.addFlashAttribute("msg", "SuperAdmin actualizado correctamente");
                    superAdminRepository.actualizarPerfilSuperAdmin(superAdmin.getEmail(), superAdmin.getName(), superAdmin.getLastname(), superAdmin.getPassword(), imagen.getOriginalFilename());
                    userRepository.actualizar(encryptedPassword,superAdmin.getEmail(),idUser);
                    return "redirect:verPerfil";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            /*
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encryptedPassword = passwordEncoder.encode(superAdmin.getPassword());
            attr.addFlashAttribute("msg", "SuperAdmin actualizado correctamente");
            superAdminRepository.actualizarPerfilSuperAdmin(superAdmin.getEmail(), superAdmin.getName(), superAdmin.getLastname(), superAdmin.getPassword());
            userRepository.actualizar(encryptedPassword,superAdmin.getEmail(),idUser);
            return "redirect:verPerfil";

             */
        }

    }
    //////////////////////////////////REPORTES///////////////////////////////////////
    @GetMapping("/exportarMedicamentosPDF")
    public void exportarMedicamentosPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Medicamentos_" + fechaActual + ".pdf";
        response.setHeader(cabecera, valor);



        List<CantidadMedicamentosDTO> medicines = medicineRepository.obtenerDatosMedicamentos();

        MedicinePDF exporter = new MedicinePDF(medicines);
        exporter.exportar(response);
    }
    @GetMapping("/exportarMedicamentosExcel")
    public void exportarMedicamentosExcel(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Medicamentos_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<CantidadMedicamentosDTO> medicamentos = medicineRepository.obtenerDatosMedicamentos();
        String titulo = "Listado de Productos";

        MedicineExcel exporter = new MedicineExcel(medicamentos,titulo);
        exporter.exportar(response);
    }
    @GetMapping("/exportarAdministradoresPDF")
    public void exportarAdminPDF(HttpServletResponse response,@RequestParam("filtro") String filtro) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Administradores_" + fechaActual + ".pdf";
        List<Administrator> administrators = new ArrayList<>();

        String titulo;
        if (!filtro.equals("null")){
            administrators = administratorRepository.filtrarAdministradoresSede(filtro);
            titulo = "Lista de Administradores de "+filtro;
            valor = "attachment; filename=Administradores_"+ filtro +"_"+ fechaActual + ".pdf";

        }else{
            administrators = administratorRepository.listarAdminValidos();
            titulo = "Lista de Administradores";
        }
        response.setHeader(cabecera, valor);


        AdminPDF exporter = new AdminPDF(administrators,titulo);
        exporter.exportar(response);
    }
    @GetMapping("/exportarAdministradoresExcel")
    public void exportarAdministradoresExcel(HttpServletResponse response,@RequestParam("filtro") String filtro) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Administradores_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        String titulo;
        List<Administrator> administrators = new ArrayList<>();
        if(!filtro.equals("null")){
            titulo = "Lista de Administradores de "+filtro;
            administrators = administratorRepository.filtrarAdministradoresSede(filtro);

        }else{
           administrators = administratorRepository.listarAdminValidos();
            titulo = "Lista de Administradores";
        }


        AdminExcel exporter = new AdminExcel(administrators,titulo);
        exporter.exportar(response);

    }
    @GetMapping("/exportarFarmacistasPDF")
    public void exportarFarmaPDF(HttpServletResponse response,@RequestParam("filtro") String filtro) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Farmacistas_" + fechaActual + ".pdf";
        response.setHeader(cabecera, valor);
        String titulo;

        List<Pharmacist> pharmacists = new ArrayList<>();
        if(!filtro.equals("null")){
            titulo = "Lista de farmacistas de "+filtro;
            pharmacists = pharmacistRepository.filtrarFarmacistasSede(filtro);

        }else{
            pharmacists= pharmacistRepository.listarFarmacistasValidos();
            titulo = "Lista de Administradores";
        }

        FarmacistaPDF exporter = new FarmacistaPDF(pharmacists,titulo);
        exporter.exportar(response);
    }
    @GetMapping("/exportarFarmacistasExcel")
    public void exportarFarmacistasExcel(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Farmacistas_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);


        List<Pharmacist> pharmacists = pharmacistRepository.listarFarmacistasValidos();
        String titulo = "Lista de Farmacistas";

        FarmacistaExcel exporter = new FarmacistaExcel(pharmacists,titulo);
        exporter.exportar(response);

    }
    @GetMapping("/exportarPacientesPDF")
    public void exportarPacientesPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());
        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Pacientes_" + fechaActual + ".pdf";
        response.setHeader(cabecera, valor);

        List<Patient> patients = patientRepository.listarPacientesValidos();
        PacientePDF exporter = new PacientePDF(patients);
        exporter.exportar(response);
    }
    @GetMapping("/exportarPacientesExcel")
    public void exportarPacientesExcel(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Pacientes_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Patient> patients = patientRepository.listarPacientesValidos();
        String titulo = "Lista de pacientes";

        PacienteExcel exporter = new PacienteExcel(patients,titulo);
        exporter.exportar(response);

    }
    @GetMapping("/exportarDoctoresPDF")
    public void exportarDoctoresPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Doctores_" + fechaActual + ".pdf";
        response.setHeader(cabecera, valor);

        List<Doctor> doctors = doctorRepository.listarDoctoresValidos();
        DoctoresPDF exporter = new DoctoresPDF(doctors);
        exporter.exportar(response);
    }
    @GetMapping("/exportarDoctoresExcel")
    public void exportarDoctoresExcel(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Doctores_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Doctor> doctors = doctorRepository.listarDoctoresValidos();
        String titulo = "Lista de doctores";
        DoctoresExcel exporter = new DoctoresExcel(doctors,titulo);
        exporter.exportar(response);

    }

    @GetMapping("/exportarRepoPDF")
    public void exportaRepoPDF(HttpServletResponse response, @RequestParam("sede") int sede) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Ordenes_de_Reposición_" + fechaActual + ".pdf";
        response.setHeader(cabecera, valor);

        switch (sede){

            case 1:
                List<ReplacementOrder> listaRepo1 = replacementOrderRepository.obtenerSolicitudesRepoPando1();
                String titulo = "Lista de solicitudes de reposición de Pando 1";
                RepoPDF repoPDF = new RepoPDF(listaRepo1,titulo);
                repoPDF.exportar(response);
                break;

            case 2:
                List<ReplacementOrder> listaRepo2 = replacementOrderRepository.obtenerSolicitudesRepoPando2();
                String titulo2 = "Lista de solicitudes de reposición de Pando 2";
                RepoPDF repoPDF2 = new RepoPDF(listaRepo2,titulo2);
                repoPDF2.exportar(response);
                break;

            case 3:
                List<ReplacementOrder> listaRepo3 = replacementOrderRepository.obtenerSolicitudesRepoPando3();
                String titulo3 = "Lista de solicitudes de reposición de Pando 3";
                RepoPDF repoPDF3 = new RepoPDF(listaRepo3,titulo3);
                repoPDF3.exportar(response);
                break;

            case 4:
                List<ReplacementOrder> listaRepo4 = replacementOrderRepository.obtenerSolicitudesRepoPando4();
                String titulo4 = "Lista de solicitudes de reposición de Pando 2";
                RepoPDF repoPDF4 = new RepoPDF(listaRepo4,titulo4);
                repoPDF4.exportar(response);
                break;

        }

    }
    @GetMapping("/exportarRepoExcel")
    public void exportarRepoExcel(HttpServletResponse response,@RequestParam("sede") int sede) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Ordenes_De_Reposición_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        switch (sede){

            case 1:
                List<ReplacementOrder> listaRepo1 = replacementOrderRepository.obtenerSolicitudesRepoPando1();
                String titulo = "Lista de solicitudes de reposición de Pando 1";
                RepoExcel exporter = new RepoExcel(listaRepo1,titulo);
                exporter.exportar(response);
                break;

            case 2:
                List<ReplacementOrder> listaRepo2 = replacementOrderRepository.obtenerSolicitudesRepoPando2();
                String titulo2 = "Lista de solicitudes de reposición de Pando 2";
                RepoExcel exporter2 = new RepoExcel(listaRepo2,titulo2);
                exporter2.exportar(response);
                break;

            case 3:
                List<ReplacementOrder> listaRepo3 = replacementOrderRepository.obtenerSolicitudesRepoPando3();
                String titulo3 = "Lista de solicitudes de reposición de Pando 3";
                RepoExcel exporter3 = new RepoExcel(listaRepo3,titulo3);
                exporter3.exportar(response);
                break;

            case 4:
                List<ReplacementOrder> listaRepo4 = replacementOrderRepository.obtenerSolicitudesRepoPando4();
                String titulo4 = "Lista de solicitudes de reposición de Pando 2";
                RepoExcel exporter4 = new RepoExcel(listaRepo4,titulo4);
                exporter4.exportar(response);
                break;

        }

    }



    /////////////////////////////////////////////////////////////////////////////////////////7
    public String generateRandomWord() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String characters = letters + numbers;
        int wordLength = 8;
        Random random = new SecureRandom();
        StringBuilder word = new StringBuilder(wordLength);

        // Ensure at least one letter
        word.append(letters.charAt(random.nextInt(letters.length())));

        // Ensure at least one number
        word.append(numbers.charAt(random.nextInt(numbers.length())));

        // Fill the rest of the word with random characters
        for (int i = 2; i < wordLength; i++) {
            word.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Shuffle the characters to ensure randomness
        char[] wordArray = word.toString().toCharArray();
        for (int i = 0; i < wordArray.length; i++) {
            int randomIndex = random.nextInt(wordArray.length);
            char temp = wordArray[i];
            wordArray[i] = wordArray[randomIndex];
            wordArray[randomIndex] = temp;
        }

        return new String(wordArray);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////

    @ExceptionHandler({HttpMediaTypeException.class ,   ClassCastException.class , MethodArgumentTypeMismatchException.class})
    public Object gestionExcetion(HttpServletRequest request) {
        HashMap<String, Object> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("GET")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Te equivocaste en algún parámetro");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

    //SERVICIOS
    @PostMapping("/rechazarFarmacista")
    public ResponseEntity<Object> rechazarSolicitud(@RequestParam(value = "idFarmacista" , required = false) int idFarmacista,
                                                @RequestParam(value = "motivo", required = false) String motivo) {
        LinkedHashMap<String, Object > generalResponse = new LinkedHashMap<>();
        try{
            if(idFarmacista == 0 ){
                generalResponse.put("status","error");
                generalResponse.put("message","debes de ingresar un numero en el parametro del idFarmacista" );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            if(motivo==null){
                generalResponse.put("status","error");
                generalResponse.put("message","debes de ingresar un motivo" );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            if(!pharmacistRepository.findById(idFarmacista).isPresent() ){
                generalResponse.put("status","error");
                generalResponse.put("message","El farmacista no existe" );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generalResponse);
            }
        HashMap<String, Object> response = new HashMap<>();
        pharmacistRepository.rechazarFarmacistaPorId(idFarmacista);
        Pharmacist pharmacist = pharmacistRepository.findById(idFarmacista).get();
        response.put("Success", "Solicitud rechazada con éxito. Motivo: " + motivo);
        try {
            emailService.sendHtmlRechazo(pharmacist.getEmail(), "Ha sido rechazado de Saint Medic", pharmacist.getName(),motivo);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        pharmacistRepository.deleteById(idFarmacista);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace();
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Se produjo un error al rechazar la solicitud.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}


    @GetMapping(value="/getDni")
    public Object getDni(@RequestParam("dni") String  dni , Model model ) {
        try {
            PersonaDni p = new DniDao().buscarDatosPorDNI(dni);
            model.addAttribute("santiago",  p);
            return ResponseEntity.ok(p);
        } catch (Exception err) {
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "No se encontro el DNI");
            return ResponseEntity.badRequest().body(er);
        }
    }

}
