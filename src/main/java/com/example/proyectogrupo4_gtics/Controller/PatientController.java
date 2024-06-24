package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.MeciamentosPorCompraDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchasePorPatientDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorSedeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    final PatientRepository patientRepository;
    final MedicineRepository medicineRepository;
    final UserRepository userRepository;
    final PurchaseHasLoteRepository purchaseHasLoteRepository;
    final PurchaseOrderRepository purchaseOrderRepository;
    private final DoctorRepository doctorRepository;
    private final LoteRepository loteRepository;
    private final CarritoRepository carritoRepository;
    final TrackingRepository trackingRepository;
    public PatientController (SiteRepository siteRepository ,PatientRepository patientRepository , MedicineRepository medicineRepository,
                              PurchaseHasLoteRepository purchaseHasLoteRepository, PurchaseOrderRepository purchaseOrderRepository,
                              DoctorRepository doctorRepository,
                              LoteRepository loteRepository,
                              UserRepository userRepository, TrackingRepository trackingRepository ,
                              CarritoRepository carritoRepository) {
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
    }

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
        return "pacient/verPrincipalNuevo";
    }
    @GetMapping("/verSeleccionarSedePaciente")
    public String verSeleccionarSedePaciente(Model model, HttpSession session) {
        List<Site> listaSedes=  siteRepository.findAll();
        model.addAttribute("listaSede", listaSedes);
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
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
        return "pacient/chatNuevo";
    }
    @GetMapping("/verDatosPago")
    public String verDatosPago(@RequestParam("idPurchase") int idPurchase, Model model, HttpSession session){
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
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

        Medicine medicine = medicineRepository.findById(1).get();
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("carro",carritoRepository.getMedicineListByPatient(patient.getIdPatient()));
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        return "pacient/generar_orden_compraNuevo";
    }

    @PostMapping("/crearOrdenCompra")
    public String agregarOrdenCompra( @SessionAttribute("idSede") String idSede,
                                     @RequestParam("Hour") String HourStr,
                                     //@RequestParam("cantidad")int cantidad,
                                     //@RequestParam("idMedicine") int idMedicine,
                                     @RequestParam("phoneNumber") String phoneNumber,
                                     @RequestParam("direccion") String direccion,
                                     @RequestParam("idDoctor") int idDoctor,
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
            LocalTime deliveryHour = LocalTime.parse(HourStr);
            purchaseOrder.setDeliveryHour(deliveryHour);
        } catch (DateTimeParseException e) {
            attr.addFlashAttribute("errorHora", "Ingrese una hora válida");
            fallo=true;
        }

        if (fallo){
            return "redirect:verGenerarOrdenCompra";
        }

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
        LocalTime deliveryHour = LocalTime.parse(HourStr);
        purchaseOrder.setDeliveryHour(deliveryHour);
        purchaseOrder.setReleaseDate(LocalDate.now());
        Tracking tracking = new Tracking();
        tracking.setSolicitudDate(LocalDateTime.now());
        tracking.setEnProcesoDate(LocalDateTime.now().plusMinutes(10));
        tracking.setEmpaquetadoDate(LocalDateTime.now().plusMinutes(20));
        tracking.setEnRutaDate(LocalDateTime.now().plusMinutes(30));
        tracking.setEntregadoDate(LocalDateTime.now().plusMinutes(40));
        trackingRepository.save(tracking);
        purchaseOrder.setIdtracking(tracking);
        purchaseOrderRepository.save(purchaseOrder);

        List<Carrito> listaaa = carritoRepository.getMedicineListByPatient(((Patient)session.getAttribute("usuario")).getIdPatient());
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

    }

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
        return "pacient/historialNuevo";
    }

