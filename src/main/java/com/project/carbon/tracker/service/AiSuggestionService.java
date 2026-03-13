package com.project.carbon.tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiSuggestionService {
    private final RestTemplate restTemplate;
    private final CarbonTrackingService carbonTrackingService;


    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String getEcoSuggestion(Long userId) {

        Map<String, Object> dashboardData = carbonTrackingService.getUserDashboard(userId);


        String prompt = "You are an expert eco-coach. Review this user's carbon footprint data: "
                + dashboardData.toString()
                + ". Give them 2 short, highly actionable tips to reduce their specific carbon footprint. "
                + "Keep the tone encouraging, and keep the response under 4 sentences.";


        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> parts = new HashMap<>();
        parts.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(parts));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);


        String urlWithKey = apiUrl + "?key=" + apiKey;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithKey, requestEntity, Map.class);


            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");
                return (String) partsList.get(0).get("text");
            }
            return "Keep up the great work! No new suggestions at this time.";

        } catch (Exception e) {
            System.out.println("AI Error: " + e.getMessage());
            return "Our AI eco-coach is currently resting. Please try again later!";
        }
    }
}
