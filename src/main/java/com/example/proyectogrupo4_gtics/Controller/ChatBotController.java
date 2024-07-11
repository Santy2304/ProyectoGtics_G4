package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatBotController {
    final AdministratorRepository administratorRepository;
    final DoctorRepository doctorRepository;
    final PharmacistRepository pharmacistRepository;
    final MedicineRepository medicineRepository;
    final ReplacementOrderRepository replacementOrderRepository;
    final LoteRepository loteRepository;
    final UserRepository userRepository;
    final NotificationsRepository notificationsRepository;
    final TrackingRepository trackingRepository;
    final SiteRepository siteRepository;

    final CodeRepository codeRepository;
    final PurchaseHasLoteRepository purchaseHasLoteRepository ;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PatientRepository patientRepository;

    public ChatBotController(AdministratorRepository administratorRepository, DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, MedicineRepository medicineRepository, ReplacementOrderRepository replacementOrderRepository,
                             ReplacementOrderHasMedicineRepository replacementOrderHasMedicineRepository ,
                             LoteRepository loteRepository,UserRepository userRepository,
                             TrackingRepository trackingRepository,NotificationsRepository notificationsRepository,
                             SiteRepository siteRepository, CodeRepository codeRepository,
                             PurchaseOrderRepository purchaseOrderRepository,
                             PatientRepository patientRepository,
                             PurchaseHasLoteRepository purchaseHasLoteRepository) {
        this.administratorRepository = administratorRepository;
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.medicineRepository = medicineRepository;
        this.replacementOrderRepository = replacementOrderRepository ;
        this.loteRepository =loteRepository;
        this.userRepository=userRepository;
        this.trackingRepository = trackingRepository;
        this.notificationsRepository= notificationsRepository;
        this.siteRepository=siteRepository;
        this.codeRepository = codeRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.patientRepository = patientRepository;
        this.purchaseHasLoteRepository = purchaseHasLoteRepository;

    }
    //Aca se crearan los metodos webServices con los q interactuará el ChatBot
    //Medicamentos disponibles de una farmacia
    @GetMapping(value="/getMedicineChatBot")
    @ResponseBody
    @CrossOrigin
    public Object getMedicine(){
        try {
            List<Medicine> lista = medicineRepository.findAll();
            List<Lote> lotes = loteRepository.findAll();
            LinkedHashMap<String, Object> response = new LinkedHashMap<>();
            ArrayList<MedicineContent> listaMedicinaAvailable = new ArrayList<>();
            LinkedHashMap<String, Object> Contenido = new LinkedHashMap<>();
            for (Medicine m : lista) {
                int count = 0;
                for (Lote l : lotes) {
                    if (l.getMedicine().getIdMedicine() == m.getIdMedicine()) {
                        count=count+ l.getStock();
                    }
                }
                MedicineContent medicineContent = new MedicineContent();
                medicineContent.setId(""+m.getIdMedicine());
                medicineContent.setName(m.getName());
                medicineContent.setCantidad(""+ count);
                medicineContent.setCategory("" + m.getCategory());
                medicineContent.setPrice(m.getPrice().doubleValue());
                medicineContent.setDescription(m.getDescription());
                listaMedicinaAvailable.add(medicineContent);
            }
            response.put("Status", "OK");
            response.put("Content", listaMedicinaAvailable);
            return ResponseEntity.ok(response);
        }catch(Exception error){
            LinkedHashMap<String , Object > response =  new LinkedHashMap<>();
            response.put("Status", "Ocurrio un error inesperado");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping(value="/generarSolicitudVentaPorChatBot")
    @ResponseBody
    @CrossOrigin
    public Object generarSolicitudVentaPorChatBot( Solicitud solicitud) {
        try{
            //En construccion
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setSite("Pando 1");
            purchaseOrder.setPatient(patientRepository.findByDni(solicitud.getDni()).get());
            purchaseOrder.setIdDoctor((doctorRepository.findAll().get(0)));
            purchaseOrder.setTipo("tarjeta");
            purchaseOrder.setPhoneNumber("920821483");
            purchaseOrder.setDireccion(patientRepository.findByDni(solicitud.getDni()).get().getLocation());
            purchaseOrder.setApproval("pendiente");
            purchaseOrder.setStatePaid("en espera");
            purchaseOrder.setTracking("en espera");
            purchaseOrder.setTipo("chatBot");
            purchaseOrder.setRecurrent(false);
            purchaseOrder.setDeliveryHour(""+ LocalDateTime.now());
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
            //Ahora validamos si existen suficientes unidades en pando1


            boolean validar = true;
            for (Medicamentos m : solicitud.getListaMedicamentos()) {
                PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                purchaseHasLote.setCantidadComprar(Integer.parseInt(m.getCantidad()));
                purchaseHasLote.setPurchaseOrder(purchaseOrder);
                PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
                purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
                List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles((  medicineRepository.findById( Integer.parseInt(m.getIdMedicamento())).get()).getIdMedicine(), Integer.parseInt(m.getCantidad()) ,"Pando 1");
                if (listaLotesPosibles.isEmpty()) {
                    validar = false;
                    break;
                }
            }

            if (validar) {
                for (Medicamentos m : solicitud.getListaMedicamentos()) {
                    PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                    purchaseHasLote.setCantidadComprar(Integer.parseInt(m.getCantidad()));
                    purchaseHasLote.setPurchaseOrder(purchaseOrder);
                    PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
                    purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
                    List<Lote> listaLotesPosibles = loteRepository.listarLotesPosibles((  medicineRepository.findById( Integer.parseInt(m.getIdMedicamento())).get()).getIdMedicine(), Integer.parseInt(m.getCantidad()) ,"Pando 1");
                    purchaseHasLote.setLote(listaLotesPosibles.get(0));
                    purchaseHasLotID.setIdLote(listaLotesPosibles.get(0).getIdLote());
                    purchaseHasLote.setId(purchaseHasLotID);
                    purchaseHasLoteRepository.save(purchaseHasLote);
                }

                LinkedHashMap<String , Object> response  =  new LinkedHashMap<>();
                response.put("success", true);
                response.put("idCompra", purchaseOrder.getId());
                return ResponseEntity.ok(response);
            } else {
                LinkedHashMap<String , Object> responseBad  =  new LinkedHashMap<>();
                responseBad.put("status","fail" );
                return ResponseEntity.badRequest().body(responseBad);
            }



        }catch(Exception error){

        }
        return ResponseEntity.ok();
    }


    public class MedicineContent {
        private String id;
        private String name;
        private String category;
        private double  price;
        private String description;
        private String cantidad;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }
    }

    public class listaMedicamentosAComprar{
        private ArrayList<Medicine> medicinas ;

    }

    public class Solicitud{
        private String dni;
        private String fecha;
        private ArrayList<Medicamentos> listaMedicamentos;

        public String getDni() {
            return dni;
        }

        public void setDni(String dni) {
            this.dni = dni;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public ArrayList<Medicamentos> getListaMedicamentos() {
            return listaMedicamentos;
        }

        public void setListaMedicamentos(ArrayList<Medicamentos> listaMedicamentos) {
            this.listaMedicamentos = listaMedicamentos;
        }
    }
    public class Medicamentos{
        private String idMedicamento;
        private String cantidad;

        public String getIdMedicamento() {
            return idMedicamento;
        }

        public void setIdMedicamento(String idMedicamento) {
            this.idMedicamento = idMedicamento;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }
    }

}
