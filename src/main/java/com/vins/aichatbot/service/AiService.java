package com.vins.aichatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AiService {

    private final WebClient webClient;

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

            // Call Python API
            String response = String.valueOf(webClient.post()
                    .uri("/ai")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AiResponse.class)
                    .block()); // synchronous

            return response != null ? response: "AI Error";
        } catch (Exception e) {
            return "AI service error: " + e.getMessage();
        }
    }

    public static class AiResponse {
        public String reply;
    }
}