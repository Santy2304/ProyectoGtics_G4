package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.MeciamentosPorCompraDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchasePorPatientDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Service.Dialogflow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteArrayDataInput;
import com.google.rpc.Help;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@SessionAttributes({"idUser","idSede", "carrito"})
@RequestMapping("/patient")
public class PatientController {
    @ModelAttribute("carrito")
    public ArrayList<Medicamento> carrito() {
        return new ArrayList<Medicamento>();
    }
    final SiteRepository siteRepository;
    final ChatRepository chatRepository;
    final PatientRepository patientRepository;
    final MedicineRepository medicineRepository;
    final UserRepository userRepository;
    final PurchaseHasLoteRepository purchaseHasLoteRepository;
    final PurchaseOrderRepository purchaseOrderRepository;
    private final DoctorRepository doctorRepository;
    private final LoteRepository loteRepository;
    private final CarritoRepository carritoRepository;

    final NotificationsRepository notificationsRepository;
    final CreditCardRepository creditCardRepository;

    final TrackingRepository trackingRepository;
    final ChatContentRepository chatContentRepository;
    private final PharmacistRepository pharmacistRepository;
    final Dialogflow dialogflow;

    public PatientController (SiteRepository siteRepository ,PatientRepository patientRepository , MedicineRepository medicineRepository,
                              PurchaseHasLoteRepository purchaseHasLoteRepository, PurchaseOrderRepository purchaseOrderRepository,
                              DoctorRepository doctorRepository,
                              LoteRepository loteRepository,
                              UserRepository userRepository, TrackingRepository trackingRepository ,
                              CarritoRepository carritoRepository, CreditCardRepository creditCardRepository,
                              NotificationsRepository notificationsRepository,
                              ChatRepository chatRepository,
                              ChatContentRepository chatContentRepository,
                              PharmacistRepository pharmacistRepository,
                              Dialogflow dialogflow) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.medicineRepository = medicineRepository;
        this.purchaseHasLoteRepository = purchaseHasLoteRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.doctorRepository = doctorRepository;
        this.loteRepository = loteRepository;
        this.userRepository = userRepository;
        this.trackingRepository = trackingRepository;
        this.carritoRepository = carritoRepository;
        this.creditCardRepository = creditCardRepository;
        this.notificationsRepository = notificationsRepository;
        this.chatRepository = chatRepository;
        this.chatContentRepository = chatContentRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.dialogflow = dialogflow;
    }
    private String rutaAbsoluta = "//SaintMedic//imagenes";

    @GetMapping("/sessionPatient")
    public String iniciarSesion( Model model, @RequestParam("idUser") String id){
        model.addAttribute("idUser",id);
        return "redirect:ElegirSede";
    }
    //EMPIEZA eleccion de sede
    @GetMapping("/ElegirSede")
    public String ElegirSede( Model model, HttpSession session){
        //Se listan las sedes
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));
        List<Site> listSite = siteRepository.findAll();
        model.addAttribute("listSite", listSite);
        return "elegirSede";
    }
    @GetMapping("/elegirSedePrimeraVez")
    public String llevarVistaPrincipal(@RequestParam("idSede") String idSede ,Model model,HttpSession session){
        model.addAttribute("idSede", (siteRepository.findById(Integer.parseInt(idSede)).get()).getIdSite());
        session.setAttribute("sede" , (siteRepository.findById(Integer.parseInt(idSede))).get() );
        List<MedicamentosPorSedeDTO> listMedicineBySede = medicineRepository.getMedicineBySite(Integer.parseInt(idSede));
        model.addAttribute("listaMedicinas" , listMedicineBySede) ;
        return "redirect:compras";
    }
    @GetMapping("/elegirSedeEnPagina")
    public String elegirSedeEnPagina(@RequestParam("idSede") String nuevoIdSede , Model model, HttpSession session ){
        model.addAttribute("idSede", (siteRepository.findById(Integer.parseInt(nuevoIdSede)).get()).getIdSite());
        model.addAttribute("listamedicamentosPatient",medicineRepository.listaMedicamentosPorSedePaciente(((Site) session.getAttribute("sede")).getName() ));
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/verPrincipalNuevo";
    }
    @GetMapping("/verSeleccionarSedePaciente")
    public String verSeleccionarSedePaciente(Model model, HttpSession session) {
        List<Site> listaSedes=  siteRepository.findAll();
        model.addAttribute("listaSede", listaSedes);
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/SeleccionarSedeNuevo";
    }

    //Termina elegir sede en la página , esto funciona completamente bien

    //Gestionan el cambio de las contraseñas la primera vez q se loguea un paciente
    @Scheduled(fixedRate = 60000) // Ejecuta la tarea cada minuto
    public void deleteExpiredUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<Patient> patients = patientRepository.findAll();

        for (Patient patient : patients) {
            if (patient.getExpirationDate().isBefore(now) && !patient.getChangePassword()) {
                // Eliminar el usuario si la contraseña ha expirado
                userRepository.delete(userRepository.findByEmail(patient.getEmail()));
                patientRepository.delete(patient);
                System.out.println("Eliminado usuario con email: " + patient.getEmail());
            }
        }
    }
    @GetMapping("/cambioObligatorio")
    public String cambioObligatorio( Model model){
        return "changePasswordFirstTime";
    }
    @PostMapping("/efectuarCambioContrasena")
    public String efectuarCambio(@RequestParam("confirmarContrasena") String password,Model model, RedirectAttributes attr, HttpSession httpSession){
        Patient patient = (Patient) httpSession.getAttribute("usuario");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        userRepository.actualizarPassword(encryptedPassword,patient.getEmail());
        patientRepository.updateChangePasswrod(patient.getIdPatient());
        return "redirect:ElegirSede";
    }
