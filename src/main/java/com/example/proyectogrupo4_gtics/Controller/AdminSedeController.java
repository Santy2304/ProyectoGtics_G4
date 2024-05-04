package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.DoctorPorSedeDTO;
import com.example.proyectogrupo4_gtics.DTOs.FarmacistaPorSedeDTO;
import com.example.proyectogrupo4_gtics.DTOs.medicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import com.example.proyectogrupo4_gtics.Entity.ReplacementOrder;
import com.example.proyectogrupo4_gtics.Repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@SessionAttributes({"idUser"})
@Controller
public class AdminSedeController {
    final AdministratorRepository administratorRepository;
    final DoctorRepository doctorRepository;
    final PharmacistRepository pharmacistRepository;
    final MedicineRepository medicineRepository;
    final ReplacementOrderRepository replacementOrderRepository;

    public AdminSedeController(AdministratorRepository administratorRepository, DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, MedicineRepository medicineRepository, ReplacementOrderRepository replacementOrderRepository,
                               ReplacementOrderHasMedicineRepository replacementOrderHasMedicineRepository) {
        this.administratorRepository = administratorRepository;
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.medicineRepository = medicineRepository;
        this.replacementOrderRepository = replacementOrderRepository ;
    }


    @GetMapping("/listaDoctoresAdminSede")
    public String listDoctors(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser"));
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        model.addAttribute("listaDoctores", doctorRepository.listaDoctorPorSede(idAdministrator));
        return "admin_sede/doctorlist";
    }
    @PostMapping("/listaDoctoresAdminSede/buscar")
    public String buscarDoctores(Model model, RedirectAttributes attr, @RequestParam("nombre") String nombreDoc){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        List<DoctorPorSedeDTO> listaDoctors = doctorRepository.listaDoctorPorBuscador(nombreDoc,idAdministrator);
        model.addAttribute("listaDoctores", listaDoctors);

        return("/listaDoctoresAdminSede");
    }
    @GetMapping("/listaFarmacistaAdminSede")
    public String listPharmacist(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser") );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        model.addAttribute("listaFarmacista", pharmacistRepository.listaFarmacistaPorSede(idAdministrator));
        return "admin_sede/pharmacistlist";
    }
    @PostMapping("/listaFarmacistaAdminSede/buscar")
    public String buscarFarmacista(Model model,RedirectAttributes attr, @RequestParam("buscador") String buscador){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        List<FarmacistaPorSedeDTO> listFarmacista = pharmacistRepository.listaFarmacistaPorBuscador(buscador,idAdministrator);
        model.addAttribute("listaFarmacista", listFarmacista );
        return "/listaFarmacistaAdminSede";
    }
    @GetMapping("/verAddPharmacist")
    public String verAddPharmacist(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
            admin = administratorRepository.getByIdAdministrador(idAdministrator);
            model.addAttribute("sede", admin.getSite());
            model.addAttribute("nombre", admin.getName());
            model.addAttribute("apellido", admin.getLastName());
            if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
                model.addAttribute("rol","administrador");
            }

            return "admin_sede/addpharmacist";
        }


    @PostMapping("/agregarFarmacista")
    public String agregarFarmacista(
                                   Pharmacist pharmacist,
                                   Model model){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        if(admin.getState().equalsIgnoreCase("normal")){
            pharmacist.setSite(admin.getSite());
            pharmacist.setApprovalState("pendiente");
            pharmacist.setState("activo");
            pharmacist.setCreationDate(LocalDate.now());
            pharmacistRepository.save(pharmacist);
            return "redirect:/listaFarmacistaAdminSede";
        }else{
            return "/listaFarmacistaAdminSede";
        }

    }
    /*Linkear las demás vistas*/

    @GetMapping("/sessionAdmin")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idAdministrator){
        model.addAttribute("idUser",idAdministrator);
        return "redirect:/dashboardAdminSede";
    }
    @GetMapping("/dashboardAdminSede")
    public String verDashboard(Model model) {
        Administrator admin = new Administrator();
        String idAdministrator = (String) model.getAttribute("idUser");
        admin = administratorRepository.getByIdAdministrador(Integer.parseInt(idAdministrator));
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        return "admin_sede/dashboard";
    }

    @GetMapping("/inventarioAdminSede")
    public String verInventario(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }

        model.addAttribute("medicamentos", medicineRepository.listaMedicamentosPorSede(idAdministrator));

        return "admin_sede/inventario";
    }
    @PostMapping("/inventarioAdminSede/buscar")
    public String buscarMedicina(Model model, RedirectAttributes attr, @RequestParam("nameOrCategory") String palabra){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscador(palabra,idAdministrator);
        if(listMedicine.isEmpty()){
            System.out.println("No se encontro medicina que contenga esa palabra");
        }
        model.addAttribute("medicamentos", listMedicine);
        return "admin_sede/inventario";
    }

    @GetMapping("/verListaReposicion")
    public String listaReposicion(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }

        return "admin_sede/listaReposicion";
    }

    @GetMapping("/verSolicitudReposicion")
    public String solicitudReposicion(Model model){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }

        model.addAttribute("listaMedicamentosBS", medicineRepository.listaMedicamentosPocoStock(idAdministrator));

        return "admin_sede/reposicion";
    }

    @PostMapping("/solicitudReposicion")
    public String generarReposicion(Model model ,@RequestParam("idMedicine") int idMedicamento, @RequestParam("cantidad") int cantidad, ReplacementOrder replacementOrder){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        return("redirect:/verListaReposicion");
    }

    @GetMapping("/verNotificaciones")
    public String notificaciones(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }

        return "admin_sede/notifications";
    }
    @GetMapping("/verPerfil")
    public String profile(Model model){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);

        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("email", admin.getEmail());
        model.addAttribute("dni", admin.getDni());
        if(!(admin.getState().equalsIgnoreCase("baneado"))){
            model.addAttribute("rol", "Administrador");
        }else{
            model.addAttribute("rol", "Se encuentra baneado");//opcional solo para probar
        }
        model.addAttribute("sede", admin.getSite());
        return "admin_sede/profile";
    }
}
