package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.*;
import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SessionAttributes({"idUser","sede" , "idPatient" , "idDoctor"})
@Controller
public class PharmacistController {
    final MedicineRepository medicineRepository;
    final LoteRepository loteRepository;
    final PharmacistRepository pharmacistRepository;
    private final DoctorRepository doctorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PatientRepository patientRepository;
    final PurchaseHasLoteRepository purchaseHasLoteRepository;


    public PharmacistController(PurchaseHasLoteRepository purchaseHasLoteRepository , MedicineRepository medicineRepository, PatientRepository patientRepository ,LoteRepository loteRepository, PharmacistRepository pharmacistRepository, DoctorRepository doctorRepository,PurchaseOrderRepository purchaseOrderRepository) {
        this.medicineRepository = medicineRepository;
        this.loteRepository = loteRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.doctorRepository = doctorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.patientRepository = patientRepository;
        this.purchaseHasLoteRepository = purchaseHasLoteRepository;

    }

    @GetMapping("/sessionPharmacist")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idPharmacist){
        model.addAttribute("idUser",idPharmacist);
        model.addAttribute("idPatient","");
        model.addAttribute("idDoctor","");
        return "redirect:/verMedicinelistFarmacista";
    }

    @GetMapping("/cerrarSesionPharmacist")
    public String eliminarStributo(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return "redirect:/inicioSesion";
    }

    @GetMapping("/verChatFarmacista")
    public String verChatPharmacist(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/chat";
    }

    @GetMapping("/verEditarProductoFarmacista")
    public String verEditProduct(Model model){

        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/editproduct";
    }


    @GetMapping("/verNotificationsFarmacista")
    public String verNotifications(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/notifications";
    }

    @GetMapping("/posFarmacista")
    public String verPosPharmacist(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
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
        ArrayList<Integer> ola = new ArrayList<>();
        ola.add(1);
        ola.add(3);
        ola.add(26);
        ola.add(27);
        //Enviamos una cantidad de medicamentos con sus cantidades deseadas
        ArrayList<Medicine> lista = new ArrayList<>() ;
        for(Integer id :  ola){
            lista.add(medicineRepository.findById(id).get());
        }
        model.addAttribute("listaComprar" , lista);
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
    public String verSolicitudes(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
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
    public String detalleSolicitudVenta(@RequestParam("idSolicitud") int idOrdenVenta, Model model) {

        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
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
            return "redirect:/verMedicinelistFarmacista";
        }
    }

    @GetMapping("/aceptarSolicitud")
    public String aceptarSolicitud(@RequestParam("idSolicitud") int idSolicitud) {
        purchaseOrderRepository.aceptarSolicitudPorId(idSolicitud);
        return "redirect:/solicitudesFarmacista";
    }


    @GetMapping("/rechazarSolicitud")
    public String rechazarSolicitud(@RequestParam("idSolicitud") int idSolicitud) {
        purchaseOrderRepository.rechazarSolicitudPorId(idSolicitud);
        return "redirect:/solicitudesFarmacista";
    }


    @GetMapping("/productDetails")
    public String verProductDetails(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        return "pharmacist/product-details";
    }



    @GetMapping("/verProductListFarmacista")
    public String verProductList(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
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

    @GetMapping("/verProfileFarmacista")
    public String verPerfilPharmacist(Model model){

        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        return "pharmacist/profile";
    }

    @PostMapping("/editarPerfilPharmacist")
    public String editarDatosFarmacista(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());
        //Actualizar datos cambiados
        System.out.println(pharmacist.getIdFarmacista());
        System.out.println(pharmacist.getEmail());
        System.out.println(pharmacist.getDistrit());
        pharmacistRepository.updateEmailAndDistritById(pharmacist.getEmail(), pharmacist.getDistrit(), pharmacist.getIdFarmacista());
        return "redirect:verProfileFarmacista";
    }

    @GetMapping("/verMedicinelistFarmacista")
    public String verMedicineList(Model model){
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());

        model.addAttribute("listamedicamentosfarm", medicineRepository.listaMedicamentosPorSedeFarmacista(idPharmacist));
        return "pharmacist/medicinelist";
    }

    @GetMapping("/detallesMedicamentos")
    public String detallesMedicamentos(@RequestParam("idMedicine") int idMedicine, Model model) {
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
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
            return "redirect:/verMedicinelistFarmacista";
        }
    }
    @GetMapping("/detallesOrdenVenta")
    public String detallesOrdenVenta(@RequestParam("idPurchaseOrder") int idOrdenVenta, Model model) {

        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
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
            return "redirect:/verMedicinelistFarmacista";
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
        return "redirect:/posFarmacista";
    }



    @RequestMapping("/GenerarVentaFarmacista")
    @ResponseBody
    public Map<String, String> GenerarVentaFarmacista( @RequestBody String cuerpo ,Model model) throws JsonProcessingException {
        //Queries para la venta
        System.out.println(cuerpo);
        Map<String,String> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        //Contiene toda la data de la venta
        DataVenta data = objectMapper.readValue(cuerpo, DataVenta.class);
        //Creamos el purchase order
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setDeliveryHour(LocalTime.now());
        purchaseOrder.setPatient(patientRepository.findById(Integer.parseInt((String)model.getAttribute("idPatient"))).get());
        purchaseOrder.setIdDoctor(doctorRepository.findById(Integer.parseInt((String) model.getAttribute("idDoctor"))).get());
        purchaseOrder.setSite(pharmacistRepository.findById(Integer.parseInt((String) model.getAttribute("idUser"))).get().getSite());
        purchaseOrder.setStatePaid("Pagado");
        purchaseOrder.setTipo("Presencial");
        purchaseOrder.setDireccion(pharmacistRepository.findById(Integer.parseInt((String) model.getAttribute("idUser"))).get().getSite());
        purchaseOrder.setTipoPago("Efectivo");
        purchaseOrder.setReleaseDate(LocalDate.now());
        PurchaseOrder p = purchaseOrderRepository.save(purchaseOrder);

        ArrayList<Integer> listaIdMedicine = new ArrayList<>();
        ArrayList<Integer> listaCantidades = new ArrayList<>();
        for(int i= 0 ;  i < data.getIds().toArray().length ;  i++ ){
            listaIdMedicine.add(Integer.parseInt("" + data.getIds().get(i)));
            listaCantidades.add(Integer.parseInt("" + data.getCantidades().get(i)));
        }

        for(int idx= 0 ;  idx < data.getIds().toArray().length ;  idx++) {
            PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
            purchaseHasLote.setCantidadComprar(listaCantidades.get(idx));
            purchaseHasLote.setPurchaseOrder(p);
            PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
            purchaseHasLotID.setIdPurchase(p.getId());
            List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles(listaIdMedicine.get(idx),listaCantidades.get(idx), pharmacistRepository.findById(Integer.parseInt(""+ model.getAttribute("idUser"))).get().getSite());
            if (listaLotesPosibles.isEmpty()){
                continue;
            }
            purchaseHasLote.setLote(listaLotesPosibles.get(0));
            loteRepository.actualizarStockLote(listaLotesPosibles.get(0).getIdLote(),listaCantidades.get(idx));
            purchaseHasLotID.setIdLote(listaLotesPosibles.get(0).getIdLote());
            purchaseHasLote.setId(purchaseHasLotID);
            purchaseHasLoteRepository.save(purchaseHasLote);
            model.addAttribute("idPatient" , "");
            model.addAttribute("idDoctor" , "");
        }
        response.put("error", "");
        return response;
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