//Termina la gestion de cambio de contraseña la primera vez

    //Empieza Chat del paciente  (habra q implementar webservices para conseguirlo)
    @GetMapping("/verChatPaciente")
    public String verChatPaciente(Model model, HttpSession session){
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/chatNuevo";
    }
    @GetMapping( value = {"/verDatosPago",""})
    public String verDatosPago(@RequestParam("idPurchase") int idPurchase, Model model, HttpSession session){
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));
        List<MeciamentosPorCompraDTO> meciamentosPorCompra = medicineRepository.listaMedicamentosPorCompra(idPurchase);
        model.addAttribute("listaMedicamentosCompra",meciamentosPorCompra);
        model.addAttribute("purchaseDTO",purchaseOrderRepository.obtenerPurchasePorId(idPurchase));
        model.addAttribute("idPurchase",idPurchase);
        model.addAttribute("tarjetaElejida",creditCardRepository.encontrarCreditCardFavorita(patient.getIdPatient()));
        return "pacient/datos_pago";
    }


    //////////////////////////ORDENES DE COMPRA///////////////////////
    @GetMapping(value = {"/verGenerarOrdenCompra",""})
    public String verGenerarOrdenCompra(@SessionAttribute("idSede") String idSede ,Model model,RedirectAttributes redirectAttributes
    , HttpSession session){

        if (redirectAttributes != null) {
            String errorPhone = (String) redirectAttributes.getFlashAttributes().get("errorPhone");
            String errorDireccion = (String) redirectAttributes.getFlashAttributes().get("errorDireccion");
            String errorHora = (String) redirectAttributes.getFlashAttributes().get("errorHora");
            String errorDoctor = (String) redirectAttributes.getFlashAttributes().get("errorDoctor");
            if (errorPhone != null) {
                model.addAttribute("errorPhone", errorPhone);
            }
            if (errorDireccion != null) {
                model.addAttribute("errorDireccion", errorDireccion);
            }
            if (errorHora != null) {
                model.addAttribute("errorHora", errorHora);
            }

            if (errorDoctor != null) {
                model.addAttribute("errorDoctor", errorDoctor);
            }
        }
        Optional<Site> sede = siteRepository.findById(Integer.parseInt(idSede));
        model.addAttribute("listaDoctores",doctorRepository.listaDoctorPorSedePaciente(sede.get().getName()));

        Patient paciente = patientRepository.findById(((Patient)session.getAttribute("usuario")).getIdPatient()).get();
        model.addAttribute("direccion",paciente.getLocation());

        //Medicine medicine = medicineRepository.findById(1).get();
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("carro",carritoRepository.getMedicineListByPatient(patient.getIdPatient()));
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));
        return "pacient/generar_orden_compraNuevo";
    }
/*
    @PostMapping("/crearOrdenCompra")
    public String agregarOrdenCompra( @SessionAttribute("idSede") String idSede,
                                     @RequestParam("Hour") String HourStr,
                                     //@RequestParam("cantidad")int cantidad,
                                     //@RequestParam("idMedicine") int idMedicine,
                                     @RequestParam("phoneNumber") String phoneNumber,
                                     @RequestParam("direccion") String direccion,
                                     @RequestParam("idDoctor") int idDoctor,
                                     @RequestParam("receta") MultipartFile receta,
                                     Model model, RedirectAttributes attr, HttpSession session){
        Optional<Doctor> optionalDoctor = doctorRepository.findById(idDoctor);
        boolean fallo = false;
        if (optionalDoctor.isEmpty()) {
            fallo = true;
            attr.addFlashAttribute("errorDoctor", "El doctor no existe");
        }
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches() || phoneNumber.length() != 9) {
            attr.addFlashAttribute("errorPhone", "El teléfono debe ser de 9 dígitos");
            fallo=true;
        }
        if (direccion==null || direccion.trim().isEmpty()){
            attr.addFlashAttribute("errorDireccion", "Ingrese una dirección");
            fallo=true;
        }

        try {
            purchaseOrder.setDeliveryHour(HourStr);
        } catch (DateTimeParseException e) {
            attr.addFlashAttribute("errorHora", "Ingrese una hora válida");
            fallo=true;
        }

        List<Carrito> listaaa = carritoRepository.getMedicineListByPatient(((Patient)session.getAttribute("usuario")).getIdPatient());

        if (listaaa.isEmpty()){
            fallo=true;
            attr.addFlashAttribute("errorCarrito", "No ha colocado medicamentos en el carrito.");

        }


        if (fallo){
            return "redirect:verGenerarOrdenCompra";
        }

        if(!receta.isEmpty()){
            //ruta relativa para la imagen
            //Path directorioImagenMedicine= Paths.get("src//main//resources//static//assets_superAdmin//ImagenesMedicina");
            //String rutaAbsoluta =  directorioImagenMedicine.toFile().getAbsolutePath();
            String rutaAbsoluta = "//SaintMedic//imagenes";
            //imagen a flujo bytes y poder guardarlo en la base de datos para poder extraerlo después
            try {
                byte[] bytesImgMedicine = receta.getBytes();
                String fileOriginalName = receta.getOriginalFilename();

                long fileSize = receta.getSize();
                long maxFileSize  = 5*1024*1024;

                String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                if(fileSize>maxFileSize){
                    model.addAttribute("imageError","El tamaño de la imagen excede a 5MB");
                    return "redirect:verGenerarOrdenCompra";
                }
                if(
                        !fileExtension.equalsIgnoreCase(".jpg") &&
                                !fileExtension.equalsIgnoreCase(".png") &&
                                !fileExtension.equalsIgnoreCase(".jpeg")
                ){
                    model.addAttribute("imageError","El formato de la imagen debe ser jpg, jpeg o png");
                    return "redirect:verGenerarOrdenCompra";
                }

                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + receta.getOriginalFilename());
                Files.write(rutaCompleta,bytesImgMedicine);
                //se guarda la preescripcion
                purchaseOrder.setPrescription(receta.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /////

        purchaseOrder.setIdDoctor(doctorRepository.findById(idDoctor).get());
        purchaseOrder.setTipo("tarjeta");
        purchaseOrder.setPhoneNumber(phoneNumber);
        purchaseOrder.setDireccion(direccion);
        Patient patient = patientRepository.findById(((Patient)session.getAttribute("usuario")).getIdPatient()).get();
        purchaseOrder.setPatient(patient);
        purchaseOrder.setApproval("pendiente");
        Site sede = siteRepository.findById(Integer.parseInt(idSede)).get();
        purchaseOrder.setSite(sede.getName());
        purchaseOrder.setStatePaid("en espera");
        purchaseOrder.setTracking("en espera");
        purchaseOrder.setTipo("web");
        purchaseOrder.setDeliveryHour(HourStr);
        purchaseOrder.setReleaseDate(LocalDate.now());
        Tracking tracking = new Tracking();
        tracking.setSolicitudDate(LocalDateTime.now());
        tracking.setEnProcesoDate(LocalDateTime.now().plusMinutes(1));
        tracking.setEmpaquetadoDate(LocalDateTime.now().plusMinutes(2));
        tracking.setEnRutaDate(LocalDateTime.now().plusMinutes(3));
        tracking.setEntregadoDate(LocalDateTime.now().plusMinutes(4));
        trackingRepository.save(tracking);
        purchaseOrder.setIdtracking(tracking);
        purchaseOrderRepository.save(purchaseOrder);
        boolean validar=  true;
        for(Carrito c :  listaaa) {
            PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
            purchaseHasLote.setCantidadComprar(c.getCantidad());
            purchaseHasLote.setPurchaseOrder(purchaseOrder);
            PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
            purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
            List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(c.getIdMedicine().getIdMedicine(), c.getCantidad(), siteRepository.findById(Integer.parseInt("" + model.getAttribute("idSede"))).get().getName());
            if (listaLotesPosibles.isEmpty()) {
                validar = false;
                break;
            }
        }
        if(validar){
            for(Carrito c :  listaaa) {
                PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                purchaseHasLote.setCantidadComprar(c.getCantidad());
                purchaseHasLote.setPurchaseOrder(purchaseOrder);
                PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
                purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
                List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(c.getIdMedicine().getIdMedicine(), c.getCantidad(), siteRepository.findById(Integer.parseInt("" + model.getAttribute("idSede"))).get().getName());
                if (listaLotesPosibles.isEmpty()) {
                    return "redirect:verPrincipalPaciente";
                }
                purchaseHasLote.setLote(listaLotesPosibles.get(0));
                purchaseHasLotID.setIdLote(listaLotesPosibles.get(0).getIdLote());
                purchaseHasLote.setId(purchaseHasLotID);
                purchaseHasLoteRepository.save(purchaseHasLote);
            }

            //Vaciamos el carrito
            List<Carrito> list = carritoRepository.getMedicineListByPatient(((Patient) session.getAttribute("usuario")).getIdPatient());
            ArrayList<Integer> listaId = new ArrayList<>();
            for(Carrito c : list ){
                listaId.add (c.getId());
            }
            carritoRepository.deleteAllByIdInBatch(listaId);
            return "redirect:verTicket?idCompra="+purchaseOrder.getId();
        }else{
            model.addAttribute("ola",1);
            return "redirect:verGenerarOrdenCompra";
        }
    }
*/




    @GetMapping("/verTicket")
    public String verTicket(@RequestParam("idCompra") int idCompra , Model model){
        model.addAttribute("idCompra",idCompra);
        return "pacient/ticketOrdenCompra";
    }
    ///////////////////// TERMINA ORDEN DE COMPRA//////////////
    @GetMapping("/verHistorial")
    public String verHistorial( Model model, HttpSession session){
        List<PurchasePorPatientDTO> comprasPorPaciente = purchaseOrderRepository.obtenerComprarPorPaciente(((Patient)session.getAttribute("usuario")).getIdPatient());
        model.addAttribute("listaCompras",comprasPorPaciente);
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/historialNuevo";
    }
