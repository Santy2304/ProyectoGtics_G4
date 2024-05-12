package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.*;
import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import org.hibernate.query.ImmutableEntityUpdateQueryHandlingMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SessionAttributes({"idUser","sede"})
@Controller
public class PharmacistController {
    final MedicineRepository medicineRepository;
    final LoteRepository loteRepository;
    final PharmacistRepository pharmacistRepository;
    private final DoctorRepository doctorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public PharmacistController(MedicineRepository medicineRepository, LoteRepository loteRepository, PharmacistRepository pharmacistRepository, DoctorRepository doctorRepository,PurchaseOrderRepository purchaseOrderRepository) {
        this.medicineRepository = medicineRepository;
        this.loteRepository = loteRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.doctorRepository = doctorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @GetMapping("/sessionPharmacist")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idPharmacist){
        model.addAttribute("idUser",idPharmacist);
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
        model.addAttribute("listaDoctores", doctorRepository.listaDoctoresPorSede(pharmacist.getSite()));
        return "pharmacist/pos";
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
        model.addAttribute("listaMedicamentos", medicineRepository.listaMedicamentosPorCompra(idOrdenVenta));
        Optional<PurchaseOrder> purchaseOrderOpt = purchaseOrderRepository.findById(idOrdenVenta);
        if(purchaseOrderOpt.isPresent()){
            PurchaseOrder purchaseOrder = purchaseOrderOpt.get();
            model.addAttribute("purchaseOrder", purchaseOrder);
            return "pharmacist/product-details";
        }else{
            return "redirect:/verMedicinelistFarmacista";
        }
    }

}
