package com.example.proyectogrupo4_gtics.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PharmacistController {
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

}