/////////////////////////////////////////////////////////
    @GetMapping("/verPerfilPaciente")
    public String verPerfilPaciente(  Model model, HttpSession session){
        Patient patient=  (Patient)session.getAttribute("usuario");
        model.addAttribute("paciente" , patient);
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/perfilNuevo";
    }
    @GetMapping("/verNoti")
    public String verNotifications(Model model, HttpSession session){
        Patient patient = ((Patient)session.getAttribute("usuario"));
        model.addAttribute("nombre", patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));
        User user = userRepository.findByEmail(patient.getEmail());

        model.addAttribute("listaNotificaciones",notificationsRepository.notificacionesUser(user.getId()));
        return "pacient/notificacionesPaciente";
    }
    @GetMapping("/verPrincipalPaciente")
    public String verPrincipalPaciente(HttpSession httpSesion , Model model , @SessionAttribute String idSede , HttpSession session ){
        System.out.println("Hola yo soy " + ( (Patient) httpSesion.getAttribute("usuario")).getName() );
        model.addAttribute("listamedicamentosPatient",medicineRepository.listaMedicamentosPorSedePaciente(((Site) session.getAttribute("sede")).getName() ));
        Patient patient = (Patient) httpSesion.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/verPrincipalNuevo";
    }
    //No funciona bien

    @GetMapping("/verTracking")
    public String verTrackingPaciente( Model model, HttpSession session){
        List<PurchasePorPatientDTO> tracking = purchaseOrderRepository.obtenerComprarPorPacienteTracking(((Patient)session.getAttribute("usuario")).getIdPatient());
        model.addAttribute("listaTracking",tracking);
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        return "pacient/trackingNuevo";
    }
    @GetMapping("/verTrackingSolitario")
    public String verTrackingPersonal(@RequestParam("idPurchase") int idPurchase , Model model, HttpSession session){

        Patient patient=  (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));
        Tracking tracking =  purchaseOrderRepository.findById(idPurchase).get().getIdtracking();
        model.addAttribute("listaMedicamentos", medicineRepository.listaMedicamentosPorCompra(idPurchase));

        model.addAttribute("idPurchase",idPurchase);
        model.addAttribute("Tracking",tracking);
        model.addAttribute("solicitudDate", tracking.getSolicitudDate().minusHours(5));
        model.addAttribute("enProcesoDate", tracking.getEnProcesoDate().minusHours(5));
        model.addAttribute("empaquetadoDate", tracking.getEmpaquetadoDate().minusHours(5));
        model.addAttribute("enRutaDate", tracking.getEnRutaDate().minusHours(5));
        model.addAttribute("entregadoDate", tracking.getEntregadoDate().minusHours(5));
        return "pacient/TrackingSolitario";
    }
    @PostMapping("/editarPerfilPaciente")
    public String editarDatosPaciente(@RequestParam("patientFile") MultipartFile imagen,@ModelAttribute("paciente") @Valid Patient patient, HttpSession session,BindingResult bindingResult, Model model, RedirectAttributes attr){
        //Actualizar datos cambiados
        System.out.println(patient.getIdPatient());
        System.out.println(patient.getLocation());
        System.out.println(patient.getInsurance());
        Patient patient1 = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient1.getName());
        model.addAttribute("apellido",patient1.getLastName());
        if (bindingResult.hasErrors()) {
            return "pacient/perfilNuevo";
        } else {
            if (imagen.isEmpty()) {
                attr.addFlashAttribute("msg", "Paciente actualizado correctamente");
                //patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), imagen.getOriginalFilename(), patient.getIdPatient());
                patientRepository.updatePatientDataSinFoto(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), patient.getIdPatient());
                session.setAttribute("usuario",patientRepository.findById(patient.getIdPatient()).get());

                return "redirect:verPerfilPaciente";
            }
            else {
                //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");

                try {
                    byte[] bytesImgPerfil = imagen.getBytes();
                    String fileOriginalName = imagen.getOriginalFilename();

                    long fileSize = imagen.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                    //foto unica
                    String fotoUnica = Calendar.getInstance().getTimeInMillis()+fileExtension;
                    if (fileSize > maxFileSize) {
                        model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                        return "pacient/perfilNuevo";
                    }
                    if (
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                                    !fileExtension.equalsIgnoreCase(".png") &&
                                    !fileExtension.equalsIgnoreCase(".jpeg")
                    ) {
                        model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                        return "pacient/perfilNuevo";
                    }

                    //Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + fotoUnica);

                    Files.write(rutaCompleta, bytesImgPerfil);
                    //patient.setPhoto(imagen.getOriginalFilename());
                    attr.addFlashAttribute("msg", "Paciente actualizado correctamente");
                    //patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), imagen.getOriginalFilename(), patient.getIdPatient());
                    patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), fotoUnica, patient.getIdPatient());
                    session.setAttribute("usuario",patientRepository.findById(patient.getIdPatient()).get());

                    return "redirect:verPerfilPaciente";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @GetMapping(value = {"/verInformacionPago",""})
    public String verInfoPago(  Model model, HttpSession session){
        Patient patient=  (Patient) session.getAttribute("usuario");
        model.addAttribute("paciente" , patient);
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));


        model.addAttribute("listarTarjetas",creditCardRepository.listaCreditCards(patient.getIdPatient()));

        return "pacient/informacionPago";
    }

