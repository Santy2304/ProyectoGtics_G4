package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.MeciamentosPorCompraDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchasePorPatientDTO;
import com.example.proyectogrupo4_gtics.DTOs.lotesPorReposicion;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.DTOs.medicamentosPorSedeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
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

    final PurchaseHasLoteRepository purchaseHasLoteRepository;

    final PurchaseOrderRepository purchaseOrderRepository;
    private final DoctorRepository doctorRepository;
    private final LoteRepository loteRepository;

    public PatientController (SiteRepository siteRepository ,PatientRepository patientRepository , MedicineRepository medicineRepository,
                              PurchaseHasLoteRepository purchaseHasLoteRepository, PurchaseOrderRepository purchaseOrderRepository,
                              DoctorRepository doctorRepository,
                              LoteRepository loteRepository) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.medicineRepository = medicineRepository;
        this.purchaseHasLoteRepository = purchaseHasLoteRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.doctorRepository = doctorRepository;
        this.loteRepository = loteRepository;
    }

    @GetMapping("/sessionPatient")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idAdministrator){
        model.addAttribute("idUser",idAdministrator);
        return "redirect:patient/ElegirSede";
    }

    @GetMapping("/ElegirSede")
    public String ElegirSede( Model model){
        //Se listan las sedes
        String idUser =  (String) model.getAttribute("idUser");
        Patient patient = patientRepository.findById(Integer.parseInt(idUser)).get();
        System.out.println(patient.getName());
        List<Site> listSite = siteRepository.findAll();
        model.addAttribute("listSite", listSite);
        return "elegirSede";
    }

    @GetMapping("/elegirSedePrimeraVez")
    public String llevarVistaPrincipal(@RequestParam("idSede") String idSede ,Model model){
        model.addAttribute("idSede", (siteRepository.findById(Integer.parseInt(idSede)).get()).getIdSite());
        List<medicamentosPorSedeDTO> listMedicineBySede = medicineRepository.getMedicineBySite(Integer.parseInt(idSede));
        model.addAttribute("listaMedicinas" , listMedicineBySede) ;
        return "pacient/principal";
    }

    @GetMapping("/elegirSedeEnPagina")
    public String elegirSedeEnPagina(@RequestParam("idSede") String nuevoIdSede , Model model,  @SessionAttribute String idSede ){
        model.addAttribute("idSede", (siteRepository.findById(Integer.parseInt(nuevoIdSede)).get()).getIdSite());
        List<medicamentosPorSedeDTO> listMedicineBySede = medicineRepository.getMedicineBySite(Integer.parseInt(idSede));
        model.addAttribute("listaMedicinas" , listMedicineBySede);
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
    @GetMapping("/verDatosPago")
    public String verDatosPago(@RequestParam("idPurchase") int idPurchase){
        return "pacient/datos_pago";
    }
    //No funciona bien

    //////////////////////////ORDENES DE COMPRA///////////////////////
    @GetMapping(value = {"/verGenerarOrdenCompra",""})
    public String verGenerarOrdenCompra(@SessionAttribute("idUser") String idUser,@SessionAttribute("idSede") String idSede ,Model model,RedirectAttributes redirectAttributes){

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

        Patient paciente = patientRepository.findById(Integer.parseInt(idUser)).get();
        model.addAttribute("direccion",paciente.getLocation());

        Medicine medicine = medicineRepository.findById(1).get();

        model.addAttribute("medicine",medicine);
        return "pacient/generar_orden_compra";
    }


    @PostMapping("/crearOrdenCompra")
    public String agregarOrdenCompra(@SessionAttribute("idUser") String idUser, @SessionAttribute("idSede") String idSede,
                                     @RequestParam("Hour") String HourStr,
                                     @RequestParam("cantidad")int cantidad,
                                     @RequestParam("idMedicine") int idMedicine,
                                     @RequestParam("phoneNumber") String phoneNumber,
                                     @RequestParam("direccion") String direccion,
                                     @RequestParam("idDoctor") int idDoctor,
                                     Model model, RedirectAttributes attr){



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
            return "redirect:patient/verGenerarOrdenCompra";
        }

        purchaseOrder.setIdDoctor(doctorRepository.findById(idDoctor).get());
        purchaseOrder.setTipo("tarjeta");
        purchaseOrder.setPhoneNumber(phoneNumber);
        purchaseOrder.setDireccion(direccion);
        Patient patient = patientRepository.findById(Integer.parseInt(idUser)).get();
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

        purchaseOrderRepository.save(purchaseOrder);

        PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
        purchaseHasLote.setCantidadComprar(cantidad);

        purchaseHasLote.setPurchaseOrder(purchaseOrder);
        PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
        purchaseHasLotID.setIdPurchase(purchaseOrder.getId());


        List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(idMedicine,cantidad, siteRepository.findById(Integer.parseInt(""+ model.getAttribute("idSede"))).get().getName());

        if (listaLotesPosibles.isEmpty()){
            return "redirect:patient/verPrincipalPaciente";
        }
        purchaseHasLote.setLote(listaLotesPosibles.get(0));
        purchaseHasLotID.setIdLote(listaLotesPosibles.get(0).getIdLote());

        purchaseHasLote.setId(purchaseHasLotID);

        purchaseHasLoteRepository.save(purchaseHasLote);

        return "redirect:patient/verTicket?idCompra="+purchaseOrder.getId();


    }


    @GetMapping("/verTicket")
    public String verTicket(@SessionAttribute("idUser") String idUser,@RequestParam("idCompra") int idCompra , Model model){

        model.addAttribute("idCompra",idCompra);

        return "pacient/ticketOrdenCompra";
    }



    @GetMapping("/verHistorial")
    public String verHistorial(@SessionAttribute("idUser") String idUser , Model model){
        List<PurchasePorPatientDTO> comprasPorPaciente = purchaseOrderRepository.obtenerComprarPorPaciente(Integer.parseInt(idUser));
        model.addAttribute("listaCompras",comprasPorPaciente);
        return "pacient/historial";
    }



