package com.project.carbon.tracker.controller;

import com.project.carbon.tracker.service.AiSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AiController {

    private final AiSuggestionService aiSuggestionService;


    @GetMapping("/suggestion/{userId}")
    public ResponseEntity<String> getAiSuggestion(@PathVariable Long userId) {
        String suggestion = aiSuggestionService.getEcoSuggestion(userId);
        return ResponseEntity.ok(suggestion);
    }
}
