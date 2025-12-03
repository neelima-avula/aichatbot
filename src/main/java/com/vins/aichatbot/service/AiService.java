package com.vins.aichatbot.service;

import com.vins.aichatbot.controller.WebhookController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.vins.aichatbot.model.AiResponse;
@Service
public class AiService {

    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    public AiService() {
        this.webClient = WebClient.builder()
                .baseUrl(System.getenv("AI_SERVICE_URL")) // set in Railway
                .build();
    }

    public String getAiReply(String userPrompt) {
        try {
            // JSON request payload
            var request = new java.util.HashMap<String, String>();
            request.put("prompt", userPrompt);

            logger.info("web client  1111from: {} | Body: {}", System.getenv("AI_SERVICE_URL"));

            // Call Python API
            AiResponse aiResponse = webClient.post()
                    .uri("/ai")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AiResponse.class)
                    .block();

            String response = aiResponse != null ? aiResponse.getReply() : "AI error";

            logger.info("web client  222222from: {} | Body: {}", response);
            return response != null ? response: "AI Error";
        } catch (Exception e) {
            return "AI service error: " + e.getMessage();
        }
    }


}