/////////////////////////////////////////////////////////
    @GetMapping("/verNumeroOrden")
    public String verNumeroOrdenPaciente(){
        return "pacient/numero_de_orden";
    }

    @GetMapping("/verPerfilPaciente")
    public String verPerfilPaciente( @SessionAttribute("idUser") String idUser , Model model){
        Optional<Patient>patient=  patientRepository.findById(Integer.parseInt(idUser));
        model.addAttribute("paciente" , patient.get());
        return "pacient/perfil";
    }
    @GetMapping("/verPrincipalPaciente")
    public String verPrincipalPaciente(Model model , @SessionAttribute String idSede ){
        List<medicamentosPorSedeDTO> listMedicineBySede = medicineRepository.getMedicineBySite(Integer.parseInt(idSede));
        model.addAttribute("listaMedicinas" , listMedicineBySede) ;
        return "pacient/principal";
    }
    //No funciona bien
    @GetMapping("/verProductList")
    public String verProductListPaciente(){
        return "pacient/productlist";
    }
    @GetMapping("/verSeleccionarSedePaciente")
    public String verSeleccionarSedePaciente(Model model) {
        List<Site> listaSedes=  siteRepository.findAll();
        model.addAttribute("listaSede", listaSedes);
        return "pacient/seleccionarSede";
    }
    @RequestMapping("/verDetalleCompra")
    @ResponseBody
    public ArrayList<String> verDetalleCompra(@SessionAttribute("idUser") String idUser, @RequestParam("idPurchase") int idPurchase , Model model){
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
    public String verTrackingPaciente(@SessionAttribute("idUser") String idUser , Model model){

        List<PurchasePorPatientDTO> tracking = purchaseOrderRepository.obtenerComprarPorPacienteTracking(Integer.parseInt(idUser));

        model.addAttribute("listaTracking",tracking);

        return "pacient/tracking";
    }
    @PostMapping("/editarPerfilPaciente")
    public String editarDatosPaciente(@ModelAttribute("paciente") @Valid Patient patient, BindingResult bindingResult, Model model, RedirectAttributes attr){
        //Actualizar datos cambiados
        System.out.println(patient.getIdPatient());
        System.out.println(patient.getLocation());
        System.out.println(patient.getInsurance());
        if (bindingResult.hasErrors()) {
            return "pacient/perfil";
        } else {
            attr.addFlashAttribute("msg", "Paciente actualizado correctamente");
            patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), patient.getIdPatient());
            return "redirect:patient/verPerfilPaciente";
        }


    }

    @GetMapping("/cerrarSesionPaciente")
    public String eliminarStributo(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return "redirect:/inicioSesion";
    }

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


    @PostMapping("/addCart")
    public void addCart(Model model , Medicamento m , @SessionAttribute("carrito") ArrayList<Medicamento> carrito ) {
        System.out.println(m.getNombre());
        int count = 0;
        if(carrito.size() !=0) {
            for (Medicamento mm : carrito) {
                System.out.println(mm.nombre);
                if (mm.nombre.equals(m.nombre)) {
                    count++;
                }
            }
        }
        if(count==0){
            model.addAttribute("carrito" , carrito.add(m));
        }
    }


}
