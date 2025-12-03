package com.vins.aichatbot.controller;

import com.vins.aichatbot.dto.IncomingMessageDTO;
import com.vins.aichatbot.model.Message;
import com.vins.aichatbot.model.Users;
import com.vins.aichatbot.repository.MessageRepository;
import com.vins.aichatbot.repository.UserRepository;
import com.vins.aichatbot.service.AiService;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AiService aiService;

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/twilio")
    public String receiveMessage(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {



        //IncomingMessageDTO dto = new IncomingMessageDTO(from, body);
        //String reply = messageService.processIncomingMessage(dto);

        logger.info("Received message from: {} | Body: {}", from, body);

        String phone = from.replace("whatsapp:", "");

        Users user= userRepository.findByPhoneNumber(phone)
                .orElseGet(()->{
                    Users newUser = new Users();
                    newUser.setPhoneNumber(phone);
                    newUser.setUserName("User");
                    return userRepository.save(newUser);

                });

        // Save message
        Message msg = new Message();
        msg.setUser(user);
        msg.setMessageText(body);
        messageRepository.save(msg);


        String aiReply = aiService.getAiReply(body);

        // 3. Return reply back to WhatsApp
        return """<Response><Message>""" + aiReply + """</Message></Response>""";

        // TwiML response format (Twilio requirement)
       // return "<Response><Message>" + "inserted one record " + "</Message></Response>";
    }
}