/*
    private String formatCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            return cardNumber; // Devuelve el número tal cual si no tiene 16 dígitos
        }
        return cardNumber.substring(0, 4) + " **** **** " + cardNumber.substring(12);
    }
*/
    @PostMapping("/agregarTarjetaUsuario")
    public String agregarTarjeta(Model model , CreditCard creditCard,@RequestParam("fechaV") String fechaV ,HttpSession httpSession,RedirectAttributes attributes) {

        Patient patient = (Patient) httpSession.getAttribute("usuario");
        String[] parts = fechaV.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);

        CreditCard creditCardOptional = creditCardRepository.encontrarCreditCard(creditCard.getNumberCard());

        if (creditCardOptional!=null){
            int medDb = creditCardOptional.getExpireMonth();
            int yearDb = creditCardOptional.getExpireYear();
            if (creditCardOptional.getCvv().equals(creditCard.getCvv()) && medDb==month && yearDb==year && creditCardOptional.getIdPatient()==null){
                    creditCardOptional.setIdPatient(patient);
                    creditCardOptional.setPrefered(true);
                    creditCardRepository.save(creditCardOptional);
                    List<CreditCard> listaTarjetas= creditCardRepository.listaCreditCards(patient.getIdPatient());
                    for(CreditCard tarjeta:listaTarjetas){
                        if (!creditCard.getNumberCard().equals(tarjeta.getNumberCard())){
                            tarjeta.setPrefered(false);
                            creditCardRepository.save(tarjeta);
                        }
                    }
            }else{
                attributes.addFlashAttribute("msg","La tarjeta o los datos son incorrectos");
            }

        }else{
            attributes.addFlashAttribute("msg","La tarjeta o los datos son incorrectos");
        }

        return "redirect:verInformacionPago";
    }
    @GetMapping("/marcarTarjetaPreferida")
    public String marcarTarjetaFavorita(  Model model, HttpSession session,@RequestParam("idTarjeta") int idTarjeta){
        Patient patient=  (Patient)session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));
        List<CreditCard> listaTarjetas= creditCardRepository.listaCreditCards(patient.getIdPatient());
        creditCardRepository.preferirPorid(idTarjeta);
        for (CreditCard tarjeta: listaTarjetas){
            if(tarjeta.getIdCredit()!=idTarjeta){
                tarjeta.setPrefered(false);
                creditCardRepository.save(tarjeta);
            }
        }

        return "redirect:verInformacionPago";
    }
    @GetMapping("/eliminarTarjeta")
    public String eliminarTarjeta(  Model model, HttpSession session,@RequestParam("idTarjeta") int idTarjeta){
       CreditCard creditCard = creditCardRepository.findById(idTarjeta).get();
       creditCard.setIdPatient(null);
       creditCardRepository.save(creditCard);
        return "redirect:verInformacionPago";
    }
    @PostMapping("/pagarFinal")
    public String pagar(@RequestParam("idPurchase")int idPurchase,@RequestParam(value = "recurrent", required = false) String recurrent,@RequestParam("nuevaTarjetaNum") String tarjetaNueva,@RequestParam("cvv") String cvv,@RequestParam("fechaV") String fechaV,Model model, RedirectAttributes attr, HttpSession httpSession){
        Patient patient = (Patient) httpSession.getAttribute("usuario");
        String[] parts = fechaV.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        if (tarjetaNueva.isEmpty()){
            CreditCard creditCard = creditCardRepository.encontrarCreditCardFavorita(patient.getIdPatient());
            int medDb = creditCard.getExpireMonth();
            int yearDb = creditCard.getExpireYear();
            if (creditCard.getCvv().equals(cvv) && medDb==month && yearDb==year){

                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(idPurchase).get();
                if (recurrent.equals("true")){
                    purchaseOrder.setRecurrent(true);
                }

                purchaseOrderRepository.pagarOrdenCompra(idPurchase);
            }else{
                attr.addFlashAttribute("msg","La tarjeta o los datos son incorrectos");
                return "redirect:verDatosPago?idPurchase="+idPurchase;
            }
        }else{
            CreditCard creditCardOptional = creditCardRepository.encontrarCreditCard(tarjetaNueva);
            if (creditCardOptional!=null){
                int medDb = creditCardOptional.getExpireMonth();
                int yearDb = creditCardOptional.getExpireYear();

                if (creditCardOptional.getCvv().equals(cvv) && medDb==month && yearDb==year){
                    purchaseOrderRepository.pagarOrdenCompra(idPurchase);
                }else{
                    attr.addFlashAttribute("msg","La tarjeta o los datos son incorrectos");
                    return "redirect:verDatosPago?idPurchase="+idPurchase;
                }
            }else{
                attr.addFlashAttribute("msg","La tarjeta o los datos son incorrectos");
                return "redirect:verDatosPago?idPurchase="+idPurchase;
            }
        }
        return "redirect:verHistorial";
    }
    //Vista principal que requiere interacción con los webservices Uu
    @GetMapping(value = "/compras")
    public String verPosPatient(Model model , HttpSession session ){
        Patient patient = (Patient)session.getAttribute("usuario");
        //El queri para obtener la cantidad de medicamentos está bien
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        model.addAttribute("listaNotiUWU",notificationsRepository.notificacionesUserPeque(userRepository.findByEmail(patient.getEmail()).getId()));

        model.addAttribute("listamedicamentosPatient",medicineRepository.listaMedicamentosPorSedePaciente(((Site) session.getAttribute("sede")).getName() ));
        model.addAttribute("carrito" , carritoRepository.getMedicineListByPatient(patient.getIdPatient()));
        return "pacient/posPacienteNuevo";
    }
    //Filtro de la vista principal
    @PostMapping(value="/filtroMedicinas")
    public String verPostPatienteFiltrado (Model model , HttpSession session, @RequestParam("medicamento") String medicamento ){
        Patient patient = (Patient)session.getAttribute("usuario");
        List<MedicamentosPorSedeDTO> lista = medicineRepository.listaMedicamentosPorSedePaciente(((Site) session.getAttribute("sede")).getName() );
        ArrayList<MedicamentosPorSedeDTO> listaFiltrada = new ArrayList<MedicamentosPorSedeDTO>();
        if(!medicamento.isEmpty()) {
            for (MedicamentosPorSedeDTO m : lista) {
                if (m.getNombreMedicamento().contains(medicamento)) {
                    listaFiltrada.add(m);
                }
            }
        }else{
            for (MedicamentosPorSedeDTO m : lista) {
                    listaFiltrada.add(m);
            }
        }
        //El queri para obtener la cantidad de medicamentos está bien
        model.addAttribute("listamedicamentosPatient",listaFiltrada);
        model.addAttribute("carrito" , carritoRepository.getMedicineListByPatient(patient.getIdPatient()));
        return "pacient/posPacienteNuevo";
    }
    //WebServices de la vista de paciente para compras:

    //Clase extra necesaria
    public class Medicamento {
        private String nombre;
        private String precio;
        private String cantidad;
        private String idMedicina;
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getPrecio() {
            return precio;
        }

        public void setPrecio(String precio) {
            this.precio = precio;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }

        public String getIdMedicina() {
            return idMedicina;
        }

        public void setIdMedicina(String idMedicina) {
            this.idMedicina = idMedicina;
        }
    }
    @Scheduled(fixedRate = 30000) // Ejecuta la tarea cada 1/2 minuto
    public void changeTrackingPurchase() {
        LocalDateTime now = LocalDateTime.now();
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();

        for (PurchaseOrder purchaseOrder : purchaseOrders) {

            if(!purchaseOrder.getTipo().equals("Preorden")){
                String tracking = purchaseOrder.getTracking();

                Tracking trackingReal = purchaseOrder.getIdtracking();
                if(tracking !=null){
                    switch (tracking){
                        case ("Solicitado"):
                            if (trackingReal.getEnProcesoDate().isBefore(now)){
                                purchaseOrderRepository.actualizarTrackingPurchase("En Proceso",purchaseOrder.getId());
                            }
                            break;

                        case ("En Proceso"):
                            if (trackingReal.getEmpaquetadoDate().isBefore(now)){
                                purchaseOrderRepository.actualizarTrackingPurchase("Empaquetando",purchaseOrder.getId());
                            }
                            break;
                        case ("Empaquetando"):
                            if (trackingReal.getEnRutaDate().isBefore(now)){
                                purchaseOrderRepository.actualizarTrackingPurchase("En Ruta",purchaseOrder.getId());
                            }
                            break;

                        case ("En Ruta"):
                            if (trackingReal.getEntregadoDate().isBefore(now)){
                                purchaseOrderRepository.actualizarTrackingPurchase("Entregado",purchaseOrder.getId());
                                Notifications notifications = new Notifications();
                                notifications.setDate(LocalDateTime.now());
                                notifications.setContent("Tu orden número WB"+purchaseOrder.getId()+" ha llegado.");
                                String email = purchaseOrder.getPatient().getEmail();
                                notifications.setIdUsers(userRepository.findByEmail(email));
                                notificationsRepository.save(notifications);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

        }
    }
    @Scheduled(fixedRate = 600000)
    public void notificacionRecurrente(){
        for (PurchaseOrder purchaseOrder: purchaseOrderRepository.findAll()){

            if(purchaseOrder.getRecurrent() != null){
                if (purchaseOrder.getRecurrent()){
                    List<Lote> listaLotesCompra = loteRepository.listarLotesPorCompra(purchaseOrder.getId());

                    for (Lote lote: listaLotesCompra){

                        if (lote.getExpireDate().minusDays(10).isBefore(LocalDate.now()) || lote.getExpireDate().minusDays(5).isBefore(LocalDate.now())){
                            Notifications notification = new Notifications();
                        notification.setContent("Su orden de compra recurrente con el medicamento: "+lote.getMedicine().getName()+ " está por expirar; le recomedamos generar una nueva orden de compra." );
                            User user = userRepository.findByEmail(purchaseOrder.getPatient().getEmail());
                            notification.setIdUsers(user);
                            notification.setDate(LocalDateTime.now());
                            notificationsRepository.save(notification);
                        }
                    }
                    purchaseOrder.setRecurrent(false);
                    purchaseOrderRepository.save(purchaseOrder);
                }
            }

        }
    }
    @GetMapping(value="/vaciarCarrito")
    public Object deleteCarrito( HttpSession session){
        List<Carrito> list = carritoRepository.getMedicineListByPatient(((Patient) session.getAttribute("usuario")).getIdPatient());
        ArrayList<Integer> listaId = new ArrayList<>();
        for(Carrito c : list ){
            listaId.add (c.getId());
        }
        carritoRepository.deleteAllByIdInBatch(listaId);
        return "redirect:compras";
    }

    @GetMapping(value="/verChatBot")
    public String verChatBot(){
        return "pacient/chatBot";
    }
    //Con estos metodos cargamos los efectos y dialogos del chatBot
    //


    //SERVICIOSSSSS-----------------------------
    //En caso se equivoquen de metodo de consulta
    @ExceptionHandler({HttpMediaTypeException.class ,   ClassCastException.class , MethodArgumentTypeMismatchException.class})
    public Object gestionExcetion(HttpServletRequest request) {
        HashMap<String, Object> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("GET")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Te equivocaste en algún parámetro");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

    //Se van a harcodear todos los webServices de paciente
    @GetMapping(value="/requestChatBot")
    @ResponseBody
    @CrossOrigin
    public Object requestChatBot(@RequestParam(value="message", required = true) String message, HttpSession session){
        try {
            //Aqui consumimos el servicio de dialog flow
            LinkedHashMap<String, Object> linked = new LinkedHashMap<>();
            linked.put("status", "ok");

            //HARD
            linked.put("content", dialogflow.detectIntent(message,""+ ((Patient) session.getAttribute("usuario")).getEmail()));
            //linked.put("content", dialogflow.detectIntent(message,"alex@gmail.com"));

            //RECORDAR Q EN LA VISTA SE ESPERA ESTE RESULTADO
            //                createMessageReceiver(response.content.message , obtenerHoraActual);
            return ResponseEntity.ok(linked);
        }catch(Exception error) {
            error.printStackTrace();
            LinkedHashMap<String, Object> response =  new LinkedHashMap<>();
            response.put("status", "error");
            response.put("date", LocalDateTime.now());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @ResponseBody
    @GetMapping(value="/getCorreo")
    @CrossOrigin
    public Object getCorreo(HttpSession session){
        LinkedHashMap<String , Object> errorResponse = new LinkedHashMap<>();
        //Sale error por necesidad de tener asignada una sede hay q harcodear
        //HARD
        if(  ((Site) session.getAttribute("sede")) == null  ){
            errorResponse.put("status","error");
            errorResponse.put("reason", "You don't have a site");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        try {
            LinkedHashMap<String, Object> hasMap = new LinkedHashMap<>();
            //HARD
            //String sede = "Pando 1";
            String sede = ((Site) session.getAttribute("sede")).getName();
            List<Pharmacist> listaFarmacista = pharmacistRepository.findAll();
            for (Pharmacist p : listaFarmacista) {
                if (p.getSite().equals(sede) && !p.getState().equals("eliminado")) {
                    hasMap.put("correo", p.getEmail());
                    break;
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(hasMap);
        }catch(Exception error){
            error.printStackTrace();
            LinkedHashMap<String , Object> badRequest=  new LinkedHashMap<>();
            badRequest.put("status","error");
            badRequest.put("date", ""+ LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(badRequest);
        }
    }
    @GetMapping(value="/addCarritoVenta")
    @CrossOrigin
    public Object validarCarrito(@RequestParam("idProducto") String  idProducto , HttpSession session){
        LinkedHashMap<String , Object> responseGeneral = new LinkedHashMap<>();
        try {
            if(idProducto==null){
                responseGeneral.put("status", "error");
                responseGeneral.put("message", "idProductor is a required param");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseGeneral);
            }
            int idProduct;
            try {
                 idProduct = Integer.parseInt(idProducto);
            }catch(NumberFormatException number){
                responseGeneral.put("status", "error");
                responseGeneral.put("message", "idProducto debe ser un numero");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseGeneral);
            }
            if(!medicineRepository.findById(idProduct).isPresent()){
                responseGeneral.put("status", "error");
                responseGeneral.put("message", "el producto no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseGeneral);
            }
                //HARD
                Patient p = (Patient) session.getAttribute("usuario");
                //Patient p =  patientRepository.findByEmail("alex@gmail.com").get();
                //Verficamos que no este repetido
                List<Carrito> lista =  carritoRepository.getMedicineListByPatient(p.getIdPatient());
                boolean existeMecidina =false;
                for(Carrito c : lista){
                    if(c.getIdMedicine().getIdMedicine() == idProduct){
                        existeMecidina = true;
                    }
                }
                if(!existeMecidina){
                    Carrito car  = new Carrito();
                    car.setCantidad(1);
                    car.setIdPatient(p);
                    car.setIdMedicine(medicineRepository.findById(Integer.parseInt(idProducto)).get());
                    carritoRepository.save(car);
                    HashMap<String, Object> okey = new HashMap<>();
                    okey.put("Succes", "Todo good");
                    return ResponseEntity.status(HttpStatus.OK).body(okey);
                }else{
                    responseGeneral.put("status", "error");
                    responseGeneral.put("message", "Este producto ya esta en la lista");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseGeneral);
                }
        } catch (Exception err) {
            err.printStackTrace();
            HashMap<String, Object> er = new HashMap<>();
            er.put("status", "error");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }
    @GetMapping(value="/getAllCarrito")
    @CrossOrigin
    public Object getAllCarrito( HttpSession session){
        LinkedHashMap<String , Object> responseGeneral = new LinkedHashMap<>();
        try {
            Patient p =  (Patient)  session.getAttribute("usuario");
            //Patient p = patientRepository.findByEmail("alex@gmail.com").get();
            return ResponseEntity.status(HttpStatus.OK).body(carritoRepository.getMedicineListByPatient(p.getIdPatient()));
        } catch (Exception err) {
            err.printStackTrace();
            responseGeneral.put("error", "Error");
            responseGeneral.put("date", "" + LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseGeneral);
        }
    }
    @GetMapping(value="/updateCantidad")
    @ResponseBody
    @CrossOrigin
    public Object updateCantidadA(@RequestParam("idProduct") String idProduct, @RequestParam("newCantidad") String newCantidad,  HttpSession session){
        LinkedHashMap<String , Object> responseGeneral =  new LinkedHashMap<>();
        try {
            //HARD
            Patient p =  (Patient)  session.getAttribute("usuario");
            //Patient p =  patientRepository.findByEmail("alex@gmail.com").get();
            List<Carrito> list = carritoRepository.getMedicineListByPatient(p.getIdPatient());
            Carrito aux= new Carrito();
            for(Carrito c:  list){
                if(c.getIdMedicine().getIdMedicine()== Integer.parseInt(idProduct)){
                    aux= c;
                }
            }
            aux.setCantidad(Integer.parseInt(newCantidad));
            carritoRepository.save(aux);
            return ResponseEntity.status(HttpStatus.OK).body(carritoRepository.getMedicineListByPatient(p.getIdPatient()));
        } catch (Exception err) {
            responseGeneral.put("status", "error");
            responseGeneral.put("message", "Error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseGeneral);
        }
    }

    @GetMapping(value="/deleteProductCarritoVenta")
    @CrossOrigin
    public Object deleteCarritoProduct(@RequestParam("idProducto") String  idProducto , HttpSession session){
        LinkedHashMap<String , Object> generalResponse = new LinkedHashMap<>();
        if(idProducto==null){
            generalResponse.put("status", "error");
            generalResponse.put("message", "idProducto is a required param");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
        }
        int idProduct;
        try{
            idProduct = Integer.parseInt(idProducto);
        }catch(NumberFormatException number){
            generalResponse.put("status", "error");
            generalResponse.put("message", "idProducto must be a integer number");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
        }
        try {
            if(!medicineRepository.findById(idProduct).isPresent()){
                generalResponse.put("status", "error");
                generalResponse.put("message", "idProducto is not assigned to any medicine");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            Medicine m =  medicineRepository.findById(Integer.parseInt(idProducto)).get();
                //HARD
                Patient p = (Patient) session.getAttribute("usuario");
                //Patient p = patientRepository.findByEmail("deanw202315@gmail.com").get();
                List<Carrito> listaCart = carritoRepository.getMedicineListByPatient(p.getIdPatient());
                Carrito cat =  new Carrito();
                for(Carrito c : listaCart){
                    if(c.getIdMedicine().getIdMedicine() == m.getIdMedicine()){
                        cat =  c;
                    }
                }
                carritoRepository.deleteById(cat.getId());
                HashMap<String, Object> okey = new HashMap<>();
                okey.put("Succes", "Todo good");
                return ResponseEntity.ok(okey);

        } catch (Exception err) {
            err.printStackTrace();
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "Error interno");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }
//    @RequestMapping("/verSingleProduct")
//    @ResponseBody
//    @CrossOrigin
//    public ArrayList<String>  verSingleProductPaciente(@RequestParam String idMedicine){
//        Optional<Medicine> medicine  = medicineRepository.findById(Integer.parseInt(idMedicine));
//        ArrayList<String> response =  new ArrayList<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = null;
//        // Convertir el objeto a JSON
//        try {
//            json = objectMapper.writeValueAsString(medicine.get());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(json);
//        response.add(json);
//        return response;
//    }

    @RequestMapping("/verSingleProduct")
    @ResponseBody
    @CrossOrigin
    public Object  verSingleProductPaciente(@RequestParam String idMedicine){
        LinkedHashMap<String , Object> linked  =  new LinkedHashMap<>();
        if(idMedicine==null){
            linked.put("status" , "error");
            linked.put("message", "Debes poner un parametro de idMedicine");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(linked);
        }
        int idMedicineInt ;
        try{
            idMedicineInt= Integer.parseInt(idMedicine);
        }catch(Exception error){
            linked.put("status", "error");
            linked.put("messafe","idMedicine debe de ser un numero entero");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(linked);
        }
        if(! medicineRepository.findById(idMedicineInt).isPresent()){
            linked.put("status", "error");
            linked.put("messafe","No existe la medicina ingresada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(linked);
        }
        return ResponseEntity.status(HttpStatus.OK).body(medicineRepository.findById(idMedicineInt).get());
    }

    @RequestMapping("/verDetalleCompra")
    @ResponseBody
    @CrossOrigin
    public ArrayList<String> verDetalleCompra( @RequestParam("idPurchase") int idPurchase , Model model , HttpSession session){
        List<MeciamentosPorCompraDTO> meciamentosPorCompra = medicineRepository.listaMedicamentosPorCompra(idPurchase);
        model.addAttribute("listaMedicamentosPorCompra",meciamentosPorCompra);
        ArrayList<String> response =  new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        for(MeciamentosPorCompraDTO m : meciamentosPorCompra){
            // Convertir el objeto a JSON
            try {
                json = objectMapper.writeValueAsString(m);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.println(json);
            response.add(json);
        }
        return response;
    }

    //Envia el contenido del chat con las
    @GetMapping("/getChat")
    @ResponseBody
    @CrossOrigin
    public Object getChat( HttpSession session){
        LinkedHashMap<String , Object > errorResponse= new LinkedHashMap<>();
        try {
            //posibles errores
            if(  ((Site) session.getAttribute("sede")) == null  ){
                errorResponse.put("status","error");
                errorResponse.put("reason", "You don't have a site");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
            if(  ((Patient) session.getAttribute("usuario")) == null  ){
                errorResponse.put("status","error");
                errorResponse.put("reason", "You are not a user");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
            //Borrarlos para tomar la captura

            //Hay q harcodear la sede y el usuario
            List<Chat> chats = chatRepository.findAll();
            //HARD
            Patient patient = (Patient) session.getAttribute("usuario");
            //Patient patient =  patientRepository.findByEmail("alex@gmail.com").get();
            Chat chat = null;
            for (Chat c : chats) {
                if (c.getIdPacient().getEmail().equals(patient.getEmail())
                         &&
                         c.getIdFarmacist().getSite().equals(((Site) session.getAttribute("sede")).getName())
                ){
                    chat = c;
                }
            }
            List<Chatcontent> chatContents = chatContentRepository.findAll();
            //
            ArrayList<Chatcontent> listaFiltrada = new ArrayList<>();
            for (Chatcontent cs : chatContents) {
                if (cs.getIdChat() == chat) {
                    listaFiltrada.add(cs);
                }
            }
            LinkedHashMap<String, Object> hasMap = new LinkedHashMap<>();
            hasMap.put("Content", listaFiltrada);
            return ResponseEntity.status(HttpStatus.OK).body(hasMap);
        }catch (Exception err){
            err.printStackTrace();
            LinkedHashMap<String,Object > like = new LinkedHashMap<>();
            like.put("state", "Error");
            like.put("date" , "" + LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(like);
        }
    }


    @GetMapping(value="/verificarCorreoPharmacist")
    @ResponseBody
    @CrossOrigin
    public Object verificarCorreoPharmacist(HttpSession session ,@RequestParam(value="email" ,required = false) String email){
        //Verificamos q el correo existe
        try {
            Pharmacist p = pharmacistRepository.findByEmail(email);

            HashMap<String, Object> has = new HashMap<>();
            //HARD
            if (p.getSite().equals(((Site) session.getAttribute("sede")).getName())) {
                has.put("content", "si");
            } else {
                has.put("content", "no");
            }
            return ResponseEntity.ok(has);
        }catch(Exception err){
            return ResponseEntity.internalServerError();
        }
    }
    @PostMapping("/crearOrdenCompra")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Map<String, Object>> agregarOrdenCompra(
            @SessionAttribute("idSede") String idSede,
            @RequestParam("Hour") String HourStr,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("direccion") String direccion,
            @RequestParam("idDoctor") int idDoctor,
            @RequestParam("receta") MultipartFile receta,
            Model model, RedirectAttributes attr, HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Optional<Doctor> optionalDoctor = doctorRepository.findById(idDoctor);
        boolean fallo = false;

        if (optionalDoctor.isEmpty()) {
            fallo = true;
            response.put("errorDoctor", "El doctor no existe");
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(phoneNumber);

        if (!matcher.matches() || phoneNumber.length() != 9) {
            response.put("errorPhone", "El teléfono debe ser de 9 dígitos");
            fallo = true;
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            response.put("errorDireccion", "Ingrese una dirección");
            fallo = true;
        }

        try {
            purchaseOrder.setDeliveryHour(HourStr);
        } catch (DateTimeParseException e) {
            response.put("errorHora", "Ingrese una hora válida");
            fallo = true;
        }

        List<Carrito> listaaa = carritoRepository.getMedicineListByPatient(((Patient) session.getAttribute("usuario")).getIdPatient());

        if (listaaa.isEmpty()) {
            fallo = true;
            response.put("errorCarrito", "No ha colocado medicamentos en el carrito.");
        }

        if (fallo) {
            return ResponseEntity.badRequest().body(response);
        }

        if (!receta.isEmpty()) {

            try {
                byte[] bytesImgMedicine = receta.getBytes();
                String fileOriginalName = receta.getOriginalFilename();

                long fileSize = receta.getSize();
                long maxFileSize = 5 * 1024 * 1024;

                String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                //nombre de foto de receta único
                String photoRecetaUnique = Calendar.getInstance().getTimeInMillis() + fileExtension;
                if (fileSize > maxFileSize) {
                    response.put("imageError", "El tamaño de la imagen excede a 5MB");
                    return ResponseEntity.badRequest().body(response);
                }
                if (!fileExtension.equalsIgnoreCase(".jpg") && !fileExtension.equalsIgnoreCase(".png") && !fileExtension.equalsIgnoreCase(".jpeg")) {
                    response.put("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                    return ResponseEntity.badRequest().body(response);
                }

                //Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + receta.getOriginalFilename());
                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + photoRecetaUnique);
                Files.write(rutaCompleta, bytesImgMedicine);
                //purchaseOrder.setPrescription(receta.getOriginalFilename());
                purchaseOrder.setPrescription(photoRecetaUnique);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        purchaseOrder.setIdDoctor(doctorRepository.findById(idDoctor).get());
        purchaseOrder.setTipo("tarjeta");
        purchaseOrder.setPhoneNumber(phoneNumber);
        purchaseOrder.setDireccion(direccion);
        Patient patient = patientRepository.findById(((Patient) session.getAttribute("usuario")).getIdPatient()).get();
        purchaseOrder.setPatient(patient);
        purchaseOrder.setApproval("pendiente");
        Site sede = siteRepository.findById(Integer.parseInt(idSede)).get();
        purchaseOrder.setSite(sede.getName());
        purchaseOrder.setStatePaid("en espera");
        purchaseOrder.setTracking("en espera");
        purchaseOrder.setTipo("web");
        purchaseOrder.setRecurrent(false);
        purchaseOrder.setDeliveryHour(HourStr);
        purchaseOrder.setReleaseDate(LocalDate.now());

        Tracking tracking = new Tracking();
        tracking.setSolicitudDate(LocalDateTime.now());
        tracking.setEnProcesoDate(LocalDateTime.now().plusMinutes(1));
        tracking.setEmpaquetadoDate(LocalDateTime.now().plusMinutes(2));
        tracking.setEnRutaDate(LocalDateTime.now().plusMinutes(3));
        tracking.setEntregadoDate(LocalDateTime.now().plusMinutes(4));
        trackingRepository.save(tracking);
        purchaseOrder.setIdtracking(tracking);
        purchaseOrderRepository.save(purchaseOrder);

        boolean validar = true;
        for (Carrito c : listaaa) {
            PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
            purchaseHasLote.setCantidadComprar(c.getCantidad());
            purchaseHasLote.setPurchaseOrder(purchaseOrder);
            PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
            purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
            List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(c.getIdMedicine().getIdMedicine(), c.getCantidad(), siteRepository.findById(Integer.parseInt(idSede)).get().getName());
            if (listaLotesPosibles.isEmpty()) {
                validar = false;
                break;
            }
        }

        if (validar) {
            for (Carrito c : listaaa) {
                PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                purchaseHasLote.setCantidadComprar(c.getCantidad());
                purchaseHasLote.setPurchaseOrder(purchaseOrder);
                PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
                purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
                List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(c.getIdMedicine().getIdMedicine(), c.getCantidad(), siteRepository.findById(Integer.parseInt(idSede)).get().getName());
                if (listaLotesPosibles.isEmpty()) {
                    return ResponseEntity.badRequest().body(response);
                }
                purchaseHasLote.setLote(listaLotesPosibles.get(0));
                purchaseHasLotID.setIdLote(listaLotesPosibles.get(0).getIdLote());
                purchaseHasLote.setId(purchaseHasLotID);
                purchaseHasLoteRepository.save(purchaseHasLote);
            }

            // Vaciamos el carrito
            List<Carrito> list = carritoRepository.getMedicineListByPatient(((Patient) session.getAttribute("usuario")).getIdPatient());
            ArrayList<Integer> listaId = new ArrayList<>();
            for (Carrito c : list) {
                listaId.add(c.getId());
            }
            carritoRepository.deleteAllByIdInBatch(listaId);

            response.put("success", true);
            response.put("idCompra", purchaseOrder.getId());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
    }


}
