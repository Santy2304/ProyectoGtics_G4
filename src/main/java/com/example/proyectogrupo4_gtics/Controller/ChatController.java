package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Config.Image;
import com.example.proyectogrupo4_gtics.Config.Message;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    final ChatRepository chatRepository;
    final ChatContentRepository chatContentRepository;
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
    private final PatientRepository patientRepository;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate,
                          UserRepository userRepository,
                          ChatRepository chatRepository,
                          ChatContentRepository chatContentRepository,
                          AdministratorRepository administratorRepository,
                          DoctorRepository doctorRepository,
                          PharmacistRepository pharmacistRepository,
                          MedicineRepository medicineRepository,
                          ReplacementOrderRepository replacementOrderRepository,
                          ReplacementOrderHasMedicineRepository replacementOrderHasMedicineRepository ,
                          LoteRepository loteRepository,
                          TrackingRepository trackingRepository,
                          NotificationsRepository notificationsRepository,
                          SiteRepository siteRepository,
                          CodeRepository codeRepository,
                          PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatRepository = chatRepository;
        this.chatContentRepository = chatContentRepository;
        this.administratorRepository = administratorRepository;
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.medicineRepository = medicineRepository;
        this.replacementOrderRepository = replacementOrderRepository ;
        this.loteRepository =loteRepository;
        this.trackingRepository = trackingRepository;
        this.notificationsRepository= notificationsRepository;
        this.siteRepository=siteRepository;
        this.codeRepository = codeRepository;
        this.patientRepository = patientRepository;
    }
//De farmacista a Paciente
    //Farmacista a paciente
    @MessageMapping(value = "/chatPharmacist")
    public void sendMessagePharmacistToPatient(Message chatMessage) {
        User sender = userRepository.findByEmail(chatMessage.getSender());
        User  receiver = userRepository.getById(chatMessage.getRecipient());
        //Si no existe debemos crear un chat q contenga
        Pharmacist pharmacist = null ;
        Patient patient =  null;
        switch(sender.getIdRol().getName()){
            case "paciente":
                patient = patientRepository.findByEmail(sender.getEmail()).get();
                break;
            case "farmacista":
                pharmacist = pharmacistRepository.findByEmail(sender.getEmail());
                break;
        }
        switch(receiver.getIdRol().getName()){
            case "paciente":
                patient = patientRepository.findByEmail(receiver.getEmail()).get();
                break;
            case "farmacista":
                pharmacist = pharmacistRepository.findByEmail(receiver.getEmail());
                break;
        }

        Chat chatVerdadero = null;
        List<Chat> chats = chatRepository.findAll();
        if(!chats.isEmpty()){
            boolean existeChatSenderReceiver = false;
            for(Chat c : chats){
                if((c.getIdPacient().getIdPatient() == patient.getIdPatient())  &&  (c.getIdFarmacist().getIdFarmacista() == pharmacist.getIdFarmacista())){
                    chatVerdadero = c;
                    existeChatSenderReceiver = true;
                    break;
                }
            }
            if(!existeChatSenderReceiver){
                Chat c =  new Chat();
                c.setIdPacient(patient);
                c.setIdFarmacist(pharmacist);
                chatVerdadero = chatRepository.save(c);
            }
        }else{
            //Debemos de crear el chat xd
            Chat c =  new Chat();
            c.setIdPacient(patient);
            c.setIdFarmacist(pharmacist);
            chatVerdadero = chatRepository.save(c);
        }


        Chatcontent content = new Chatcontent();
        content.setMessage(chatMessage.getContent());
        LocalDateTime ahora = LocalDateTime.now();

        // Formatear la hora actual según tus necesidades
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String horaFormateada = ahora.format(formatter);
        content.setDateTime(ahora);
        content.setAutor(sender.getEmail());
        content.setIdChat(chatVerdadero);
        chatContentRepository.save(content);
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", chatMessage);
    }
