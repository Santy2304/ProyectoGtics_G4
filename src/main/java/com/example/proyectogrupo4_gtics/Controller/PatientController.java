package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.MeciamentosPorCompraDTO;
import com.example.proyectogrupo4_gtics.DTOs.PurchasePorPatientDTO;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.example.proyectogrupo4_gtics.Entity.PurchaseHasLote;
import com.example.proyectogrupo4_gtics.Entity.Site;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.example.proyectogrupo4_gtics.DTOs.medicamentosPorSedeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@SessionAttributes({"idUser","idSede", "carrito"})
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

    public PatientController (SiteRepository siteRepository ,PatientRepository patientRepository , MedicineRepository medicineRepository,
                              PurchaseHasLoteRepository purchaseHasLoteRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.siteRepository = siteRepository;
        this.patientRepository = patientRepository;
        this.medicineRepository = medicineRepository;
        this.purchaseHasLoteRepository = purchaseHasLoteRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @GetMapping("/sessionPatient")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idAdministrator){
        model.addAttribute("idUser",idAdministrator);
        return "redirect:/ElegirSede";
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
    @GetMapping("/verDatosPagoPaciente")
    public String verDatosPago(@RequestParam("idPurchase") int idPurchase){
        return "pacient/datos_pago";
    }
    //No funciona bien

    @GetMapping("/verGenerarOrdenCompraPaciente")
    public String verGenerarOrdenCompra(){
        return "pacient/generar_orden_compra";
    }
    @GetMapping("/verHistorialPaciente")
    public String verHistorial(@SessionAttribute("idUser") String idUser , Model model){
        List<PurchasePorPatientDTO> comprasPorPaciente = purchaseOrderRepository.obtenerComprarPorPaciente(Integer.parseInt(idUser));
        model.addAttribute("listaCompras",comprasPorPaciente);
        return "pacient/historial";
    }

    @GetMapping("/verDetalleCompraPatient")
    public String verDetalleCompra(@SessionAttribute("idUser") String idUser,@RequestParam("idPurchase") int idPurchase , Model model){
        List<MeciamentosPorCompraDTO> meciamentosPorCompra = medicineRepository.listaMedicamentosPorCompra(idPurchase);
        model.addAttribute("listaMedicamentosPorCompra",meciamentosPorCompra);
        return "pacient/detalle";
    }


    @GetMapping("/verNumeroOrdenPaciente")
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
    @GetMapping("/verProductListPaciente")
    public String verProductListPaciente(){
        return "pacient/productlist";
    }
    @GetMapping("/verSeleccionarSedePaciente")
    public String verSeleccionarSedePaciente(Model model) {
        List<Site> listaSedes=  siteRepository.findAll();
        model.addAttribute("listaSede", listaSedes);
        return "pacient/seleccionarSede";
    }
    //Falta corregir
    @GetMapping("/verSingleProductPaciente")
    public String verSingleProductPaciente(@RequestParam String idMedicamento , Model model){
        Optional<Medicine> medicine  = medicineRepository.findById(Integer.parseInt(idMedicamento));
        if(medicine.isPresent()){
            model.addAttribute("medicina" , medicine.get());
            return "pacient/single_product";
        }else{
            return "redirect:/verPrincipalPaciente";
        }
    }
    @GetMapping("/verTrackingPaciente")
    public String verTrackingPaciente(@SessionAttribute("idUser") String idUser , Model model){

        List<PurchasePorPatientDTO> tracking = purchaseOrderRepository.obtenerComprarPorPacienteTracking(Integer.parseInt(idUser));

        model.addAttribute("listaTracking",tracking);

        return "pacient/tracking";
    }
    @PostMapping("/editarPerfilPaciente")
    public String editarDatosPaciente(Patient patient){
        //Actualizar datos cambiados
        System.out.println(patient.getIdPatient());
        System.out.println(patient.getLocation());
        System.out.println(patient.getInsurance());
        patientRepository.updatePatientData(patient.getDistrit(), patient.getLocation() , patient.getInsurance(), patient.getIdPatient());
        return "redirect:verPerfilPaciente";
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
