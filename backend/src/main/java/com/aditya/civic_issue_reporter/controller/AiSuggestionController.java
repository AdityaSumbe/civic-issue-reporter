package com.aditya.civic_issue_reporter.controller;

import com.aditya.civic_issue_reporter.dto.AiIssueSuggestionRequest;
import com.aditya.civic_issue_reporter.dto.AiIssueSuggestionResponse;
import com.aditya.civic_issue_reporter.services.OllamaAiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiSuggestionController {

    private final OllamaAiService ollamaAiService;

    public AiSuggestionController(OllamaAiService ollamaAiService) {
        this.ollamaAiService = ollamaAiService;
    }

    @PostMapping("/suggestions")
    public AiIssueSuggestionResponse suggestIssue(
            @Valid @RequestBody AiIssueSuggestionRequest request) {

        return ollamaAiService.suggestIssue(
                request.getTitle(),
                request.getDescription()
        );
    }
}