//De paciente a farmacista
    //Paciente enviando texto
    @MessageMapping(value = "/chat")
    public void sendMessage(Message chatMessage) {
        try {
            //Debemos de enviar el mensaje a todos los farmacistas de esa sede con un for
            User sender = userRepository.findByEmail(chatMessage.getSender());
            User receiver = userRepository.getById(chatMessage.getRecipient());
            //Si no existe debemos crear un chat q contenga
            Pharmacist pharmacist = null;
            Patient patient = null;
            switch (sender.getIdRol().getName()) {
                case "paciente":
                    patient = patientRepository.findByEmail(sender.getEmail()).get();
                    break;
                case "farmacista":
                    pharmacist = pharmacistRepository.findByEmail(sender.getEmail());
                    break;
            }
            switch (receiver.getIdRol().getName()) {
                case "paciente":
                    patient = patientRepository.findByEmail(receiver.getEmail()).get();
                    break;
                case "farmacista":
                    pharmacist = pharmacistRepository.findByEmail(receiver.getEmail());
                    break;
            }
            Chat chatVerdadero = null;
            List<Chat> chats = chatRepository.findAll();
            if (!chats.isEmpty()) {
                boolean existeChatSenderReceiver = false;
                for (Chat c : chats) {
                    if ((c.getIdPacient().getIdPatient() == patient.getIdPatient()) && (c.getIdFarmacist().getIdFarmacista() == pharmacist.getIdFarmacista())) {
                        chatVerdadero = c;
                        existeChatSenderReceiver = true;
                        break;
                    }
                }
                if (!existeChatSenderReceiver) {
                    Chat c = new Chat();
                    c.setIdPacient(patient);
                    c.setIdFarmacist(pharmacist);
                    chatVerdadero = chatRepository.save(c);
                }
            } else {
                //Debemos de crear el chat xd
                Chat c = new Chat();
                c.setIdPacient(patient);
                c.setIdFarmacist(pharmacist);
                chatVerdadero = chatRepository.save(c);
            }

            Chatcontent content = new Chatcontent();
            content.setMessage(chatMessage.getContent());
            LocalDateTime ahora = LocalDateTime.now();

            // Formatear la hora actual según tus necesidades
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String horaFormateada = ahora.format(formatter);
            content.setDateTime(ahora);
            content.setAutor(sender.getEmail());
            content.setIdChat(chatVerdadero);
            chatContentRepository.save(content);
            //Escogo mandarles a todos los farmacistas de esa sede ;
            List<Pharmacist> listaFarmacista = pharmacistRepository.findAll();
            ArrayList<Pharmacist> listaFiltrada = new ArrayList<>();
            //simpMessagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", chatMessage);
            for (Pharmacist p : listaFarmacista) {
                if (p.getSite().equals(pharmacist.getSite()) && !p.getEmail().equals(pharmacist.getEmail())   ) {
                    chatVerdadero.setIdFarmacist(p);
                    content.setIdChat(chatVerdadero);
                    chatContentRepository.save(content);
                    simpMessagingTemplate.convertAndSendToUser(p.getEmail(), "/queue/messages", chatMessage);
                }
            }
        }catch(Exception error ){
            error.printStackTrace();
        }
    }
    //Paciente enviando imagenes
    @MessageMapping(value = "/chatImage")
    public void sendImage(Message chatMessage){
        try {
            //Debemos de enviar el mensaje a todos los farmacistas de esa sede con un for
            User sender = userRepository.findByEmail(chatMessage.getSender());
            User receiver = userRepository.getById(chatMessage.getRecipient());
            if (chatMessage.getContent() == null) {
                chatMessage.setContent("");
            }
            //Si no existe debemos crear un chat q contenga
            Pharmacist pharmacist = null;
            Patient patient = null;
            switch (sender.getIdRol().getName()) {
                case "paciente":
                    patient = patientRepository.findByEmail(sender.getEmail()).get();
                    break;
                case "farmacista":
                    pharmacist = pharmacistRepository.findByEmail(sender.getEmail());
                    break;
            }
            switch (receiver.getIdRol().getName()) {
                case "paciente":
                    patient = patientRepository.findByEmail(receiver.getEmail()).get();
                    break;
                case "farmacista":
                    pharmacist = pharmacistRepository.findByEmail(receiver.getEmail());
                    break;
            }
            //Con lo anterior logramos encontrar al patient y al pharmacist;

            Chat chatVerdadero = null;
            List<Chat> chats = chatRepository.findAll();
            if (!chats.isEmpty()) {
                boolean existeChatSenderReceiver = false;
                for (Chat c : chats) {
                    if ((c.getIdPacient().getIdPatient() == patient.getIdPatient()) && (c.getIdFarmacist().getIdFarmacista() == pharmacist.getIdFarmacista())) {
                        chatVerdadero = c;
                        existeChatSenderReceiver = true;
                        break;
                    }
                }
                if (!existeChatSenderReceiver) {
                    Chat c = new Chat();
                    c.setIdPacient(patient);
                    c.setIdFarmacist(pharmacist);
                    chatVerdadero = chatRepository.save(c);
                }
            } else {
                //Debemos de crear el chat xd
                Chat c = new Chat();
                c.setIdPacient(patient);
                c.setIdFarmacist(pharmacist);
                chatVerdadero = chatRepository.save(c);
            }

            Chatcontent content = new Chatcontent();
            LocalDateTime ahora = LocalDateTime.now();
            // Formatear la hora actual según tus necesidades
            for (Chat chatSupremes : chatRepository.findAll()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String horaFormateada = ahora.format(formatter);
                content.setDateTime(ahora);
                content.setAutor(sender.getEmail());
                content.setIdChat(chatVerdadero);
                //chatContentRepository.save(content);
                //Escogo mandarles a todos los farmacistas de esa sede ;
                List<Pharmacist> listaFarmacista = pharmacistRepository.findAll();
                //simpMessagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", chatMessage);
                for (Pharmacist p : listaFarmacista) {
                    if (p.getSite().equals(pharmacist.getSite()) && !p.getEmail().equals(pharmacist.getEmail())) {
                        chatMessage.setRecipient(p.getEmail());
                        simpMessagingTemplate.convertAndSendToUser(p.getEmail(), "/queue/messages", chatMessage);
                    }
                }
            }

        }catch(Exception error){
            error.printStackTrace();
        }
    }
    @PostMapping(value="/saveImage")
    @ResponseBody
    public Object saveImage(HttpSession session , @RequestParam(value="image") MultipartFile img ) {
        LinkedHashMap<String , Object  >generalResponse = new LinkedHashMap<>();
        try{
            Patient patient = ((Patient) session.getAttribute("usuario"));
            Site sede = (Site) session.getAttribute("sede");

            ArrayList<Chat> chats = new ArrayList<>();
            for(Chat c : chatRepository.findAll()){
                if(c.getIdPacient().getIdPatient() == patient.getIdPatient()  && c.getIdFarmacist().getSite().equals(sede.getName())){
                    chats.add(c);
                }
            }
            for(Chat cs :  chats){
                Chatcontent chatcontent = new Chatcontent();
                chatcontent.setAutor(patient.getEmail());
                chatcontent.setDateTime(LocalDateTime.now());
                chatcontent.setPhoto(img.getBytes());
                chatcontent.setIdChat(cs);
                chatContentRepository.save(chatcontent);
            }
            generalResponse.put("status" , "ok");
            return ResponseEntity.status(HttpStatus.OK).body(generalResponse);
        }catch(Exception error ){
            error.printStackTrace();
            generalResponse.put("status",  "error");
            generalResponse.put("message", "Ocurrio un error inesperado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponse);
        }
    }
    @GetMapping(value="recuperarLastImage")
    @ResponseBody
    public Object recuperarLastImage(HttpSession session){
        LinkedHashMap<String , Object  >generalResponse = new LinkedHashMap<>();
        try{
            Patient patient = ((Patient) session.getAttribute("usuario"));
            List <Chatcontent> listas = chatContentRepository.findAll();
            Collections.reverse(listas);
            //Filtramos
            Chatcontent chatcontent =  new Chatcontent();
            for(Chatcontent cc :  listas){
                if( cc.getIdChat().getIdPacient().getIdPatient() == patient.getIdPatient()  && cc.getMessage() == null  ){
                    chatcontent = cc;
                    break;
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Cambia el tipo de media según el formato de tu imagen
            return new ResponseEntity<>( chatcontent.getPhoto(), headers, HttpStatus.OK);
        }catch(Exception error ){
            error.printStackTrace();
            generalResponse.put("status",  "error");
            generalResponse.put("message", "Ocurrio un error inesperado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponse);
        }
    }
    @GetMapping(value="recuperarLastImagePharmacist")
    @ResponseBody
    public Object recuperarLastImagePharmacist(HttpSession session , @RequestParam(value="email" , required = false) String email){
        LinkedHashMap<String , Object  >generalResponse = new LinkedHashMap<>();
        try{
            Pharmacist pharmacist = ((Pharmacist) session.getAttribute("usuario"));
            Patient patient = patientRepository.findByEmail(email).get();

            List <Chatcontent> listas = chatContentRepository.findAll();
            Collections.reverse(listas);
            //Filtramos
            Chatcontent chatcontent =  new Chatcontent();
            for(Chatcontent cc :  listas){
                if( cc.getIdChat().getIdPacient().getIdPatient() == patient.getIdPatient()  && cc.getMessage() == null && cc.getIdChat().getIdFarmacist().getIdFarmacista() == pharmacist.getIdFarmacista() ){
                    chatcontent = cc;
                    break;
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Cambia el tipo de media según el formato de tu imagen
            return new ResponseEntity<>( chatcontent.getPhoto(), headers, HttpStatus.OK);
        }catch(Exception error ){
            error.printStackTrace();
            generalResponse.put("status",  "error");
            generalResponse.put("message", "Ocurrio un error inesperado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResponse);
        }
    }
    @GetMapping(value="/getBlob/{id}")
    public ResponseEntity<byte[]> getBlob(HttpSession session , @PathVariable   (value="id" , required = false) String idMessage){
        System.out.println(idMessage);
            HttpHeaders headers = new HttpHeaders(  );
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>( chatContentRepository.findById(Integer.parseInt(idMessage)).get().getPhoto()       , headers, HttpStatus.OK);
    }
}
