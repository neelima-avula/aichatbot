package com.vins.aichatbot.controller;

import com.vins.aichatbot.dto.IncomingMessageDTO;
import com.vins.aichatbot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/twilio")
    public String receiveMessage(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        IncomingMessageDTO dto = new IncomingMessageDTO(from, body);
        String reply = messageService.processIncomingMessage(dto);

        logger.info("Received message from: {} | Body: {}", from, body);


        // TwiML response format (Twilio requirement)
        return "<Response><Message>" + reply + "</Message></Response>";
    }
}