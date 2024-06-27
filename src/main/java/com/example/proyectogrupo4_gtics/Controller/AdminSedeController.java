package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.DoctorPorSedeDTO;
import com.example.proyectogrupo4_gtics.DTOs.lotesPorReposicion;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SessionAttributes({"idUser", "sede"})
@Controller
@RequestMapping("/adminSede")
public class AdminSedeController {
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
    public AdminSedeController(AdministratorRepository administratorRepository, DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, MedicineRepository medicineRepository, ReplacementOrderRepository replacementOrderRepository,
                               ReplacementOrderHasMedicineRepository replacementOrderHasMedicineRepository ,
                               LoteRepository loteRepository,UserRepository userRepository,
                               TrackingRepository trackingRepository,NotificationsRepository notificationsRepository,
                               SiteRepository siteRepository) {
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
    }


    @GetMapping("/cambioObligatorio")
    public String cambioObligatorio( Model model){

        return "admin_sede/changePasswordFirstTime";
    }
    @PostMapping("/efectuarCambioContrasena")
    public String efectuarCambio(@RequestParam("confirmarContrasena") String password,Model model, RedirectAttributes attr, HttpSession httpSession){
        Administrator administrator = (Administrator) httpSession.getAttribute("usuario");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        userRepository.actualizarPassword(encryptedPassword,administrator.getEmail());
        administratorRepository.updateChangePasswrod(administrator.getIdAdministrador());
        return "redirect:dashboardAdminSede";
    }


