package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.*;
import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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


    public PharmacistController(CarritoVentaRepository carritoVentaRepository,CarritoRepository carritoRepository ,
                                PurchaseHasLoteRepository purchaseHasLoteRepository ,
                                MedicineRepository medicineRepository, PatientRepository patientRepository ,
                                LoteRepository loteRepository, PharmacistRepository pharmacistRepository,
                                DoctorRepository doctorRepository,PurchaseOrderRepository purchaseOrderRepository,
                                UserRepository userRepository, NotificationsRepository notificationsRepository) {
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
        if(!(model.getAttribute("idPatient")).equals("") ){
            model.addAttribute("fullNamePatient" , (patientRepository.findById(Integer.parseInt(""+model.getAttribute("idPatient")))).get().getName()+ " " + (patientRepository.findById(Integer.parseInt(""+model.getAttribute("idPatient")))).get().getLastName());
            model.addAttribute("fullNameDoctor" , (doctorRepository.findById(Integer.parseInt(""+model.getAttribute("idDoctor")))).get().getName()+ " " + (doctorRepository.findById(Integer.parseInt(""+model.getAttribute("idDoctor")))).get().getLastName());
        }else{
            model.addAttribute("fullNamePatient" , null);
            model.addAttribute("fullNameDoctor" , null);
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
        Notifications notifications = new Notifications();
        notifications.setContent("Su compra con código "+ idSolicitud +" ha sido aceptada, pudede proceder a pagarla en la vista historial");
        String email= patientRepository.findById(purchaseOrderRepository.idPatient(idSolicitud)).get().getEmail();
        notifications.setIdUsers(userRepository.findByEmail(email));
        notificationsRepository.save(notifications);
        return "redirect:solicitudesFarmacista";
    }


    @GetMapping("/rechazarSolicitud")
    public String rechazarSolicitud(@RequestParam("idSolicitud") int idSolicitud) {
        purchaseOrderRepository.rechazarSolicitudPorId(idSolicitud);
        return "redirect:solicitudesFarmacista";
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
    public String editarDatosFarmacista(Model model, @RequestParam("email")String email, @RequestParam("distrit")String distrit, RedirectAttributes attr, HttpSession session){
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
        if (email==null || email.trim().isEmpty()) {
            falloN = true;
            attr.addFlashAttribute("errorEm", "El email no debe estar vacío");
        }

        if (distrit==null || distrit.trim().isEmpty()) {
            falloN = true;
            attr.addFlashAttribute("errorD", "El distrito no debe estar vacio");
        }

        if(!falloN){
            Pharmacist sessionPharma = (Pharmacist) session.getAttribute("usuario");

            int idUser = userRepository.encontrarId(sessionPharma.getEmail());
            pharmacistRepository.updateEmailAndDistritById(email,distrit, pharmacist.getIdFarmacista());
            userRepository.actualizarEmail(email,idUser);

        }
        return "redirect:verProfileFarmacista";
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
                List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(listaIdMedicine.get(idx), listaCantidades.get(idx), pharmacistRepository.findById(((Pharmacist)session.getAttribute("usuario")).getIdFarmacista()).get().getSite());
                if (listaLotesPosibles.isEmpty()) {
                    suficienteStock= false;
                    continue;
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
