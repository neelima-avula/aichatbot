package com.vins.aichatbot.service;

import com.vins.aichatbot.dto.IncomingMessageDTO;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public String processIncomingMessage(IncomingMessageDTO dto) {

        String message = dto.getBody().toLowerCase();

        if (message.contains("hi") || message.contains("hello")) {
            return "Hello 👋! How can I assist you today? from neelima";
        }

        return "Received your message: " + dto.getBody();
    }

    public String processIncomingMessage(String from, String body) {

        String message = body;

        if (message.contains("hi") || message.contains("hello")) {
            return "Hello 👋! How can I assist you today? from neelima";
        }

        return "Received your message: " + message;
    }
}
