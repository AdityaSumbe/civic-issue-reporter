
package com.aditya.civic_issue_reporter.controller;

import com.aditya.civic_issue_reporter.services.OllamaAiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-test")
public class AiTestController {

    private final OllamaAiService ollamaAiService;

    public AiTestController(OllamaAiService ollamaAiService) {
        this.ollamaAiService = ollamaAiService;
    }

    @PostMapping
    public Map<String, String> testAi(@RequestBody Map<String, String> request) {

        String prompt = request.get("prompt");

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt must not be empty");
        }

        String response = ollamaAiService.generateResponse(prompt);

        return Map.of("response", response);
    }
}
