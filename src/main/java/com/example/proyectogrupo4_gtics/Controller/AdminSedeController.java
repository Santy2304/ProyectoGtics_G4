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
import java.util.Optional;

@SessionAttributes({"idUser", "sede"})
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
    public String agregarFarmacista(Pharmacist pharmacist,Model model){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser"));
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }

            pharmacist.setPassword("default");
            pharmacist.setSite(admin.getSite());
            pharmacist.setApprovalState("pendiente");
            pharmacist.setRequestDate(LocalDate.now());
            pharmacistRepository.save(pharmacist);
            return "redirect:/listaFarmacistaAdminSede";

    }


    @GetMapping("/editFarmacistaAdminSede")
    public String verEditarFarmacista(@RequestParam("idFarmacista") int idFarmacista , Model model) {

        Optional<Pharmacist> pharmacist = pharmacistRepository.findById(idFarmacista);
        if(pharmacist.isPresent()){
            model.addAttribute("farmacista", pharmacist.get());
            return "admin_sede/editFarmacist";
        }else{
            return "redirect:/listaFarmacistaAdminSede";
        }
    }

    @PostMapping("/saveChangesFarmacista")
    public String editarFarmacista(Pharmacist pharmacist, Model model){
        pharmacistRepository.updateDatosPorId(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getEmail(), (administratorRepository.findById(Integer.parseInt( (String)model.getAttribute("idUser") )).get().getSite()), pharmacist.getState(), pharmacist.getDistrit(), pharmacist.getIdFarmacista());
        return "redirect:/listaFarmacistaAdminSede";
    }

    /*Linkear las demás vistas*/

    @GetMapping("/sessionAdmin")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idAdministrator){
        model.addAttribute("idUser",idAdministrator);
        model.addAttribute("sede", (administratorRepository.getByIdAdministrador(Integer.parseInt(idAdministrator)).getSite()  ));
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

    public class Busqueda{
        private String categoria;
        private String nombre;

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
    @PostMapping("/inventarioAdminSedeBusca")
    public String buscarMedicina(Model model, RedirectAttributes attr, Busqueda busqueda){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscador(busqueda.getNombre(),busqueda.getCategoria(),idAdministrator);
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

    @GetMapping("error505AdminSede")
    public String error(){
        return "pharmacist/error404";
    }

    @PostMapping("buscarMedicinaList")
    public String buscarMedicinaList(){


        return "admin_sede/inventario";
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
