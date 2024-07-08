package com.example.proyectogrupo4_gtics.Controller;

import com.example.proyectogrupo4_gtics.Config.Message;
import com.example.proyectogrupo4_gtics.Entity.*;
import com.example.proyectogrupo4_gtics.Repository.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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





    @MessageMapping(value = "/chat")
    public void sendMessage(Message chatMessage) {
        System.out.println("Holaa");
//        HashMap<String , String > hasMessage = (HashMap<String , String>)chatMessage;
//        Message m = new Message ();
//        m.setRecipient(hasMessage.get("recipient"));
//        m.setContent(hasMessage.get("content"));
//        m.setSender(hasMessage.get("sender"));
        //Debemos de enviar el mensaje a todos los farmacistas de esa sede con un for
        User sender = userRepository.findByEmail(chatMessage.getSender());
        User  receiver = userRepository.findByEmail(chatMessage.getRecipient());
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
                if((c.getIdPacient() == patient)  &&  (c.getIdFarmacist() == pharmacist)){
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




}
