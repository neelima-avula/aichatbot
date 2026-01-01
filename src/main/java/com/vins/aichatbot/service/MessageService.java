package com.vins.aichatbot.service;

import com.vins.aichatbot.dto.IncomingMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    WhatsAppSender whatsAppSender;

    public String processIncomingMessage(IncomingMessageDTO dto) {

        String message = dto.getBody().toLowerCase();

        if (message.contains("hi") || message.contains("hello")) {
            return "Hello 👋! How can I assist you today? from neelima";
        }

        return "Received your message: " + dto.getBody();
    }

    public void processIncomingMessage(String from, String body) {

        if (body == null || body.isBlank()) return;

        String reply = "Hi 👋 I received your message: " + body;

        whatsAppSender.sendTextMessage(from, reply);
    }
}
