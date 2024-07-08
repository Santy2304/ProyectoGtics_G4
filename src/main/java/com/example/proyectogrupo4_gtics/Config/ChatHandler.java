package com.example.proyectogrupo4_gtics.Config;

import com.example.proyectogrupo4_gtics.Entity.User;
import com.example.proyectogrupo4_gtics.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
@Service
public class ChatHandler extends TextWebSocketHandler {
    final UserRepository userRepository;
    public ChatHandler (UserRepository userRepository){
        this.userRepository =  userRepository;
    }

    private final Map<User, WebSocketSession> sessionsNuevoPacientes = new ConcurrentHashMap<>();
    private final Map<User, WebSocketSession> sessionsNuevoFarmacistas = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session ) throws Exception {
        String username = session.getPrincipal().getName();
        User usuario = userRepository.findById(userRepository.encontrarId(username)).get();
        //Manejamos roles;
        String rol = usuario.getIdRol().getName();
        if(rol.equals("farmacista")){
            sessionsNuevoFarmacistas.put(usuario,session);
        }
        if(rol.equals("paciente")){
            sessionsNuevoPacientes.put(usuario,session);
        }
        System.out.println("inicio sesion");
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = session.getPrincipal().getName(); // Obtener nombre de usuario desde la sesión
        User usuario = userRepository.findById(userRepository.encontrarId(username)).get();
        //Manejamos roles;
        String rol = usuario.getIdRol().getName();
        if(rol.equals("farmacista")){
            sessionsNuevoFarmacistas.remove(usuario,session);

        }
        if(rol.equals("paciente")){
            sessionsNuevoPacientes.remove(usuario,session);

        }
        System.out.println("cierro sesion");
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String username = session.getPrincipal().getName(); // Obtener nombre de usuario desde la sesión
        User usuario  = userRepository.findByEmail(username);
        //Manejamos roles;
        String rol = usuario.getIdRol().getName();
        if(rol.equals("farmacista")){
            //Manda a pacientes
            sessionsNuevoPacientes.forEach((key, value) ->{
                try {
                    value.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if(rol.equals("paciente")){
            //Manda a farmacistas
            sessionsNuevoFarmacistas.forEach((key, value) ->{
                try {
                    value.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


}