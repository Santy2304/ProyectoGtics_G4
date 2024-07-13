package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
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
            error.printStackTrace();
            LinkedHashMap<String , Object > response =  new LinkedHashMap<>();
            response.put("Status", "Ocurrio un error inesperado");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping(value="/generarSolicitudVentaPorChatBot")
    @ResponseBody
    @CrossOrigin
    public Object generarSolicitudVentaPorChatBot( @RequestBody Solicitud solicitud) {
        LinkedHashMap<String , Object> generalResponse = new LinkedHashMap<>();
        try{
            if(solicitud.getDni()==null){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","no envio el dni del paciente");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            if(!patientRepository.findByDni(solicitud.getDni()).isPresent()){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","el DNI enviado no le corresponde a ningun paciente registrado en nuestra pagina ");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generalResponse);
            }
            if(solicitud.getPhoneNumber()==null){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","no envio el dni del phoneNumber");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            if(solicitud.getDeliverHour()==null){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","no envio el dni del deliverHour");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            if(solicitud.getListaMedicamentos()==null){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","no envio ninguna lista de medicamentos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            //Validamos la existencia de todos los medicamentos
            for(Medicamentos m : solicitud.getListaMedicamentos()){
                if(!medicineRepository.findById(Integer.parseInt(m.getIdMedicamento())).isPresent()){
                    generalResponse.put("status",  "Error");
                    generalResponse.put("message","No existe una medicina con el ID = "+  m.getIdMedicamento());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generalResponse);
                }
            }
            //Debemos verificar
            if(solicitud.getSede()==null){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","Debes de ingresar una sede");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generalResponse);
            }
            if(siteRepository.findAll().contains(solicitud.getSede())){
                generalResponse.put("status",  "Error");
                generalResponse.put("message","Has ingresado una sede que no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generalResponse);
            }
            //Asumimos q lo atendera cualquier doctor
            Doctor doctor =  doctorRepository.findAll().get(0);
            //Ahora verificamos que hayan suficientes medicamentos de acuerdo a la sede q se haya escogido
            for(Medicamentos mm: solicitud.getListaMedicamentos()){
                List<MedicamentosPorSedeDTO> lista =  medicineRepository.getMedicineBySiteName(solicitud.getSede());
                for(MedicamentosPorSedeDTO mDto : lista){
                    if(mDto.getIdMedicine() == Integer.parseInt(mm.getIdMedicamento()) &&
                    mDto.getCantidad() < Integer.parseInt(mm.getCantidad())
                    ){
                        generalResponse.put("status",  "Error");
                        generalResponse.put("message","No hay suficiente "+mDto.getNombreMedicamento()+" como para realizar la compra en la sede" +  solicitud.getSede() );
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponse);
                    }
                }
            }
            //Ya validado todo eso , pasamos generar la purchase order:

            PurchaseOrder  purchaseOrder = new PurchaseOrder();
            //Ahora verificamos q hayan suficientes

            //En construccion
            purchaseOrder.setSite(solicitud.getSede());
            purchaseOrder.setPatient(patientRepository.findByDni(solicitud.getDni()).get());
            purchaseOrder.setIdDoctor(doctor);
            purchaseOrder.setTipo("tarjeta");
            purchaseOrder.setPhoneNumber(solicitud.getPhoneNumber());
            purchaseOrder.setDireccion(patientRepository.findByDni(solicitud.getDni()).get().getLocation());
            purchaseOrder.setApproval("pendiente");
            purchaseOrder.setStatePaid("en espera");
            purchaseOrder.setTracking("en espera");
            purchaseOrder.setTipo("chatBot");
            purchaseOrder.setRecurrent(false);
            purchaseOrder.setDeliveryHour(solicitud.getDeliverHour());
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
            //Verifiquemos lo
            //Ahora probamos medicina por medicina

            for(Medicamentos mmm :  solicitud.getListaMedicamentos()){
                List<Lote> listaLotesPosibles = loteRepository.listarLotesPosiblesSantiago(Integer.parseInt(mmm.getIdMedicamento()), solicitud.getSede());
                int stockCentinela = Integer.parseInt(mmm.getCantidad());
                PurchaseHasLote purchaseHasLote = new PurchaseHasLote();
                purchaseHasLote.setCantidadComprar(Integer.parseInt(mmm.getCantidad()));
                purchaseHasLote.setPurchaseOrder(purchaseOrder);
                PurchaseHasLotID purchaseHasLotID = new PurchaseHasLotID();
                purchaseHasLotID.setIdPurchase(purchaseOrder.getId());
                for(Lote l :  listaLotesPosibles){
                    if(l.getStock()>= stockCentinela){
                        purchaseHasLote.setLote(l);
                        loteRepository.actualizarStockLote(l.getIdLote(), stockCentinela);
                        purchaseHasLotID.setIdLote(l.getIdLote());
                        purchaseHasLote.setCantidadComprar(stockCentinela);
                        purchaseHasLote.setId(purchaseHasLotID);
                        purchaseHasLoteRepository.save(purchaseHasLote);
                        break;
                    }else{
                        stockCentinela=stockCentinela-l.getStock();
                        purchaseHasLote.setCantidadComprar(l.getStock());
                        l.setStock(0);
                        loteRepository.save(l);
                        purchaseHasLote.setLote(l);
                        purchaseHasLotID.setIdLote(l.getIdLote());
                        purchaseHasLote.setId(purchaseHasLotID);
                        purchaseHasLoteRepository.save(purchaseHasLote);
                    }
                }
            }

            generalResponse.put("status", "success");
            generalResponse.put("date",""+ LocalDateTime.now());
            generalResponse.put("message", "Tu compra ha sido realizada con exito , ingresa esté a la espera de que el farmacista asignado valide la compra");
            return ResponseEntity.status(HttpStatus.OK).body(generalResponse);
        }catch(Exception error){
            error.printStackTrace();
            generalResponse.put("status", "error");
            generalResponse.put("date",""+ LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponse);
        }
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

    public class Solicitud {
        private String phoneNumber;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        private String sede;
        private String deliverHour;
        private String dni;
        private ArrayList<Medicamentos> listaMedicamentos;
        public String getDni() {
            return dni;
        }
        public void setDni(String dni) {
            this.dni = dni;
        }
        public ArrayList<Medicamentos> getListaMedicamentos() {
            return listaMedicamentos;
        }
        public void setListaMedicamentos(ArrayList<Medicamentos> listaMedicamentos) {
            this.listaMedicamentos = listaMedicamentos;
        }
        public String getSede() {
            return sede;
        }
        public void setSede(String sede) {
            this.sede = sede;
        }
        public String getDeliverHour() {
            return deliverHour;
        }
        public void setDeliverHour(String deliverHour) {
            this.deliverHour = deliverHour;
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
