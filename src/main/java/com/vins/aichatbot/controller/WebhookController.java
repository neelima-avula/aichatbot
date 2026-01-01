package com.vins.aichatbot.controller;

import com.vins.aichatbot.dto.IncomingMessageDTO;
import com.vins.aichatbot.model.Message;
import com.vins.aichatbot.model.Users;
import com.vins.aichatbot.repository.MessageRepository;
import com.vins.aichatbot.repository.UserRepository;
import com.vins.aichatbot.service.AiService;
import com.vins.aichatbot.service.MessageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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


    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge
    ) {
        logger.info("🔐 Webhook verification hit");
        logger.info("mode={}, token={}, challenge={}", mode, token, challenge);

        if ("subscribe".equals(mode) && "ai_whatsapp_webhook".equals(token)) {
            return ResponseEntity.ok(challenge); // MUST return challenge only
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
    }
    @PostMapping("/whatsapp")
    public ResponseEntity<String> receiveWhatsAppMessage(
            @RequestBody Map<String, Object> payload) {

        logger.info("📩 WhatsApp payload received: {}", payload);

        try {
            Map entry = ((List<Map>) payload.get("entry")).get(0);
            Map change = ((List<Map>) entry.get("changes")).get(0);
            Map value = (Map) change.get("value");

            // Ignore delivery/status events
            if (value.get("messages") == null) {
                return ResponseEntity.ok("EVENT_RECEIVED");
            }

            Map message = ((List<Map>) value.get("messages")).get(0);
            String from = (String) message.get("from");
            Map text = (Map) message.get("text");
            String body = (String) text.get("body");

            logger.info("📩 WhatsApp message from {} : {}", from, body);

            String phone = from.replace("whatsapp:", "");

            Users user= userRepository.findByPhoneNumber(phone)
                    .orElseGet(()->{
                        Users newUser = new Users();
                        newUser.setPhoneNumber(phone);
                        newUser.setUserName("User");
                        return userRepository.save(newUser);

                    });

            Message msg = new Message();
            msg.setUser(user);
            msg.setMessageText(body);
            messageRepository.save(msg);

            logger.info("Received 33333333333 from: {} | Body: {}", from, body);
            System.out.println("--------------------- a------------------------");
            System.out.println(body);

            String aiReply = aiService.getAiReply(body);

            logger.info("Received 33333333333 from: {} | Body: {}", aiReply);

            // 3. Return reply back to WhatsApp


            messageService.processIncomingMessage(from, aiReply);

        } catch (Exception e) {
            logger.error("❌ Error processing WhatsApp message", e);
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }

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
        logger.info("Received 222222 from: {} | Body: {}", from, body);


        // Save message
        Message msg = new Message();
        msg.setUser(user);
        msg.setMessageText(body);
        messageRepository.save(msg);

        logger.info("Received 33333333333 from: {} | Body: {}", from, body);
        System.out.println("--------------------- a------------------------");
        System.out.println(body);

        String aiReply = aiService.getAiReply(body);

        logger.info("Received 33333333333 from: {} | Body: {}", aiReply);

        // 3. Return reply back to WhatsApp
        return  aiReply ;

        // TwiML response format (Twilio requirement)
       // return "<Response><Message>" + "inserted one record " + "</Message></Response>";
    }
}