/////////////////////////////////////////////////////////

    @GetMapping("/verPerfilPaciente")
    public String verPerfilPaciente(  Model model, HttpSession session){
        Optional<Patient>patient=  patientRepository.findById(((Patient)session.getAttribute("usuario")).getIdPatient());
        model.addAttribute("paciente" , patient.get());
        model.addAttribute("nombre",patient.get().getName());
        model.addAttribute("apellido",patient.get().getLastName());
        return "pacient/perfilNuevo";
    }
    @GetMapping("/verPrincipalPaciente")
    public String verPrincipalPaciente(HttpSession httpSesion , Model model , @SessionAttribute String idSede , HttpSession session ){
        System.out.println("Hola yo soy " + ( (Patient) httpSesion.getAttribute("usuario")).getName() );
        model.addAttribute("listamedicamentosPatient",medicineRepository.listaMedicamentosPorSedePaciente(((Site) session.getAttribute("sede")).getName() ));
        Patient patient = (Patient) httpSesion.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        return "pacient/verPrincipalNuevo";
    }
    //No funciona bien


    @RequestMapping("/verDetalleCompra")
    @ResponseBody
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
    //Falta corregir
    @RequestMapping("/verSingleProduct")
    @ResponseBody
    public ArrayList<String>  verSingleProductPaciente(@RequestParam String idMedicine , Model model){
        Optional<Medicine> medicine  = medicineRepository.findById(Integer.parseInt(idMedicine));
        ArrayList<String> response =  new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
            // Convertir el objeto a JSON
            try {
                json = objectMapper.writeValueAsString(medicine.get());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.println(json);
            response.add(json);
            return response;
    }
    @GetMapping("/verTracking")
    public String verTrackingPaciente(@SessionAttribute("idUser") String idUser , Model model, HttpSession session){
        List<PurchasePorPatientDTO> tracking = purchaseOrderRepository.obtenerComprarPorPacienteTracking(((Patient)session.getAttribute("usuario")).getIdPatient());
        model.addAttribute("listaTracking",tracking);
        Patient patient = (Patient) session.getAttribute("usuario");
        model.addAttribute("nombre",patient.getName());
        model.addAttribute("apellido",patient.getLastName());
        return "pacient/trackingNuevo";
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
                model.addAttribute("imageError", "Debe agregar una imagen");
                return "pacient/perfilNuevo";
            }
            else {
                //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");
                //String rutaAbsoluta = directorioImagenPerfil.toFile().getAbsolutePath();
                //NUBE
                //String rutaAbsoluta = "//SaintMedic//imagenes";
                //Local
                String rutaAbsoluta = "C://SaintMedic//imagenes";
                try {
                    byte[] bytesImgPerfil = imagen.getBytes();
                    String fileOriginalName = imagen.getOriginalFilename();

                    long fileSize = imagen.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
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

                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImgPerfil);
                    //patient.setPhoto(imagen.getOriginalFilename());
                    attr.addFlashAttribute("msg", "Paciente actualizado correctamente");
                    patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), imagen.getOriginalFilename(), patient.getIdPatient());
                    return "redirect:verPerfilPaciente";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            /*
            attr.addFlashAttribute("msg", "Paciente actualizado correctamente");
            patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), imagen.getOriginalFilename(), patient.getIdPatient());
            return "redirect:verPerfilPaciente";
             */
        }
    }




    //Vista principal que requiere interacción con los webservices Uu
    @GetMapping(value = "/compras")
    public String verPosPatient(Model model , HttpSession session ){
        Patient patient = (Patient)session.getAttribute("usuario");
        //El queri para obtener la cantidad de medicamentos está bien
        model.addAttribute("listamedicamentosPatient",medicineRepository.listaMedicamentosPorSedePaciente(((Site) session.getAttribute("sede")).getName() ));
        model.addAttribute("carrito" , carritoRepository.getMedicineListByPatient(patient.getIdPatient()));
        return "pacient/posPacienteNuevo";
    }

    //WebServices de la vista de paciente para compras:
    @GetMapping(value="/updateCantidad")
    @ResponseBody
    public Object updateCantidadA(@RequestParam("idProduct") String idProduct, @RequestParam("newCantidad") String newCantidad,  HttpSession session){
        try {
            Patient p =  (Patient)  session.getAttribute("usuario");
            List<Carrito> list = carritoRepository.getMedicineListByPatient(p.getIdPatient());
            Carrito aux= new Carrito();
            for(Carrito c:  list){
                if(c.getIdMedicine().getIdMedicine()== Integer.parseInt(idProduct)){
                    aux= c;
                }
            }
            aux.setCantidad(Integer.parseInt(newCantidad));
            carritoRepository.save(aux);
            return ResponseEntity.ok(carritoRepository.getMedicineListByPatient(p.getIdPatient()));
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
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
    @GetMapping(value="/deleteProductCarritoVenta")
    public Object deleteCarritoProduct(@RequestParam("idProducto") String  idProducto , HttpSession session){
        try {
            int idProduct = Integer.parseInt(idProducto);
            Medicine m =  medicineRepository.findById(Integer.parseInt(idProducto)).get();
            if (idProducto != null) {
                Patient p = (Patient) session.getAttribute("usuario");
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
            } else {
                System.out.println("Hola 2");
                HashMap<String, Object> er = new HashMap<>();
                er.put("error", "Debes ingresar el nombre del recurso");
                er.put("date", "" + LocalDateTime.now());
                return ResponseEntity.badRequest().body(er);
            }
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }
    @GetMapping(value="/getAllCarrito")
    public Object getAllCarrito( HttpSession session){
        try {
            Patient p =  (Patient)  session.getAttribute("usuario");
            return ResponseEntity.ok(carritoRepository.getMedicineListByPatient(p.getIdPatient()));
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }
    @GetMapping(value="/addCarritoVenta")
    public Object validarCarrito(@RequestParam("idProducto") String  idProducto , HttpSession session){
        try {
            int idProduct = Integer.parseInt(idProducto);
            if (idProducto != null) {
                Patient p = (Patient) session.getAttribute("usuario");
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
                    return ResponseEntity.ok(okey);
                }else{
                    System.out.println("pepepe");
                    HashMap<String, Object> er = new HashMap<>();
                    er.put("error", "se repite el medicamento en la lista");
                    return ResponseEntity.badRequest().body(er);
                }

            } else {
                System.out.println("Hola 2");
                HashMap<String, Object> er = new HashMap<>();
                er.put("error", "Debes ingresar el nombre del recurso");
                er.put("date", "" + LocalDateTime.now());
                return ResponseEntity.badRequest().body(er);
            }
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }
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
}
