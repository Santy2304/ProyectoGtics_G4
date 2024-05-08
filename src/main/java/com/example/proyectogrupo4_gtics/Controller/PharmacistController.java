package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorReposicionDTO;
import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.DTOs.cantidadMedicamentosDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;
import java.util.Optional;
@SessionAttributes({"idUser","sede"})
@Controller
public class PharmacistController {
    final MedicineRepository medicineRepository;
    final LoteRepository loteRepository;
    final PharmacistRepository pharmacistRepository;
    public PharmacistController(MedicineRepository medicineRepository, LoteRepository loteRepository, PharmacistRepository pharmacistRepository) {
        this.medicineRepository = medicineRepository;
        this.loteRepository = loteRepository;
        this.pharmacistRepository = pharmacistRepository;
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
        return "pharmacist/chat";
    }

    @GetMapping("/verEditarProductoFarmacista")
    public String verEditProduct(Model model){
        return "pharmacist/editproduct";
    }


    @GetMapping("/verNotificationsFarmacista")
    public String verNotifications(Model model){
        return "pharmacist/notifications";
    }

    @GetMapping("/posFarmacista")
    public String verPosPharmacist(Model model){
        return "pharmacist/pos";
    }

    @GetMapping("/productDetails")
    public String verProductDetails(Model model){
        return "pharmacist/product-details";
    }

    @GetMapping("/verProductListFarmacista")
    public String verProductList(Model model){
        return "pharmacist/productlist";
    }

    @GetMapping("/verProfileFarmacista")
    public String verPerfilPharmacist(Model model){
        /*
        int idPharmacist = Integer.parseInt((String) model.getAttribute("idUser"));
        Pharmacist pharmacist = new Pharmacist();
        pharmacist = pharmacistRepository.getByIdFarmacista(idPharmacist);
        model.addAttribute("sede", pharmacist.getSite());
        model.addAttribute("nombre", pharmacist.getName());
        model.addAttribute("apellido",pharmacist.getLastName());*/

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
        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            List<LotesValidosporMedicamentoDTO> listaLotesporMedicamento =  loteRepository.obtenerLotesValidosPorMedicamento(idMedicine);

            model.addAttribute("listaLotes",listaLotesporMedicamento);
            return "pharmacist/detallesMedicine";
        } else {
            return "redirect:/listaMedicamentosPharmacist";
        }
    }

}
