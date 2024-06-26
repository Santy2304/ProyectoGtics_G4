package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import java.util.*;

@SessionAttributes({"idUser","sede" , "idPatient" , "idDoctor"})
@Controller
@RequestMapping("/pharmacist")
public class PharmacistController {
    final MedicineRepository medicineRepository;
    final LoteRepository loteRepository;
    final PharmacistRepository pharmacistRepository;
    private final DoctorRepository doctorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PatientRepository patientRepository;
    final PurchaseHasLoteRepository purchaseHasLoteRepository;
    final CarritoRepository carritoRepository ;
    final CarritoVentaRepository carritoVentaRepository ;

    final NotificationsRepository notificationsRepository;

    final UserRepository userRepository;

    final TrackingRepository trackingRepository;



    public PharmacistController(CarritoVentaRepository carritoVentaRepository,CarritoRepository carritoRepository ,
                                PurchaseHasLoteRepository purchaseHasLoteRepository ,
                                MedicineRepository medicineRepository, PatientRepository patientRepository ,
                                LoteRepository loteRepository, PharmacistRepository pharmacistRepository,
                                DoctorRepository doctorRepository,PurchaseOrderRepository purchaseOrderRepository,
                                UserRepository userRepository, NotificationsRepository notificationsRepository,
                                TrackingRepository trackingRepository) {
        this.medicineRepository = medicineRepository;
        this.loteRepository = loteRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.doctorRepository = doctorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.patientRepository = patientRepository;
        this.purchaseHasLoteRepository = purchaseHasLoteRepository;
        this.userRepository = userRepository;
        this.carritoRepository = carritoRepository;
        this.carritoVentaRepository = carritoVentaRepository;
        this.notificationsRepository = notificationsRepository;
        this.trackingRepository = trackingRepository;
    }

