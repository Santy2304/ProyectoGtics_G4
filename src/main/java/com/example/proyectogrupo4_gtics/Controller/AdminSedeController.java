package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.DTOs.DoctorPorSedeDTO;
import com.example.proyectogrupo4_gtics.DTOs.lotesPorReposicion;
import com.example.proyectogrupo4_gtics.DTOs.medicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Lote;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import com.example.proyectogrupo4_gtics.Entity.ReplacementOrder;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SessionAttributes({"idUser", "sede"})
@Controller
public class AdminSedeController {
    final AdministratorRepository administratorRepository;
    final DoctorRepository doctorRepository;
    final PharmacistRepository pharmacistRepository;
    final MedicineRepository medicineRepository;
    final ReplacementOrderRepository replacementOrderRepository;
    final LoteRepository loteRepository;
    public AdminSedeController(AdministratorRepository administratorRepository, DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, MedicineRepository medicineRepository, ReplacementOrderRepository replacementOrderRepository,
                               ReplacementOrderHasMedicineRepository replacementOrderHasMedicineRepository , LoteRepository loteRepository) {
        this.administratorRepository = administratorRepository;
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.medicineRepository = medicineRepository;
        this.replacementOrderRepository = replacementOrderRepository ;
        this.loteRepository =loteRepository;
    }
    //Doctores por sede
    @GetMapping("/listaDoctoresAdminSede")
    public String listDoctors(Model model){
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
    //Buscador de adminSede
    @PostMapping("/listaDoctoresAdminSede/buscar")
    public String buscarDoctores(Model model, RedirectAttributes attr, @RequestParam("nombre") String nombreDoc){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        List<DoctorPorSedeDTO> listaDoctors = doctorRepository.listaDoctorPorBuscador(nombreDoc,idAdministrator);
        model.addAttribute("listaDoctores", listaDoctors);
        return"/listaDoctoresAdminSede";
    }
    //Lista Farmacistas por sede junto a las solicitudes de farmacistas de las sedes
    @GetMapping("/listaFarmacistaAdminSede")
    public String listPharmacist(Model model) {
        //CONVERSAR CON SANTIAGO SOBRE LA NECESIDAD DE SOLO LISTAR LAS SOLICITUDES NO ATENDIDAS
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
    //Agregar farmacista faltan validaciones correspondientes
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
            pharmacist.setState("En espera");
            pharmacistRepository.save(pharmacist);
            return "redirect:/listaFarmacistaAdminSede";

    }
    //Faltan agregar validaciones de editar farmacista por sede
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
    //Se solicita a superadmin agregar al farmacista
    @PostMapping("/saveChangesFarmacista")
    public String editarFarmacista(Pharmacist pharmacist, Model model){
        pharmacist.setState("activo");
        pharmacistRepository.updateDatosPorId(pharmacist.getName(), pharmacist.getLastName(), pharmacist.getEmail(), (administratorRepository.findById(Integer.parseInt( (String)model.getAttribute("idUser") )).get().getSite()), pharmacist.getState(), pharmacist.getDistrit(), pharmacist.getIdFarmacista());
        return "redirect:/listaFarmacistaAdminSede";
    }
    //Inicia sesion de admin de sede
    @GetMapping("/sessionAdmin")
    public String iniciarSesion(Model model,  @RequestParam("idUser") String idAdministrator){
        model.addAttribute("idUser",idAdministrator);
        model.addAttribute("sede", (administratorRepository.getByIdAdministrador(Integer.parseInt(idAdministrator)).getSite()  ));
        return "redirect:/dashboardAdminSede";
    }
    //Se ve el dashboard de admin de sede
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
    //Se listan medicamentos
    @GetMapping("/inventarioAdminSede")
    public String verInventario(Model model) {
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
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
    @PostMapping("/inventarioAdminSedeBusca")
    public String buscarMedicina(Model model, RedirectAttributes attr, Busqueda busqueda){
        //VALIDADO EN CUALQUIER CASO ENVIA ALGO
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        String nombre = busqueda.getNombre();
        String category = busqueda.getCategoria();
        model.addAttribute("medicamentos", medicineRepository.listaMedicamentosPorSede(idAdministrator));
        if(!nombre.equals("") && !category.equals("Elegir por tipo")){
            List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorDosParametros( busqueda.getNombre(),busqueda.getCategoria() , idAdministrator);
            model.addAttribute("medicamentos", listMedicine);
        }else{
            if(!(nombre.equals("") && category.equals("Elegir por tipo"))) {
                if (!nombre.equals("")) {
                    List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorNombre(busqueda.getNombre(), idAdministrator);
                    model.addAttribute("medicamentos", listMedicine);
                }
                if (!category.equals("Elegir por tipo")) {
                    List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorCategory(busqueda.getCategoria(), idAdministrator);
                    model.addAttribute("medicamentos", listMedicine);
                }
            }else{
                List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorDosParametros(busqueda.getNombre(),busqueda.getCategoria() , idAdministrator);
                model.addAttribute("medicamentos", listMedicine);
            }
        }
        return "admin_sede/inventario";
    }
    //Se ve lista de pedidos de reposición
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
        int ola  = Integer.parseInt((String)model.getAttribute("idUser"));
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
        model.addAttribute("medicamentos", medicineRepository.listaMedicamentosPorSede(idAdministrator));
        model.addAttribute("listaMedicamentosBS", medicineRepository.listaMedicamentosPocoStock(idAdministrator));
        return "admin_sede/generarPedidoReposicion";
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

    @GetMapping("/verNotificacionesAdminSede")
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
    @GetMapping("/verPerfilAdminSede")
    public String profile(Model model){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        Administrator admin = new Administrator();
        admin = administratorRepository.getByIdAdministrador(idAdministrator);
        model.addAttribute("nombre", admin.getName());
        model.addAttribute("apellido", admin.getLastName());
        model.addAttribute("email", admin.getEmail());
        model.addAttribute("dni", admin.getDni());
        model.addAttribute("rol", "Administrador");
        model.addAttribute("sede", admin.getSite());
        return "admin_sede/profile";
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
        return "redirect:/verListaReposicion";
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
    @PostMapping("/editarPedidoReposicionAdminSede")
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
        return "redirect:/verListaReposicion";
    }

    @PostMapping("/generarReposicionAdminSedeBusca")
    public String buscarMedicinaEnGenerarReposicionAdminSede(Model model, RedirectAttributes attr, Busqueda busqueda){
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser")  );
        String nombre = busqueda.getNombre();
        String category = busqueda.getCategoria();
        System.out.println("Nombre  es " +  nombre);
        System.out.println("category  es " +  category);
        if(!nombre.equals("") && !category.equals("Elegir por tipo")){
            System.out.println("Hola 1");
            List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoDosParametros(idAdministrator, busqueda.getNombre(),busqueda.getCategoria());
            model.addAttribute("listaMedicamentosBS", listMedicine);
        }else{
            if(!(nombre.equals("") && category.equals("Elegir por tipo"))) {
                if (!nombre.equals("")) {
                    System.out.println("Hola 2");
                    List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoNombre(idAdministrator, busqueda.getNombre());
                    model.addAttribute("listaMedicamentosBS", listMedicine);
                }
                if (!category.equals("Elegir por tipo")) {
                    System.out.println("Hola 3");
                    List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoCategory(idAdministrator, busqueda.getCategoria());
                    model.addAttribute("listaMedicamentosBS", listMedicine);
                }
            }else{
                System.out.println("Hola 4");
                List<medicamentosPorSedeDTO> listMedicine = medicineRepository.listaMedicamentosBuscadorConStockLimitadoDosParametros(idAdministrator, busqueda.getNombre(),busqueda.getCategoria());
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
    @RequestMapping ("/generarReposicionAdminSede")
    @ResponseBody
    public Map<String,String> CreateReplacementOrder( @RequestBody String cuerpo , Model  model) throws JsonProcessingException {
        int idAdministrator = Integer.parseInt(""+ model.getAttribute("idUser"));
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
        List<medicamentosPorSedeDTO> listaMedicinasPocoStock = medicineRepository.listaMedicamentosPocoStock(idAdministrator);
        int auxCount2 = 0;
        for(int idx = 0 ;  idx < cantidad.size() ; idx++) {
            try{
                int ola = Integer.parseInt("" + ids.get(idx));
                int auxCount = 0;
                for(medicamentosPorSedeDTO m : listaMedicinasPocoStock){
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
                                r.setAdministrator(administratorRepository.getByIdAdministrador(Integer.parseInt((String) model.getAttribute("idUser"))));
                                //r.setIdReplacementOrder();
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
                                        lote.setExpireDate(new Date());
                                        lote.setExpire(false);
                                        lote.setStock(Integer.parseInt(quantity));
                                        lote.setReplacementOrder(newReplacementOrder);
                                        lote.setVisible(true);
                                        lote.setInitialQuantity(Integer.parseInt(quantity));
                                        loteRepository.save(lote);
                                    }
                                }
                                response.put("error" ,"");
                                response.put("response" ,"/SolicitudDeReposicionCreada?idReplacementOrder="+newReplacementOrder.getIdReplacementOrder());
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
    @PostMapping("/doctorListBuscaAdminSede")
    public String doctorListBuscaAdminSede(DataDoctorListBusca d , Model model){
        System.out.println(d.date);
        System.out.println(d.nombre);
        int idAdministrator = Integer.parseInt((String) model.getAttribute("idUser"));
        model.addAttribute("listaDoctores", doctorRepository.listaDoctorPorSede(idAdministrator));
        return "/admin_sede/doctorlist";
    }
    //Cerrar Sesion
    @GetMapping("/CerrarSesionAdminSede")
    public String CerrarSesionAdminSede(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return "redirect:/inicioSesion";
    }

}
