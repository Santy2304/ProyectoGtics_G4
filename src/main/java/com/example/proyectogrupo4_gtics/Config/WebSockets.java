package com.example.proyectogrupo4_gtics.Config;

import com.example.proyectogrupo4_gtics.Entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSockets implements WebSocketConfigurer {
    @Autowired
    private ChatHandler chatHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(chatHandler, "/chatPaciente");
    }



}