    @GetMapping("/cambioObligatorio")
    public String cambioObligatorio(){

        return "pharmacist/changePasswordFirstTime";
    }
    @PostMapping("/efectuarCambioContrasena")
    public String efectuarCambio(@RequestParam("confirmarContrasena") String password,Model model, RedirectAttributes attr, HttpSession httpSession){
        Pharmacist pharmacist = (Pharmacist) httpSession.getAttribute("usuario");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        userRepository.actualizarPassword(encryptedPassword,pharmacist.getEmail());
        pharmacistRepository.updateChangePasswrod(pharmacist.getIdFarmacista());
        return "redirect:verMedicinelist";
    }
    @GetMapping("/sessionPharmacist")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idPharmacist){
        model.addAttribute("idUser",idPharmacist);
        model.addAttribute("idPatient","");
        model.addAttribute("idDoctor","");
        return "redirect:verMedicinelist";
    }

    @GetMapping("/cerrarSesion")
    public String eliminarStributo(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return "redirect:/inicioSesion";
    }

    @GetMapping("/verChatFarmacista")
    public String verChatPharmacist(Model model, HttpSession session
    ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/chat";
    }

    @GetMapping("/verEditarProducto")
    public String verEditProduct(Model model, HttpSession session){

        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/editproduct";
    }


    @GetMapping("/verNotificationsFarmacista")
    public String verNotifications(Model model, HttpSession session
    ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/notifications";
    }

    @GetMapping("/posFarmacista")
    public String verPosPharmacist(Model model , HttpSession session ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        model.addAttribute("listamedicamentosfarm",medicineRepository.listaMedicamentosPorSedeFarmacista(idPharmacist));
        model.addAttribute("listaDoctores", doctorRepository.findAll());
        try {
            if (!(model.getAttribute("idPatient")).equals("")) {
                model.addAttribute("fullNamePatient", (patientRepository.findById(Integer.parseInt("" + model.getAttribute("idPatient")))).get().getName() + " " + (patientRepository.findById(Integer.parseInt("" + model.getAttribute("idPatient")))).get().getLastName());
                model.addAttribute("fullNameDoctor", (doctorRepository.findById(Integer.parseInt("" + model.getAttribute("idDoctor")))).get().getName() + " " + (doctorRepository.findById(Integer.parseInt("" + model.getAttribute("idDoctor")))).get().getLastName());
            } else {
                model.addAttribute("fullNamePatient", null);
                model.addAttribute("fullNameDoctor", null);
            }
        }catch (Exception err){
            model.addAttribute("fullNamePatient", null);
            model.addAttribute("fullNameDoctor", null);
        }
        int idPhar = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        model.addAttribute("listaComprar" , carritoVentaRepository.getMedicineListByPharmacist(idPhar));
        return "pharmacist/pos";
    }

    @RequestMapping("/venderMedicamentos")
    @ResponseBody
    public Map<String , String > venderMedicamentos(Model model ){

        Map<String , String>  response =  new HashMap<>();
        return response;
    }

    /*
    @PostMapping("/generarOrdenCompra")
    public String generarOrdenCompraFarmacista(Model model, @SessionAttributes("idUser") String idUser, @SessionAttributes("sede") String sede,
                                               @RequestParam("nombre") String nombre,
                                               @RequestParam("apellido") String apellido,
                                               @RequestParam("idDoctor") int idDoctor,
                                               @RequestParam("dni") String dni,
                                               @RequestParam("tipoPago") String tipoPago){

        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));

    }

     */


    @GetMapping("/solicitudesFarmacista")
    public String verSolicitudes(Model model, HttpSession session
    ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        model.addAttribute("listaSolicitudes",purchaseOrderRepository.listaVentasSolicitudesWEBPorSede(pharmacist.getSite()));
        model.addAttribute("listaSolicitudesBOT",purchaseOrderRepository.listaVentasSolicitudesBOTPorSede(pharmacist.getSite()));

        return "pharmacist/solicitudesCompra";
    }

    @GetMapping("/verDetalleSolicitud")
    public String detalleSolicitudVenta(@RequestParam("idSolicitud") int idOrdenVenta, Model model, HttpSession session
    ) {

        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        model.addAttribute("listaMedicamentos", medicineRepository.listaMedicamentosPorCompra(idOrdenVenta));
        Optional<PurchaseOrder> purchaseOrderOpt = purchaseOrderRepository.findById(idOrdenVenta);
        if(purchaseOrderOpt.isPresent()){
            PurchaseOrder purchaseOrder = purchaseOrderOpt.get();
            model.addAttribute("purchaseOrder", purchaseOrder);
            return "pharmacist/detalleSolicitud";
        }else{
            return "redirect:verMedicinelist";
        }
    }

    @GetMapping("/aceptarSolicitud")
    public String aceptarSolicitud(@RequestParam("idSolicitud") int idSolicitud) {
        purchaseOrderRepository.aceptarSolicitudPorId(idSolicitud);

        Tracking tracking = new Tracking();
        tracking.setSolicitudDate(LocalDateTime.now());

        return "redirect:solicitudesFarmacista";
    }


    @GetMapping("/rechazarSolicitud")
    public Object rechazarSolicitud(@RequestParam("idSolicitud") int idSolicitud) {
        try {
            purchaseOrderRepository.rechazarSolicitudPorId(idSolicitud);
            HashMap<String, Object> okey = new HashMap<>();
            okey.put("Succes", "Todo good");
            return ResponseEntity.ok(okey);
        }catch (Exception err) {
            System.out.println("pepepe");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "se repite el medicamento en la lista");
            return ResponseEntity.badRequest().body(er);
        }
    }


    @GetMapping("/productDetails")
    public String verProductDetails(Model model, HttpSession session
    ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        return "pharmacist/product-details";
    }



    @GetMapping("/verProductList")
    public String verProductList(Model model, HttpSession session
    ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        model.addAttribute("listaPurchaseOrderPresencial", purchaseOrderRepository.listaVentasPresencialPorSede(pharmacist.getSite()));
        model.addAttribute("listaPurchaseOrderWeb", purchaseOrderRepository.listaVentasWEBPorSede(pharmacist.getSite()));
        model.addAttribute("listaPurchaseOrderBOT", purchaseOrderRepository.listaVentasBOTPorSede(pharmacist.getSite()));

        return "pharmacist/productlist";
    }



    @GetMapping(value={"/verProfileFarmacista", ""})
    public String verPerfilPharmacist(Model model, HttpSession session
    ){

        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        model.addAttribute("farmacista",pharmacist);
        return "pharmacist/profile";
    }

    @PostMapping("/editarPerfilPharmacist")
    public String editarDatosFarmacista(@RequestParam("pharmacistFile") MultipartFile imagen, Model model, @RequestParam("email")String email, @RequestParam("distrit")String distrit, RedirectAttributes attr, HttpSession session){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();;
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        //Actualizar datos cambiados
        System.out.println(pharmacist.getIdFarmacista());
        System.out.println(pharmacist.getEmail());
        System.out.println(pharmacist.getDistrit());

        boolean falloN = false;

        try{


            if (email==null || email.trim().isEmpty()) {
                falloN = true;
                attr.addFlashAttribute("errorEm", "El email no debe estar vacío");
            }

            if (distrit==null || distrit.trim().isEmpty()) {
                falloN = true;
                attr.addFlashAttribute("errorD", "El distrito no debe estar vacio");
            }

            byte[] bytesImgPerfil = imagen.getBytes();
            String fileOriginalName = imagen.getOriginalFilename();

            long fileSize = imagen.getSize();
            long maxFileSize = 5 * 1024 * 1024;


            String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

            if (fileSize > maxFileSize) {
                falloN = true;
                attr.addFlashAttribute("imageError", "El tamaño de la imagen excede a 5MB");

            }
            if (
                    !fileExtension.equalsIgnoreCase(".jpg") &&
                            !fileExtension.equalsIgnoreCase(".png") &&
                            !fileExtension.equalsIgnoreCase(".jpeg")
            ) {
                falloN = true;
                attr.addFlashAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");

            }

            if(!falloN){
                Pharmacist sessionPharma = (Pharmacist) session.getAttribute("usuario");
                //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");

                //String rutaAbsoluta = directorioImagenPerfil.toFile().getAbsolutePath();
                //NUBE
                //String rutaAbsoluta = "//SaintMedic//imagenes";
                //LOCAL
                String rutaAbsoluta = "//SaintMedic//imagenes";

                Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                Files.write(rutaCompleta, bytesImgPerfil);

                int idUser = userRepository.encontrarId(sessionPharma.getEmail());

                pharmacist.setPhoto(imagen.getOriginalFilename());
                pharmacistRepository.updateEmailAndDistritById(email,distrit, pharmacist.getIdFarmacista());
                userRepository.actualizarEmail(email,idUser);

                return "redirect:verProfileFarmacista";
            }else{
                return "pharmacist/profile";
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }





    @GetMapping("/verMedicinelist")
    public String verMedicineList(Model model, HttpSession session
    ){
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        model.addAttribute("listamedicamentosfarm", medicineRepository.listaMedicamentosPorSedeFarmacista(idPharmacist));
        return "pharmacist/medicinelist";
    }

    //Aplicando filtros
    @PostMapping("/verMedicinelistFiltrado")
    public String verMedicinelistFiltrado(Model model, HttpSession session ,
                                          @RequestParam("category") String category ,
                                          @RequestParam("cantidad") String cantidad ,
                                          @RequestParam("precio") String precio
    ){
        boolean errores = false;
        //Validamos campos del filtro
        if(!category.isEmpty()) {
            if (!(category.contains("Dolor") || category.contains("Belleza") || category.contains("Suplementos") || category.contains("Alergías") || category.contains("Otros"))) {
                errores = true;
            }
        }
        Integer cantidadInt = 0 ;
        if(!cantidad.isEmpty()) {
            try {
                cantidadInt = Integer.parseInt(cantidad);
            } catch (NumberFormatException err) {
                errores = true;
            }
        }
        Double precioFloat = 0.0;
        if(!precio.isEmpty()) {
            try {
                precioFloat = Double.parseDouble(precio);
            } catch (NumberFormatException err) {
                errores = true;
            }
        }

        if(errores){
            return "redirect:verMedicineList";
        }else{
            int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
            Pharmacist pharmacist = new Pharmacist();
            pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
            model.addAttribute("sede", pharmacist.getSite());
            model.addAttribute("nombre", pharmacist.getName());
            model.addAttribute("apellido",pharmacist.getLastName());
            List<MedicamentosPorSedeDTO> listaaa = medicineRepository.listaMedicamentosPorSedeFarmacista(idPharmacist);
            HashSet<MedicamentosPorSedeDTO> primerFiltro = new HashSet<>();
            if(!category.isEmpty()) {
                for (MedicamentosPorSedeDTO m : listaaa) {
                    if (m.getCategoria().equals(category)) {
                        primerFiltro.add(m);
                    }
                }
            }else{
                for (MedicamentosPorSedeDTO m : listaaa) {
                        primerFiltro.add(m);
                }
            }
            HashSet<MedicamentosPorSedeDTO> segundoFiltro = new HashSet<>();
            if(!cantidad.isEmpty()) {
                for (MedicamentosPorSedeDTO m : listaaa) {
                    switch(cantidadInt){
                        case 1:
                            if ( 0< m.getCantidad() &&  m.getCantidad() < 25  ) {
                                segundoFiltro.add(m);
                            }
                            break;
                        case 2:
                            if (25< m.getCantidad() &&  m.getCantidad() < 50 ) {
                                segundoFiltro.add(m);
                            }
                            break;
                        case 3:
                            if (50< m.getCantidad()  ) {
                                segundoFiltro.add(m);
                            }
                            break;
                    }
                }
            }else{
                for (MedicamentosPorSedeDTO m : listaaa) {
                        segundoFiltro.add(m);
                }
            }

            HashSet<MedicamentosPorSedeDTO> tercerFiltro = new HashSet<>();
            if(!precio.isEmpty()) {
                for (MedicamentosPorSedeDTO m : listaaa) {
                    switch(cantidadInt){
                        case 1:
                            if (0< m.getPrecio() &&  m.getPrecio() < 25 ) {
                                tercerFiltro.add(m);
                            }
                            break;
                        case 2:
                            if (25< m.getPrecio() &&  m.getPrecio() < 50) {
                                tercerFiltro.add(m);
                            }
                            break;
                        case 3:
                            if (50< m.getPrecio()) {
                                tercerFiltro.add(m);
                            }
                            break;
                    }

                }
            }else{
                for (MedicamentosPorSedeDTO m : listaaa) {
                        tercerFiltro.add(m);
                }
            }
            primerFiltro.retainAll(segundoFiltro);
            tercerFiltro.retainAll(primerFiltro);
            List<MedicamentosPorSedeDTO> listaSinDuplicados = new ArrayList<>(tercerFiltro);
            model.addAttribute("listamedicamentosfarm", listaSinDuplicados);
            return "pharmacist/medicinelist";
        }
    }

    @PostMapping(value="/filtradoPost")
    public String busquedaMedicamentos(Model model , HttpSession session , @RequestParam("medicamento") String medicamento) {
        int idPharmacist = ((Pharmacist) session.getAttribute("usuario")).getIdFarmacista();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido", pharmacist.getLastName());
        List<MedicamentosPorSedeDTO> listaaaa = medicineRepository.listaMedicamentosPorSedeFarmacista(idPharmacist);
        ArrayList<MedicamentosPorSedeDTO> listaFiltrada = new ArrayList<>();
        if (!medicamento.isEmpty()) {
            for (MedicamentosPorSedeDTO m : listaaaa) {
                if (m.getNombreMedicamento().contains(medicamento)) {
                    listaFiltrada.add(m);
                }
            }
    }else {
            for (MedicamentosPorSedeDTO m : listaaaa) {
                    listaFiltrada.add(m);

            }
        }
        model.addAttribute("listamedicamentosfarm",listaFiltrada);
        model.addAttribute("listaDoctores", doctorRepository.findAll());
        try {
            if (!(model.getAttribute("idPatient")).equals("")) {
                model.addAttribute("fullNamePatient", (patientRepository.findById(Integer.parseInt("" + model.getAttribute("idPatient")))).get().getName() + " " + (patientRepository.findById(Integer.parseInt("" + model.getAttribute("idPatient")))).get().getLastName());
                model.addAttribute("fullNameDoctor", (doctorRepository.findById(Integer.parseInt("" + model.getAttribute("idDoctor")))).get().getName() + " " + (doctorRepository.findById(Integer.parseInt("" + model.getAttribute("idDoctor")))).get().getLastName());
            } else {
                model.addAttribute("fullNamePatient", null);
                model.addAttribute("fullNameDoctor", null);
            }
        }catch (Exception err){
            model.addAttribute("fullNamePatient", null);
            model.addAttribute("fullNameDoctor", null);
        }
        int idPhar = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        model.addAttribute("listaComprar" , carritoVentaRepository.getMedicineListByPharmacist(idPhar));
        return "pharmacist/pos";
    }


    @GetMapping("/detallesMedicamentos")
    public String detallesMedicamentos(@RequestParam("idMedicine") int idMedicine, Model model, HttpSession session
    ) {
        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            List<LotesValidosporMedicamentoDTO> listaLotesporMedicamento =  loteRepository.obtenerLotesValidosPorMedicamento(idMedicine);

            model.addAttribute("listaLotes",listaLotesporMedicamento);
            return "pharmacist/detallesMedicine";
        } else {
            return "redirect:verMedicinelist";
        }
    }
    @GetMapping("/detallesOrdenVenta")
    public String detallesOrdenVenta(@RequestParam("idPurchaseOrder") int idOrdenVenta, Model model, HttpSession session
    ) {

        int idPharmacist = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        Optional<PurchaseOrder> purchaseOrderOpt = purchaseOrderRepository.findById(idOrdenVenta);
        model.addAttribute("listaMedicamentos", medicineRepository.listaMedicamentosPorCompra(idOrdenVenta));
        if(purchaseOrderOpt.isPresent()){
            PurchaseOrder purchaseOrder = purchaseOrderOpt.get();
            model.addAttribute("purchaseOrder", purchaseOrder);
            return "pharmacist/product-details";
        }else{
            return "redirect:verMedicinelist";
        }
    }

    @RequestMapping("/confirmarDatosPaciente")
    @ResponseBody
    public ArrayList<String> confirmarDatosPaciente(@RequestBody String cuerpo , Model  model) throws JsonProcessingException {
        ArrayList<String> response = new ArrayList<String>();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("cuerpo es "+  cuerpo);
        // Crear un objeto JsonNode para almacenar el JSON resultante
        JsonNode jsonNode = objectMapper.createObjectNode();
        errorData erro = new errorData();
        // Dividir la cadena en pares clave-valor y agregarlos al objeto JsonNode
        String[] pairs = cuerpo.split("&");
        String dni = pairs[0].split("=")[1];
        String idDoctor = pairs[1].split("=")[1];
        System.out.println(dni);
        System.out.println(idDoctor);
        //Verificamos si ingreso un DNI valido tanto por convención de letras como por coincidencias en la bd
        boolean errorCasteo = false;
        try{
            Integer.parseInt(dni);
        }catch(NumberFormatException err){
            errorCasteo = true;
        }
        //Ahora buscamos al DNI ;
        Optional<Patient> p = patientRepository.findByDni(dni);
        Optional<Doctor> d = doctorRepository.findById(Integer.parseInt(idDoctor));
        if(!errorCasteo){
            if(p.isPresent() && d.isPresent()){
                model.addAttribute("idPatient",""+p.get().getIdPatient());
                model.addAttribute("idDoctor",""+d.get().getIdDoctor());
                erro.setError("");
                response.add(objectMapper.writeValueAsString(erro));
            }else{
                erro.setError("noEncontro");
                response.add(objectMapper.writeValueAsString(erro));
            }
        }else{
            erro.setError("casteo");
            response.add(objectMapper.writeValueAsString(erro));
        }
        return response;
    }

    @GetMapping("/eliminarPacienteDoctorDesdeFarmacista")
    public String eliminarSessionPaciente(Model model ){
        model.addAttribute("idPatient" , "");
        model.addAttribute("idDoctor" , "");
        return "redirect:posFarmacista";
    }



    @RequestMapping("/GenerarVenta")
    @ResponseBody
    public Map<String, String> GenerarVentaFarmacista( @RequestBody String cuerpo ,Model model , HttpSession session) throws JsonProcessingException {
        //Queries para la venta
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println(cuerpo);
            ObjectMapper objectMapper = new ObjectMapper();
            //Contiene toda la data de la venta
            DataVenta data = objectMapper.readValue(cuerpo, DataVenta.class);
            //Creamos el purchase order
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setDeliveryHour(LocalTime.now());
            purchaseOrder.setPatient(patientRepository.findById(Integer.parseInt((String) model.getAttribute("idPatient"))).get());
            purchaseOrder.setIdDoctor(doctorRepository.findById(Integer.parseInt((String) model.getAttribute("idDoctor"))).get());
            purchaseOrder.setSite(pharmacistRepository.findById(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista()).get().getSite());
            purchaseOrder.setStatePaid("Pagado");
            purchaseOrder.setTipo("Presencial");
            purchaseOrder.setDireccion(pharmacistRepository.findById(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista()).get().getSite());
            purchaseOrder.setTipoPago("Efectivo");
            purchaseOrder.setReleaseDate(LocalDate.now());
            PurchaseOrder p = purchaseOrderRepository.save(purchaseOrder);

            ArrayList<Integer> listaIdMedicine = new ArrayList<>();
            ArrayList<Integer> listaCantidades = new ArrayList<>();
            for (int i = 0; i < data.getIds().toArray().length; i++) {
                listaIdMedicine.add(Integer.parseInt("" + data.getIds().get(i)));
                listaCantidades.add(Integer.parseInt("" + data.getCantidades().get(i)));
            }

            boolean suficienteStock= true;
            for (int idx = 0; idx < data.getIds().toArray().length; idx++) {
                int id  = listaIdMedicine.get(idx);
                int cantidad = listaCantidades.get(idx);
                String site = pharmacistRepository.findById(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista()).get().getSite();
                List<MedicamentosPorSedeDTO> listaaaaaaaa = medicineRepository.listaMedicamentosPorSedeFarmacista(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista());
                for (MedicamentosPorSedeDTO m : listaaaaaaaa) {
                    if(m.getIdMedicine() == id){
                        if(m.getCantidad()>=cantidad){
                            suficienteStock = true;
                            break;
                        }else{
                            suficienteStock = false;
                            break;
                        }
                    }else{
                        continue;
                    }
                }
            }

            if(suficienteStock){
                for (int idx = 0; idx < data.getIds().toArray().length; idx++) {
                    PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                    purchaseHasLote.setCantidadComprar(listaCantidades.get(idx));
                    purchaseHasLote.setPurchaseOrder(p);
                    PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
                    purchaseHasLotID.setIdPurchase(p.getId());
                    List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(listaIdMedicine.get(idx), listaCantidades.get(idx), pharmacistRepository.findById(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista()).get().getSite());
                    if (listaLotesPosibles.isEmpty()) {
                        continue;
                    }
                    purchaseHasLote.setLote(listaLotesPosibles.get(0));
                    loteRepository.actualizarStockLote(listaLotesPosibles.get(0).getIdLote(), listaCantidades.get(idx));
                    purchaseHasLotID.setIdLote(listaLotesPosibles.get(0).getIdLote());
                    purchaseHasLote.setId(purchaseHasLotID);
                    purchaseHasLoteRepository.save(purchaseHasLote);
                    model.addAttribute("idPatient", "");
                    model.addAttribute("idDoctor", "");
                }
                List<CarritoVenta> listaaaa = carritoVentaRepository.getMedicineListByPharmacist(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista());
                for(CarritoVenta flsmdfr : listaaaa  ){
                    carritoVentaRepository.deleteById(flsmdfr.getId());
                }
                response.put("error", "");
            }else{
                response.put("error", "noHaySuficienteStock");
            }

        }catch(Exception error){
            response.put("error", "errorInesperado");
        }
        return response;
    }


    //Metodos RestServer
    @GetMapping(value="/addCarritoVenta")
    public Object validarCarrito(@RequestParam("idProducto") String  idProducto , HttpSession session){
        try {
            int idProduct = Integer.parseInt(idProducto);
            if (idProducto != null) {
                Pharmacist p = (Pharmacist) session.getAttribute("usuario");
                //Verficamos que no este repetido
                List<CarritoVenta> lista =  carritoVentaRepository.getMedicineListByPharmacist(p.getIdFarmacista());
                boolean existeMecidina =false;
                for(CarritoVenta c : lista){
                    if(c.getIdMedicine().getIdMedicine() == idProduct){
                        existeMecidina = true;
                    }
                }
                if(!existeMecidina){
                    CarritoVenta car  = new CarritoVenta();
                    car.setCantidad(1);
                    car.setIdPharmacist(p);
                    car.setIdMedicine(medicineRepository.findById(Integer.parseInt(idProducto)).get());
                    carritoVentaRepository.save(car);
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

    ///pharmacist/vaciarCarrito
    @GetMapping(value="/vaciarCarrito")
    public Object deleteCarrito( HttpSession session){
        List<CarritoVenta>list = carritoVentaRepository.getMedicineListByPharmacist(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista());
        ArrayList<Integer> listaId = new ArrayList<>();
        for(CarritoVenta c : list ){
            listaId.add (c.getId());
        }
        carritoVentaRepository.deleteAllByIdInBatch(listaId);
        return "redirect:posFarmacista";
    }

    @GetMapping(value="/deleteProduct")
    public Object deleteProduct(@RequestParam("idProducto") String  idProducto , HttpSession session){
        try {
            int idProduct = Integer.parseInt(idProducto);
            if (idProducto != null) {
                Pharmacist p = (Pharmacist) session.getAttribute("usuario");
                List<CarritoVenta> listaCart = carritoVentaRepository.getMedicineListByPharmacist(p.getIdFarmacista());
                CarritoVenta cat =  new CarritoVenta();
                for(CarritoVenta c : listaCart){
                    if(c.getIdMedicine().getIdMedicine() == idProduct){
                        cat =  c;
                    }
                }
                carritoVentaRepository.deleteById(cat.getId());
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
    //  deleteProductCarritoVenta

    @GetMapping(value="/deleteProductCarritoVenta")
    public Object deleteCarritoProduct(@RequestParam("idProducto") String  idProducto , HttpSession session){
        try {
            int idProduct = Integer.parseInt(idProducto);
            Medicine m =  medicineRepository.findById(Integer.parseInt(idProducto)).get();
            if (idProducto != null) {
                Pharmacist p = (Pharmacist) session.getAttribute("usuario");
                List<CarritoVenta> listaCart = carritoVentaRepository.getMedicineListByPharmacist(p.getIdFarmacista());
                CarritoVenta cat =  new CarritoVenta();
                for(CarritoVenta c : listaCart){
                    if(c.getIdMedicine().getIdMedicine() == m.getIdMedicine()){
                        cat =  c;
                    }
                }
                carritoVentaRepository.deleteById(cat.getId());
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
            Pharmacist p =  (Pharmacist)  session.getAttribute("usuario");
            return ResponseEntity.ok(carritoVentaRepository.getMedicineListByPharmacist(p.getIdFarmacista()));
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }


    @GetMapping(value="/updateCantidad")
    @ResponseBody
    public Object updateCantidadA(@RequestParam("idProduct") String idProduct, @RequestParam("newCantidad") String newCantidad,  HttpSession session){
        try {
            Pharmacist p =  (Pharmacist)  session.getAttribute("usuario");
            List<CarritoVenta> list = carritoVentaRepository.getMedicineListByPharmacist(p.getIdFarmacista());
            CarritoVenta aux= new CarritoVenta();
            for(CarritoVenta c:  list){
                if(c.getIdMedicine().getIdMedicine()== Integer.parseInt(idProduct)){
                    aux= c;
                }
            }
            aux.setCantidad(Integer.parseInt(newCantidad));
            carritoVentaRepository.save(aux);
            return ResponseEntity.ok(carritoVentaRepository.getMedicineListByPharmacist(p.getIdFarmacista()));
        } catch (Exception err) {
            System.out.println("ErrorFatal");
            HashMap<String, Object> er = new HashMap<>();
            er.put("error", "errorHola");
            er.put("date", "" + LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }


    //Generar preorden y todos los web services usados
    @GetMapping(value="/generarPreorden")
    public String verGenerarPreorden(Model model, HttpSession session){
        Pharmacist phar = (Pharmacist) session.getAttribute("usuario");
        List<MedicamentosPorSedeDTO> lista =  medicineRepository.listaMedicamentosPorSedeFarmacista(phar.getIdFarmacista());
        ArrayList<MedicamentosPorSedeDTO> listaFiltrada=  new ArrayList<>();
        for( MedicamentosPorSedeDTO  m : lista ){
            if(m.getCantidad()==0){
                listaFiltrada.add(m);
            }
        }

        model.addAttribute("listaMedicamentosBS",listaFiltrada );
        try {
            if (!(model.getAttribute("idPatient")).equals("")) {
                model.addAttribute("fullNamePatient", (patientRepository.findById(Integer.parseInt("" + model.getAttribute("idPatient")))).get().getName() + " " + (patientRepository.findById(Integer.parseInt("" + model.getAttribute("idPatient")))).get().getLastName());
                model.addAttribute("fullNameDoctor", (doctorRepository.findById(Integer.parseInt("" + model.getAttribute("idDoctor")))).get().getName() + " " + (doctorRepository.findById(Integer.parseInt("" + model.getAttribute("idDoctor")))).get().getLastName());
            } else {
                model.addAttribute("fullNamePatient", null);
                model.addAttribute("fullNameDoctor", null);
            }
        }catch (Exception err){
            model.addAttribute("fullNamePatient", null);
            model.addAttribute("fullNameDoctor", null);
        }
        int idPhar = ((Pharmacist)session.getAttribute("usuario")).getIdFarmacista();
        model.addAttribute("listaComprar" , carritoVentaRepository.getMedicineListByPharmacist(idPhar));
        return "pharmacist/generarPreorden";
    }

    @GetMapping(value="/verPreordenes")
    public String verPreordenes(Model model, HttpSession session){

        return "/pharmacist/verPreordenes";
    }




    @GetMapping(value="/getUsuarioPorDni")
    public Object getUsuarioPorDni(@RequestParam(value = "dni") String dni){
        try{
        Patient p  =  patientRepository.findByDni(dni).get();
        return ResponseEntity.ok(p);
        } catch(Exception err ){
            err.printStackTrace();
            HashMap<String , Object> has = new HashMap<>();
            has.put("error", "error");
            return ResponseEntity.badRequest().body(has);
        }
    }



    @PostMapping(value="/generarPreordenPost")
    public Object generarPreordenPost(Preorden p, HttpSession session){
        try {
            Pharmacist phar = (Pharmacist) session.getAttribute("usuario");
            //Primero se crean los lotes necesarios para luego venderlos a la persona
            String[] ids = p.getIds();
            String[] cantidades = p.getCantidad();
            ArrayList<Integer> listaa = new ArrayList<>();
            for (int idx = 0; idx < ids.length; idx++) {
                int id = Integer.parseInt(ids[idx]);
                int cantidad = Integer.parseInt(cantidades[idx]);
                Lote lo = new Lote();
                lo.setMedicine((medicineRepository.findById(id).get()));
                lo.setSite(phar.getSite());
                LocalDate fechaActual = LocalDate.now();
                LocalDate fechaFutura = fechaActual.plusMonths(5);
                lo.setExpireDate(fechaFutura);
                lo.setExpire(false);
                lo.setStock(0);
                lo.setInitialQuantity(cantidad);
                lo.setVisible(true);
                Lote l = loteRepository.save(lo);
                listaa.add(l.getIdLote());
            }
            PurchaseOrder pur = new PurchaseOrder();
            pur.setPatient(patientRepository.findByDni(p.getName()).get());
            pur.setIdDoctor((doctorRepository.listaDoctorPorSedePaciente(phar.getSite())).get(0));
            pur.setTipo("Preorden");
            pur.setReleaseDate(LocalDate.now());
            pur.setSite(phar.getSite());
            PurchaseOrder purchase = purchaseOrderRepository.save(pur);
            for (int idx = 0; idx < ids.length; idx++) {
                PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                int id = Integer.parseInt(ids[idx]);
                int cantidad = Integer.parseInt(cantidades[idx]);
                purchaseHasLote.setCantidadComprar(cantidad);
                purchaseHasLote.setPurchaseOrder(purchase);
                purchaseHasLote.setLote(loteRepository.findById(listaa.get(idx)).get());
            }
            HashMap<String , Object> has = new HashMap<>();
            has.put("status", "Todo en orden");
            return ResponseEntity.ok(has);
        }catch(Exception err){
            err.printStackTrace();
            HashMap<String , Object> has = new HashMap<>();
            has.put("error", "error");
            return ResponseEntity.badRequest().body(has);
        }
    }

    public class Preorden{
        private String name;
        private String date;
        private String[] ids;
        private String[] cantidad;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String[] getIds() {
            return ids;
        }

        public void setIds(String[] ids) {
            this.ids = ids;
        }

        public String[] getCantidad() {
            return cantidad;
        }

        public void setCantidad(String[] cantidad) {
            this.cantidad = cantidad;
        }
    }


}
class errorData{
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
class patientData{
    private String dni;
    private String doctor;
    public patientData() {
    }
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}
class DataVenta{
    private ArrayList<Object> ids;
    private ArrayList<Object> cantidades;
    public DataVenta(){

    }
    public ArrayList<Object> getIds() {
        return ids;
    }

    public void setIds(ArrayList<Object> ids) {
        this.ids = ids;
    }

    public ArrayList<Object> getCantidades() {
        return cantidades;
    }

    public void setCantidades(ArrayList<Object> cantidades) {
        this.cantidades = cantidades;
    }
}