    //Doctores por sede
    @GetMapping("/listaDoctores")
    public String listDoctors(Model model , HttpSession session ){
        ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        int idAdministrator = ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("admin", admin);
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        model.addAttribute("listaDoctores", doctorRepository.listaDoctorPorSede(idAdministrator));
        return "admin_sede/doctorlist";
    }
    //Buscador de adminSede
    @PostMapping("/listaDoctores/buscar")
    public String buscarDoctores(Model model, RedirectAttributes attr, @RequestParam("nombre") String nombreDoc, HttpSession session){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        List<DoctorPorSedeDTO> listaDoctors = doctorRepository.listaDoctorPorBuscador(nombreDoc,idAdministrator);
        model.addAttribute("listaDoctores", listaDoctors);
        return"/listaDoctoresAdminSede";
    }
    //Lista Farmacistas por sede junto a las solicitudes de farmacistas de las sedes
    @GetMapping("/listaFarmacista")
    public String listPharmacist(Model model, HttpSession session) {
        //CONVERSAR CON SANTIAGO SOBRE LA NECESIDAD DE SOLO LISTAR LAS SOLICITUDES NO ATENDIDAS
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        model.addAttribute("listaFarmacista", pharmacistRepository.listaFarmacistaPorSede(idAdministrator));
        if (admin.getSite().equals("Pando 1")){
            model.addAttribute("listaSolicitudes",pharmacistRepository.listarSolicitudesFarmacistaPando1());
        }else{
            if(admin.getSite().equals("Pando 2")){
                model.addAttribute("listaSolicitudes",pharmacistRepository.listarSolicitudesFarmacistaPando2());
            }else{
                if (admin.getSite().equals("Pando 3")){
                    model.addAttribute("listaSolicitudes",pharmacistRepository.listarSolicitudesFarmacistaPando3());
                }else{
                    if (admin.getSite().equals("Pando 4")){
                        model.addAttribute("listaSolicitudes",pharmacistRepository.listarSolicitudesFarmacistaPando4());
                    }
                }
            }
        }
        return "admin_sede/pharmacistlist";
    }
    //Salta a la vista para crear farmacista (no requiere un validación)
    @GetMapping(value = {"/verAddPharmacist",""})
    public String verAddPharmacist(@ModelAttribute("farmacista") Pharmacist pharmacist, Model model,RedirectAttributes redirectAttributes, HttpSession session) {
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();


        if (redirectAttributes != null) {
            String errorNombre = (String) redirectAttributes.getFlashAttributes().get("errorNombre");
            String errorApellido = (String) redirectAttributes.getFlashAttributes().get("errorApellido");
            String errorDistrito = (String) redirectAttributes.getFlashAttributes().get("errorDistrito");
            String errorEmail = (String) redirectAttributes.getFlashAttributes().get("errorEmail");
            String errorDNI = (String) redirectAttributes.getFlashAttributes().get("errorDNI");
            String errorCODE = (String) redirectAttributes.getFlashAttributes().get("errorCODE");


            if (errorNombre != null) {
                model.addAttribute("errorNombre", errorNombre);
            }
            if (errorDistrito != null) {
                model.addAttribute("errorDireccion", errorDistrito);
            }
            if (errorApellido != null) {
                model.addAttribute("errorApellido", errorApellido);
            }

            if (errorEmail != null) {
                model.addAttribute("errorEmail", errorEmail);
            }

            if (errorDNI != null) {
                model.addAttribute("errorDNI", errorDNI);
            }

            if (errorCODE != null) {
                model.addAttribute("errorCODE", errorCODE);
            }



        }


            admin = administratorRepository.getByIdAdministrador(idAdministrator);
            model.addAttribute("sede", admin.getSite());
            model.addAttribute("nombre", admin.getName());
            model.addAttribute("apellido", admin.getLastName());
            model.addAttribute("photo", admin.getPhoto());
            if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
                model.addAttribute("rol","administrador");
            }
            return "admin_sede/addpharmacist";
    }

    //Función para determinar la unicidad del DNI del farmacista
    public boolean verificarDNI(String dni) {
        List<Pharmacist> listaFarmacista = pharmacistRepository.findAll();
        for (Pharmacist pharmacist : listaFarmacista) {
            if (pharmacist.getDni().equals(dni)) {
                return true;
            }
        }
        return false;
    }
    public boolean verificarCodigo(String codigo) {
        List<Pharmacist> listaFarmacista = pharmacistRepository.findAll();
        for (Pharmacist pharmacist : listaFarmacista) {
            if (pharmacist.getCode().equals(codigo)) {
                return true;
            }
        }
        return false;
    }


    //Agregar farmacista faltan validaciones correspondientes
    @PostMapping("/agregarFarmacista")
    public String agregarFarmacista(@RequestParam("foto") MultipartFile imagen, @ModelAttribute("farmacista")Pharmacist pharmacist , Model model, RedirectAttributes attributes, RedirectAttributes attr , HttpSession session){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
            pharmacist.setSite(admin.getSite());
            pharmacist.setApprovalState("pendiente");
            pharmacist.setRequestDate(LocalDate.now());
            pharmacist.setState("En espera");



        boolean fallo = false;
        if (pharmacist.getName()==null || pharmacist.getName().trim().isEmpty()) {
            fallo = true;
            attr.addFlashAttribute("errorNombre", "Coloque un nombre");

        }

        if (pharmacist.getLastName()==null || pharmacist.getLastName().isEmpty()) {
            fallo = true;
            attr.addFlashAttribute("errorApellido", "Coloque un apellido");

        }

        if (pharmacist.getEmail()==null || pharmacist.getEmail().isEmpty()) {
            fallo = true;
            attr.addFlashAttribute("errorEmail", "Coloque un correo");

        }

        if (pharmacist.getDistrit()==null || pharmacist.getDistrit().isEmpty()) {
            fallo = true;
            attr.addFlashAttribute("errorDistrito", "Coloque un distrito");

        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(pharmacist.getDni());
        if (!matcher.matches() || pharmacist.getDni().length() != 8 || verificarDNI(pharmacist.getDni())) {
            attr.addFlashAttribute("errorDNI", "El DNI debe ser de 8 dígitos y único");
            fallo=true;
        }

        Pattern pattern1 = Pattern.compile("\\d+");
        Matcher matcher1 = pattern.matcher(pharmacist.getCode());
        if (!matcher1.matches() || pharmacist.getCode().length() != 8 || verificarCodigo(pharmacist.getCode())) {
            attr.addFlashAttribute("errorCODE", "El código debe ser de 8 dígitos y único");
            fallo=true;
        }



        if (fallo) {
                return "redirect:verAddPharmacist";
        } else {
                if (verificarDNI(pharmacist.getDni())) { //Cuando el DNI ya está en base de datos
                    model.addAttribute("error", "El DNI ingresado ya existe");
                    return "admin_sede/addpharmacist";
                } else { //Cuando se ingresa un nuevo DNI
                    if(!imagen.isEmpty()){
                        //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");

                        //String rutaAbsoluta = directorioImagenPerfil.toFile().getAbsolutePath();
                        //NUBE
                        //String rutaAbsoluta = "//SaintMedic//imagenes";
                        //LOCAL
                        String rutaAbsoluta = "//SaintMedic//imagenes";

                        String fileOriginalName = imagen.getOriginalFilename();
                        try {
                            byte[] bytesImgMedicine = imagen.getBytes();

                            long fileSize = imagen.getSize();
                            long maxFileSize = 5 * 1024 * 1024;

                            String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                            if (fileSize > maxFileSize) {
                                model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                                return "admin_sede/addpharmacist";
                            }
                            if (
                                    !fileExtension.equalsIgnoreCase(".jpg") &&
                                            !fileExtension.equalsIgnoreCase(".png") &&
                                            !fileExtension.equalsIgnoreCase(".jpeg")
                            ) {
                                model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                                return "admin_sede/addpharmacist";
                            }

                            Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                            Files.write(rutaCompleta, bytesImgMedicine);
                            pharmacist.setPhoto(imagen.getOriginalFilename());

                            attributes.addFlashAttribute("msg", "Farmacista agregado correctamente");
                            pharmacist.setChangePassword(false);
                            pharmacistRepository.save(pharmacist);
                            return "redirect:listaFarmacista";
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }else{
                        model.addAttribute("errorImage","Se debe ingresar una foto");
                        return "admin_sede/addpharmacist";
                    }
                }
        }
    }
    //Faltan agregar validaciones de editar farmacista por sede
    @GetMapping("/editFarmacista")
    public String verEditarFarmacista(@ModelAttribute("farmacista") Pharmacist pharmacist, @RequestParam("idFarmacista") int idFarmacista , Model model) {

        Optional<Pharmacist> optionalPharmacist = pharmacistRepository.findById(idFarmacista);
        if(optionalPharmacist.isPresent()){
            pharmacist = optionalPharmacist.get();
            model.addAttribute("farmacista", pharmacist);
            return "admin_sede/editFarmacist";
        }else{
            return "redirect:listaFarmacista";
        }
    }
    //Se solicita a superadmin agregar al farmacista
    @PostMapping("/saveChanges")
    public String editarFarmacista(@RequestParam("foto") MultipartFile imagen,@ModelAttribute("farmacista") @Valid Pharmacist pharmacist, BindingResult bindingResult, Model model, RedirectAttributes attributes , HttpSession session){
        if(bindingResult.hasErrors()){
            return "admin_sede/editFarmacist";
        } else {
            if(!imagen.isEmpty()) {
                //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");

                //String rutaAbsoluta = directorioImagenPerfil.toFile().getAbsolutePath();

                String rutaAbsoluta = "//SaintMedic//imagenes";

                String fileOriginalName = imagen.getOriginalFilename();
                try {
                    byte[] bytesImgMedicine = imagen.getBytes();

                    long fileSize = imagen.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));

                    if (fileSize > maxFileSize) {
                        model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                        return "admin_sede/editFarmacist";
                    }
                    if (
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                                    !fileExtension.equalsIgnoreCase(".png") &&
                                    !fileExtension.equalsIgnoreCase(".jpeg")
                    ) {
                        model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                        return "admin_sede/editFarmacist";
                    }

                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImgMedicine);
                    pharmacist.setState("activo");
                    attributes.addFlashAttribute("msg", "Farmacista actualizado correctamente");
                    pharmacistRepository.updateDatosPorId(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getEmail(), (administratorRepository.findById( ((Administrator)session.getAttribute("usuario")).getIdAdministrador()).get().getSite()), pharmacist.getState(), pharmacist.getDistrit(),imagen.getOriginalFilename(), pharmacist.getIdFarmacista());
                    return "redirect:listaFarmacista";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                model.addAttribute("imageError", "Debe añadir una imagen");
                return "admin_sede/editFarmacist";
            }
        }
    }
    //Inicia sesion de admin de sede
    @GetMapping("/sessionAdmin")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idAdministrator){
        model.addAttribute("idUser",idAdministrator);
        model.addAttribute("sede", (administratorRepository.getByIdAdministrador(Integer.parseInt(idAdministrator)).getSite()  ));
        model.addAttribute("photo", (administratorRepository.getByIdAdministrador(Integer.parseInt(idAdministrator)).getPhoto()));
        return "redirect:dashboardAdminSede";
    }
    //Se ve el dashboard de admin de sede
    @GetMapping("/dashboardAdminSede")
    public String verDashboard(Model model , HttpSession session ) {
        Administrator admin = new Administrator();
        String idAdministrator = "" + ((Administrator) session.getAttribute("usuario")).getIdAdministrador();
        admin = administratorRepository.getByIdAdministrador(Integer.parseInt(idAdministrator));
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());


        Double ganancia1 = medicineRepository.gananciaTotalPando1();
        int cantVend1 = medicineRepository.cantMedicamentosVendidosPando1();


        if(cantVend1<1){
            cantVend1 = 0;
            ganancia1=0.00;
        }
        model.addAttribute("ganancia1",ganancia1);
        model.addAttribute("cantVend1", cantVend1);


        ///Solo se va a quedar estos códigos, los demás querys son de prueba, porque la bd no está llena en las demás sedes y hay error con el código

        Double gananciaPorSede = medicineRepository.gananciaTotalPorSede(admin.getSite());
        int cantVendPorSede = medicineRepository.cantMedicamentosVendidosPorSede(admin.getSite());


        model.addAttribute("gananciaPorSede",gananciaPorSede);
        model.addAttribute("cantPorSede", cantVendPorSede);

        /*
        Double ganancia1 = medicineRepository.gananciaTotalPando1();
        Double ganancia2 = medicineRepository.gananciaTotalPando2();
        Double ganancia3 = medicineRepository.gananciaTotalPando3();
        Double ganancia4 = medicineRepository.gananciaTotalPando4();

        double valorGanancia1 = (ganancia1 != null) ? ganancia1 : 0.0;
        double valorGanancia2 = (ganancia2 != null) ? ganancia2 : 0.0;
        double valorGanancia3 = (ganancia3 != null) ? ganancia3 : 0.0;
        double valorGanancia4 = (ganancia4 != null) ? ganancia4 : 0.0;

        int cantVend1 = medicineRepository.cantMedicamentosVendidosPando1();
        int cantVend2 = medicineRepository.cantMedicamentosVendidosPando2();
        int cantVend3 = medicineRepository.cantMedicamentosVendidosPando3();
        int cantVend4 = medicineRepository.cantMedicamentosVendidosPando4();

        if(cantVend1<1){
            cantVend1 = 0;
        }
        if(cantVend2<1){
            cantVend2 = 0;
        }
        if(cantVend3<1){
            cantVend3 = 0;
        }
        if(cantVend4<1){
            cantVend4 = 0;
        }
        model.addAttribute("gananciaT",medicineRepository.gananciaTotal());
        model.addAttribute("ganancia1",valorGanancia1);
        model.addAttribute("ganancia2",valorGanancia2);
        model.addAttribute("ganancia3",valorGanancia3);
        model.addAttribute("ganancia4",valorGanancia4);
        model.addAttribute("medVendidosT", medicineRepository.cantMedicamentosVendidos());
        model.addAttribute("cantVend1", cantVend1);
        model.addAttribute("cantVend2", cantVend2);
        model.addAttribute("cantVend3", cantVend3);
        model.addAttribute("cantVend4", cantVend4);

         */
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        return "admin_sede/dashboard";
    }
    //Se listan medicamentos
    @GetMapping("/inventario")
    public String verInventario(Model model , HttpSession session) {
        int idAdministrator = ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        model.addAttribute("photo",(administratorRepository.getByIdAdministrador(idAdministrator)).getPhoto());
        model.addAttribute("medicamentos", medicineRepository.listaMedicamentosPorSede(idAdministrator));
        return "admin_sede/inventario";
    }
    //Filtrado para la busqueda del medicamento
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
    //Filtrado de medicamentos por sede
    @PostMapping("/inventarioBusca")
    public String buscarMedicina(Model model, RedirectAttributes attr, Busqueda busqueda , HttpSession session
    ){
        //VALIDADO EN CUALQUIER CASO ENVIA ALGO
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        String nombre = busqueda.getNombre();
        String category = busqueda.getCategoria();
        model.addAttribute("medicamentos", medicineRepository.listaMedicamentosPorSede(idAdministrator));
        if(!nombre.equals("") && !category.equals("Elegir por tipo")){
            List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorDosParametros( busqueda.getNombre(),busqueda.getCategoria() , idAdministrator);
            model.addAttribute("medicamentos", listMedicine);
        }else{
            if(!(nombre.equals("") && category.equals("Elegir por tipo"))) {
                if (!nombre.equals("")) {
                    List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorNombre(busqueda.getNombre(), idAdministrator);
                    model.addAttribute("medicamentos", listMedicine);
                }
                if (!category.equals("Elegir por tipo")) {
                    List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorCategory(busqueda.getCategoria(), idAdministrator);
                    model.addAttribute("medicamentos", listMedicine);
                }
            }else{
                List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorDosParametros(busqueda.getNombre(),busqueda.getCategoria() , idAdministrator);
                model.addAttribute("medicamentos", listMedicine);
            }
        }
        return "admin_sede/inventario";
    }
    //Se ve lista de pedidos de reposición
    @GetMapping("/verListaReposicion")
    public String listaReposicion(Model model , HttpSession session ) {
        int idAdministrator = ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        int ola  = ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        model.addAttribute("listaReposicion" , replacementOrderRepository.getReplacementOrderBySede(  ((administratorRepository.findById(ola)).get()).getSite() ));
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
    public String solicitudReposicion(Model model, HttpSession session){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        model.addAttribute("medicamentos", medicineRepository.listaMedicamentosPorSede(idAdministrator));
        model.addAttribute("listaMedicamentosBS", medicineRepository.listaMedicamentosPocoStock(idAdministrator));
        return "admin_sede/generarPedidoReposicion";
    }

    @PostMapping("/solicitudReposicion")
    public String generarReposicion(Model model ,@RequestParam("idMedicine") int idMedicamento, @RequestParam("cantidad") int cantidad, ReplacementOrder replacementOrder , HttpSession session){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        return("redirect:verListaReposicion");
    }

    @GetMapping("/verNotificacionesAdminSede")
    public String notificaciones(Model model, HttpSession session) {
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("photo", admin.getPhoto());
        if(!(admin.getState().equalsIgnoreCase("baneado") || admin.getState().equalsIgnoreCase("eliminado"))){
            model.addAttribute("rol","administrador");
        }
        model.addAttribute("listaNotificaciones",notificationsRepository.notificacionesSede(admin.getSite()));
        return "admin_sede/notifications";
    }
    @GetMapping("/verPerfilAdminSede")
    public String profile(Model model, HttpSession session
    ){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("email", admin.getEmail());
        model.addAttribute("dni", admin.getDni());
        model.addAttribute("rol", "Administrador");
        model.addAttribute("sede", admin.getSite());
        model.addAttribute("photo", admin.getPhoto());
        model.addAttribute("admin", admin);
        return "admin_sede/profile";
    }

    @PostMapping("/editarPerfilAdminSede")
    public String editProfilePhoto(@RequestParam("adminFile") MultipartFile imagen, Model model,  RedirectAttributes attr, HttpSession session){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        Administrator admin = administratorRepository.getByIdAdministrador(idAdministrator);

            if (imagen.isEmpty()) {
                model.addAttribute("imageError", "Debe agregar una imagen");
                return "superAdmin/perfil";
            } else {

                //Path directorioImagenPerfil = Paths.get("src//main//resources//static//assets_superAdmin//ImagenesPerfil");

                //String rutaAbsoluta = directorioImagenPerfil.toFile().getAbsolutePath();
                String rutaAbsoluta = "//SaintMedic//imagenes";

                try {
                    byte[] bytesImgPerfil = imagen.getBytes();
                    String fileOriginalName = imagen.getOriginalFilename();

                    long fileSize = imagen.getSize();
                    long maxFileSize = 5 * 1024 * 1024;

                    String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                    if (fileSize > maxFileSize) {
                        model.addAttribute("imageError", "El tamaño de la imagen excede a 5MB");
                        return "admin_sede/profile";
                    }
                    if (
                            !fileExtension.equalsIgnoreCase(".jpg") &&
                                    !fileExtension.equalsIgnoreCase(".png") &&
                                    !fileExtension.equalsIgnoreCase(".jpeg")
                    ) {
                        model.addAttribute("imageError", "El formato de la imagen debe ser jpg, jpeg o png");
                        return "admin_sede/profile";
                    }

                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImgPerfil);
                    admin.setPhoto(imagen.getOriginalFilename());
                    attr.addFlashAttribute("msg", "Foto de perfil actualizado correctamente");
                    return "redirect:verPerfilAdminSede";

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

    }
    class IdPedidoReposicion{
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
    @RequestMapping("/verDetalleRepoMedicamentos")
    @ResponseBody
    public ArrayList<String> hola(@RequestParam("idPedidoReposicion") String idPedidoReposicion ) throws JsonProcessingException {
        System.out.println("HOLAAAA LLEGUE A VER DETALLE DE REPOSICION");
        System.out.println(idPedidoReposicion);
        List<lotesPorReposicion> ola  = loteRepository.getLoteByReplacementOrderId(Integer.parseInt(idPedidoReposicion));
        ArrayList<String> response =  new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        for(lotesPorReposicion lotesPorReposicion : ola){
            // Convertir el objeto a JSON
            try {
                json = objectMapper.writeValueAsString(lotesPorReposicion);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.println(json);
            response.add(json);
        }
        return response;
    }
    @GetMapping("/cancelarPedidoReposicion")
    public String cancelarPedidoReposicion(@RequestParam("id") String id ){
        //Validamos q su estado sea uno anterior al de empaquetado
        int idRepo = Integer.parseInt(id);
        Optional<ReplacementOrder> r = replacementOrderRepository.findById(idRepo);
        if(r.isPresent() && (r.get().getTrackingState().equals("Solicitado") || r.get().getTrackingState().equals("En Proceso") )) {
            //Primero debemos borrar todos los lotes que se le asignaron a ese pedido de reposicion
            List<lotesPorReposicion> l = loteRepository.getLoteByReplacementOrderId(idRepo);
            for(lotesPorReposicion aux:  l ){
                loteRepository.deleteById(aux.getId());
            }
            replacementOrderRepository.deleteById(r.get().getIdReplacementOrder());
        }
        return "redirect:verListaReposicion";
    }
    public static class ReplacamenteOrderEdit{
        private ArrayList<Object> datos;

        public ArrayList<Object> getDatos() {
            return datos;
        }

        public void setDatos(ArrayList<Object> datos) {
            this.datos = datos;
        }

    }
    @PostMapping("/editarPedidoReposicion")
    public String editarPedidoReposicion(@RequestBody String cuerpo) throws JsonProcessingException{
        System.out.println(cuerpo);
        ObjectMapper objectMapper = new ObjectMapper();
        ReplacamenteOrderEdit datos = objectMapper.readValue(cuerpo, ReplacamenteOrderEdit.class);
        String aux ;
        //ACA EMPIEZO
        ArrayList<String> datosAux = new ArrayList<>();
        for (Object u : datos.getDatos()) {
            datosAux.add("" + u);
        }
        if( replacementOrderRepository.findById(Integer.parseInt( datosAux.get(datos.getDatos().size()-1))).isPresent() &&(replacementOrderRepository.findById(Integer.parseInt( datosAux.get(datos.getDatos().size()-1))).get().getTrackingState().equals("Solicitado") ||
                replacementOrderRepository.findById(Integer.parseInt( datosAux.get(datos.getDatos().size()-1))).get().getTrackingState().equals("En Proceso") )
        ) {
            List<lotesPorReposicion> ola = loteRepository.getLoteByReplacementOrderId(Integer.parseInt(datosAux.get(datos.getDatos().size() - 1)));
            int countError = 0;
            int countErrorNegative = 0;

            for (Object u : datos.getDatos()) {
                try{
                    int oli =  Integer.parseInt(""+ u);
                    if(oli<=0){
                        countErrorNegative++;
                    }
                }catch(NumberFormatException err){
                    countError++;
                }
                aux = "" + u;
            }
            int count = 0;
            if(countError==0  && countErrorNegative==0) {
                for (lotesPorReposicion l : ola) {
                    loteRepository.actualizarCantidadInicial(l.getId(), Integer.parseInt(datosAux.get(count)));
                    count++;
                }
            }
        }
        return "redirect:verListaReposicion";
    }

    @PostMapping("/generarReposicionBusca")
    public String buscarMedicinaEnGenerarReposicionAdminSede(Model model, RedirectAttributes attr, Busqueda busqueda , HttpSession session
    ){
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        String nombre = busqueda.getNombre();
        String category = busqueda.getCategoria();
        System.out.println("Nombre  es " +  nombre);
        System.out.println("category  es " +  category);
        if(!nombre.equals("") && !category.equals("Elegir por tipo")){
            System.out.println("Hola 1");
            List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoDosParametros(idAdministrator, busqueda.getNombre(),busqueda.getCategoria());
            model.addAttribute("listaMedicamentosBS", listMedicine);
        }else{
            if(!(nombre.equals("") && category.equals("Elegir por tipo"))) {
                if (!nombre.equals("")) {
                    System.out.println("Hola 2");
                    List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoNombre(idAdministrator, busqueda.getNombre());
                    model.addAttribute("listaMedicamentosBS", listMedicine);
                }
                if (!category.equals("Elegir por tipo")) {
                    System.out.println("Hola 3");
                    List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoCategory(idAdministrator, busqueda.getCategoria());
                    model.addAttribute("listaMedicamentosBS", listMedicine);
                }
            }else{
                System.out.println("Hola 4");
                List<MedicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoDosParametros(idAdministrator, busqueda.getNombre(),busqueda.getCategoria());
                model.addAttribute("listaMedicamentosBS", listMedicine);
            }
        }
        return "admin_sede/generarPedidoReposicion";
    }


    public static class ReplacamenteOrderData{
        private ArrayList<Object> cantidad;
        private ArrayList<Object> ids;
        private Object date;

        public Object getDate() {
            return date;
        }

        public void setDate(Object date) {
            this.date = date;
        }

        public ArrayList<Object> getCantidad() {
            return cantidad;
        }

        public void setCantidad(ArrayList<Object> cantidad) {
            this.cantidad = cantidad;
        }

        public ArrayList<Object> getIds() {
            return ids;
        }

        public void setIds(ArrayList<Object> ids) {
            this.ids = ids;
        }
    }
    //RECONTRA VALIDADO
    @RequestMapping ("/generarReposicion")
    @ResponseBody
    public Map<String,String> CreateReplacementOrder( @RequestBody String cuerpo , Model  model , HttpSession session
    ) throws JsonProcessingException {
        int idAdministrator =  ((Administrator)session.getAttribute("usuario")).getIdAdministrador();
        //Validar que se ingresen números en lugar de Strings
        //validar que solo sean medicamentos q esten por debajo de 25 de Stock
        // validar q ninguno este en cero
        //Validar q no este vacío y validar que sean menos de 10 medicamentos distintos
        //Validar la fecha que sea mayor a la que en la q nos encontramos
        Map<String,String> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ReplacamenteOrderData data = objectMapper.readValue(cuerpo, ReplacamenteOrderData.class);
        System.out.println(data.getDate());
        ArrayList<Object> ids = data.getIds();
        ArrayList<Object> cantidad = data.getCantidad();
        //Validar que se ingresen números en lugar de Strings
        int errorCount= 0;
        for(int idx = 0 ;  idx < cantidad.size() ; idx++){
            try{
                int ola = Integer.parseInt("" + ids.get(idx));
                int ola2 = Integer.parseInt("" + cantidad.get(idx));
            }catch(NumberFormatException er){
                errorCount++;
            }
        }
        boolean errorStrings = errorCount!=0;
        //validar que solo sean medicamentos q esten por debajo de 25 de Stock
        List<MedicamentosPorSedeDTO> listaMedicinasPocoStock = medicineRepository.listaMedicamentosPocoStock(idAdministrator);
        int auxCount2 = 0;
        for(int idx = 0 ;  idx < cantidad.size() ; idx++) {
            try{
                int ola = Integer.parseInt("" + ids.get(idx));
                int auxCount = 0;
                for(MedicamentosPorSedeDTO m : listaMedicinasPocoStock){
                    if( m.getIdMedicine() == ola) {
                        auxCount++;
                    }
                }
                if(auxCount==1){
                    auxCount2++;
                }
            }catch(NumberFormatException er){
                System.out.println("Hola");
            }
        }
        boolean errorMedicamentosNoCoinciden = auxCount2 != cantidad.size();
        // validar q ninguno este en cero
        int counterCero = 0;
        for(int idx = 0 ;  idx < cantidad.size() ; idx++){
            try{
                int id = Integer.parseInt(""+ids.get(idx));
                int quantity = Integer.parseInt(""+cantidad.get(idx));
                if(quantity==0){
                    counterCero++;
                }
            }catch(NumberFormatException er){
                System.out.println("Hola");
            }
        }
        boolean diferenteCero = counterCero != cantidad.size();
        //Validar q no este vacío y validar que sean menos de 10 medicamentos distintos
        boolean vacio = cuerpo.isEmpty();
        int counterNoCero = 0;
        for(int idx = 0 ;  idx < cantidad.size() ; idx++){
            try{
                int id = Integer.parseInt(""+ids.get(idx));
                int quantity = Integer.parseInt(""+cantidad.get(idx));
                if(quantity!=0){
                    counterNoCero++;
                }
            }catch(NumberFormatException er){
                System.out.println("Hola");
            }
        }
        boolean menorADiez = counterNoCero<=10;
        //Validar la fecha que sea mayor a la que en la q nos encontramos
        LocalDate ola = LocalDate.parse((String)data.getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate fechaActual = LocalDate.now();
        long diferenciaEnDias = ChronoUnit.DAYS.between(fechaActual, ola);
        boolean reposicionPermitida = diferenciaEnDias >= 7;
        if(!vacio){
            if(reposicionPermitida){
                if(!errorStrings){
                    if(!errorMedicamentosNoCoinciden){
                        if(diferenteCero){
                            if(menorADiez){
                                ArrayList<String> idsString  = new ArrayList<String>();
                                ArrayList<String> cantidadString  = new ArrayList<String>();
                                for(Object aux: ids){
                                    idsString.add(""+aux );
                                }
                                for(Object aux: cantidad){
                                    cantidadString.add(""+aux );
                                }
                                System.out.println(idsString);
                                System.out.println(cantidadString);
                                ReplacementOrder r = new ReplacementOrder();
                                r.setTrackingState("Solicitado");
                                r.setSite((String) model.getAttribute("sede"));
                                r.setReleaseDate(LocalDate.parse((String)data.getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                r.setAdministrator(administratorRepository.getByIdAdministrador( ((Administrator)session.getAttribute("usuario")).getIdAdministrador()));
                                //r.setIdReplacementOrder();
                                Tracking tracking = new Tracking();
                                tracking.setSolicitudDate(LocalDateTime.now());
                                tracking.setEnProcesoDate(LocalDateTime.now().plusMinutes(1));
                                tracking.setEmpaquetadoDate(LocalDateTime.now().plusMinutes(2));
                                tracking.setEnRutaDate(LocalDateTime.now().plusMinutes(3));
                                tracking.setEntregadoDate(LocalDateTime.now().plusMinutes(4));
                                trackingRepository.save(tracking);
                                r.setIdTracking(tracking);
                                ReplacementOrder newReplacementOrder = replacementOrderRepository.save(r);
                                //Creamos los lotes asignados a cada orden
                                String quantity  ;
                                String id ;
                                for(int i = 0 ; i<cantidadString.size() ;  i++){
                                    if(Integer.parseInt(cantidadString.get(i))>0) {
                                        quantity = cantidadString.get(i);
                                        id = idsString.get(i);
                                        Lote lote = new Lote();
                                        lote.setMedicine(medicineRepository.findById(Integer.parseInt(id)).get());
                                        //lote.setIdLote();
                                        lote.setSite((String) model.getAttribute("sede"));
                                        lote.setExpireDate(LocalDate.now());
                                        lote.setExpire(false);
                                        lote.setStock(Integer.parseInt(quantity));
                                        lote.setReplacementOrder(newReplacementOrder);
                                        lote.setVisible(true);
                                        lote.setInitialQuantity(Integer.parseInt(quantity));
                                        loteRepository.save(lote);
                                    }
                                }


                                response.put("error" ,"");
                                response.put("response" ,"/adminSede/SolicitudDeReposicionCreada?idReplacementOrder="+newReplacementOrder.getIdReplacementOrder());
                            }else{
                                response.put("error" ,"errorMenorADiez");
                            }
                        }else{
                            response.put("error" ,"errorDiferenteCero");
                        }
                    }else{
                        response.put("error" ,"errorMedicamentosNoCoinciden");
                    }
                }else{
                    response.put("error" ,"errorStrings");
                }
            }else{
                response.put("error" ,"ErrorReposicionPermitida");
            }
        }else{
            response.put("error" ,"ErrorVacio");
        }
        return response;
    }

    @GetMapping("/SolicitudDeReposicionCreada")
    public String SolicitudDeReposicionCreada(@RequestParam("idReplacementOrder") String idReplacementeOrder , Model model){
        model.addAttribute("idRepo", Integer.parseInt(idReplacementeOrder));
        return "admin_sede/TicketPedidoReposicion";
    }

    public class DataDoctorListBusca{
        String date;
        String nombre;
        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getNombre() {
            return nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
    //Filtrado de la lista de doctores
    @PostMapping("/doctorListBusca")
    public String doctorListBuscaAdminSede(DataDoctorListBusca d , Model model , HttpSession session){
        System.out.println(d.date);
        System.out.println(d.nombre);
        int idAdministrator =  ((Administrator) session.getAttribute("usuario")).getIdAdministrador() ;
        model.addAttribute("listaDoctores", doctorRepository.listaDoctorPorSede(idAdministrator));
        model.addAttribute("photo", (administratorRepository.getByIdAdministrador(idAdministrator).getPhoto()));
        return "/admin_sede/doctorlist";
    }
    //Cerrar Sesion
    @GetMapping("/CerrarSesionAdminSede")
    public String CerrarSesionAdminSede(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return "redirect:/inicioSesion";
    }

    @GetMapping("/verTrackingPersonal")
    public String verTrackingPersonal(@RequestParam("idReplacementOrder") int idReplacementeOrder , Model model){


        replacementOrderRepository.findById(idReplacementeOrder);

        Tracking tracking = replacementOrderRepository.findById(idReplacementeOrder).get().getIdTracking();

        model.addAttribute("idReplacement",idReplacementeOrder);
        model.addAttribute("Tracking",tracking);
        model.addAttribute("solicitudDate", tracking.getSolicitudDate().minusHours(5));
        model.addAttribute("enProcesoDate", tracking.getEnProcesoDate().minusHours(5));
        model.addAttribute("empaquetadoDate", tracking.getEmpaquetadoDate().minusHours(5));
        model.addAttribute("enRutaDate", tracking.getEnRutaDate().minusHours(5));
        model.addAttribute("entregadoDate", tracking.getEntregadoDate().minusHours(5));
        return "admin_sede/trackingPersonal";
    }


    @Scheduled(fixedRate = 30000) // Ejecuta la tarea cada 1/2 minuto
    public void changeTracking() {
        LocalDateTime now = LocalDateTime.now();
        List<ReplacementOrder> replacamenteOrders = replacementOrderRepository.findAll();

        for (ReplacementOrder replacementOrder : replacamenteOrders) {

            String tracking = replacementOrder.getTrackingState();

            Tracking trackingReal = replacementOrder.getIdTracking();

            switch (tracking){

                case ("Solicitado"):
                    if (trackingReal.getEnProcesoDate().isBefore(now)){
                        replacementOrderRepository.actualizarTracking("En Proceso",replacementOrder.getIdReplacementOrder());
                    }
                    break;

                case ("En Proceso"):
                    if (trackingReal.getEmpaquetadoDate().isBefore(now)){
                        replacementOrderRepository.actualizarTracking("Empaquetando",replacementOrder.getIdReplacementOrder());
                    }
                    break;

                case ("Empaquetando"):
                    if (trackingReal.getEnRutaDate().isBefore(now)){
                        replacementOrderRepository.actualizarTracking("En Ruta",replacementOrder.getIdReplacementOrder());
                    }
                    break;

                case ("En Ruta"):
                    if (trackingReal.getEntregadoDate().isBefore(now)){
                        replacementOrderRepository.actualizarTracking("Entregado",replacementOrder.getIdReplacementOrder());
                        Notifications notifications = new Notifications();
                        notifications.setDate(LocalDateTime.now());
                        notifications.setContent("La orden de reposición número "+replacementOrder.getIdReplacementOrder()+" ha llegado a la sede.");
                        notifications.setIdSite(siteRepository.encontrarSedePorNombre(replacementOrder.getSite()));
                        notificationsRepository.save(notifications);
                    }
                    break;

                default:
                    break;
            }
        }
    }
    @Scheduled(fixedRate = 3600000) // Ejecuta la tarea cada 10minutos
    public void notificacionesPorEscaso() {
        List<MedicamentosPorSedeDTO> listamedicamentosPocoStockPando1 = medicineRepository.listaMedicamentosPorSedeNoti("Pando 1");

        List<MedicamentosPorSedeDTO> listamedicamentosPocoStockPando2 = medicineRepository.listaMedicamentosPorSedeNoti("Pando 2");

        List<MedicamentosPorSedeDTO> listamedicamentosPocoStockPando3 = medicineRepository.listaMedicamentosPorSedeNoti("Pando 3");

        List<MedicamentosPorSedeDTO> listamedicamentosPocoStockPando4 = medicineRepository.listaMedicamentosPorSedeNoti("Pando 4");

        for (MedicamentosPorSedeDTO medicamento : listamedicamentosPocoStockPando1){
            Notifications notifications = new Notifications();
            notifications.setIdSite(siteRepository.encontrarSedePorNombre("Pando 1"));
            notifications.setContent("El medicamento "+medicamento.getNombreMedicamento() +" está por acabarse.");
            notifications.setDate(LocalDateTime.now());
            notificationsRepository.save(notifications);
        }

        for (MedicamentosPorSedeDTO medicamento : listamedicamentosPocoStockPando2){
            Notifications notifications = new Notifications();
            notifications.setIdSite(siteRepository.encontrarSedePorNombre("Pando 2"));
            notifications.setContent("El medicamento "+medicamento.getNombreMedicamento() +" está por acabarse.");
            notifications.setDate(LocalDateTime.now());
            notificationsRepository.save(notifications);
        }
        for (MedicamentosPorSedeDTO medicamento : listamedicamentosPocoStockPando3){
            Notifications notifications = new Notifications();
            notifications.setIdSite(siteRepository.encontrarSedePorNombre("Pando 3"));
            notifications.setContent("El medicamento "+medicamento.getNombreMedicamento() +" está por acabarse.");
            notifications.setDate(LocalDateTime.now());
            notificationsRepository.save(notifications);
        }

        for (MedicamentosPorSedeDTO medicamento : listamedicamentosPocoStockPando4){
            Notifications notifications = new Notifications();
            notifications.setIdSite(siteRepository.encontrarSedePorNombre("Pando 4"));
            notifications.setContent("El medicamento "+medicamento.getNombreMedicamento() +" está por acabarse.");
            notifications.setDate(LocalDateTime.now());
            notificationsRepository.save(notifications);
        }

    }
}
