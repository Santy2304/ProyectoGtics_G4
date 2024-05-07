package com.example.proyectogrupo4_gtics.Controller;


import com.example.proyectogrupo4_gtics.DTOs.LotesValidosporMedicamentoDTO;
import com.example.proyectogrupo4_gtics.Entity.Medicine;
import com.example.proyectogrupo4_gtics.Repository.LoteRepository;
import com.example.proyectogrupo4_gtics.Repository.MedicineRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class PharmacistController {
    final MedicineRepository medicineRepository;
    final LoteRepository loteRepository;

    public PharmacistController(MedicineRepository medicineRepository, LoteRepository loteRepository) {
        this.medicineRepository = medicineRepository;
        this.loteRepository = loteRepository;
    }
    @GetMapping("/verChatFarmacista")
    public String verChatPharmacist(){
        return "pharmacist/chat";
    }

    @GetMapping("/verEditarProductoFarmacista")
    public String verEditProduct(){
        return "pharmacist/editproduct";
    }

    @GetMapping("/verMedicinelistFarmacista")
    public String verMedicineList(){
        return "pharmacist/medicinelist";
    }

    @GetMapping("/verNotificationsFarmacista")
    public String verNotifications(){
        return "pharmacist/notifications";
    }

    @GetMapping("/posFarmacista")
    public String verPosPharmacist(){
        return "pharmacist/pos";
    }

    @GetMapping("/productDetails")
    public String verProductDetails(){
        return "pharmacist/product-details";
    }

    @GetMapping("/verProductListFarmacista")
    public String verProductList(){
        return "pharmacist/productlist";
    }

    @GetMapping("/verProfileFarmacista")
    public String verPerfilPharmacist(){
        return "pharmacist/profile";
    }

    @GetMapping("/listaMedicamentos")
    public String listaMedicamentosPharmacist(Model model) {
        model.addAttribute("medicnelist", medicineRepository.obtenerDatosMedicamentos());
        return "pharmacist/medicnelist";
    }

    @GetMapping("/detallesMedicamentos")
    public String detallesMedicamentos(@RequestParam("idMedicine") int idMedicine, Model model) {
        Optional<Medicine> medicineOptional = medicineRepository.findById(idMedicine);
        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            model.addAttribute("medicine", medicine);
            List<LotesValidosporMedicamentoDTO> listaLotesporMedicamento =  loteRepository.obtenerLotesValidosPorMedicamento(idMedicine);

            model.addAttribute("listaLotes",listaLotesporMedicamento);
            return "pharmacist/product-details";
        } else {
            return "redirect:/listaMedicamentosPharmacist";
        }
    }

}
