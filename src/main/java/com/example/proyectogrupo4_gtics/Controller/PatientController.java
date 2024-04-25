package com.example.proyectogrupo4_gtics.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatientController {
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
    public String verDatosPago(){
        return "pacient/datos_pago";
    }
    //No funciona bien
    @GetMapping("/verDetallePaciente")
    public String verDetalle(){
        return "pacient/detalle";
    }
    @GetMapping("/verGenerarOrdenCompraPaciente")
    public String verGenerarOrdenCompra(){
        return "pacient/generar_orden_compra";
    }
    @GetMapping("/verHistorialPaciente")
    public String verHistorial(){
        return "pacient/historial";
    }
    @GetMapping("/verNumeroOrdenPaciente")
    public String verNumeroOrdenPaciente(){
        return "pacient/numero_de_orden";
    }
    @GetMapping("/verPerfilPaciente")
    public String verPerfilPaciente(){
        return "pacient/perfil";
    }
    @GetMapping("/verPrincipalPaciente")
    public String verPrincipalPaciente(){
        return "pacient/principal";
    }
    //No funciona bien
    @GetMapping("/verProductListPaciente")
    public String verProductListPaciente(){
        return "pacient/productlist";
    }
    @GetMapping("/verSeleccionarSedePaciente")
    public String verSeleccionarSedePaciente(){
        return "pacient/seleccionarSede";
    }
    //Falta corregir
    @GetMapping("/verSingleProductPaciente")
    public String verSingleProductPaciente(){return "pacient/single_product";}
    @GetMapping("/verTrackingPaciente")
    public String verTrackingPaciente(){
        return "pacient/tracking";
    }
}
