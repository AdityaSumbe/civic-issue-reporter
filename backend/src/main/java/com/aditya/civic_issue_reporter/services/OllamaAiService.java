
package com.aditya.civic_issue_reporter.services;

import com.aditya.civic_issue_reporter.dto.AiIssueSuggestionResponse;
import com.aditya.civic_issue_reporter.entity.Category;
import com.aditya.civic_issue_reporter.entity.IssuePriority;
import com.aditya.civic_issue_reporter.exception.BadRequestException;
import com.aditya.civic_issue_reporter.repository.CategoryRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OllamaAiService {

    private final RestClient restClient;
    private final String model;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public OllamaAiService(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String model,
            CategoryRepository categoryRepository,
            ObjectMapper objectMapper) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.model = model;
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    public String generateResponse(String prompt) {

        Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        Map<?, ?> response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("response") == null) {
            throw new IllegalStateException(
                    "Ollama returned an empty response"
            );
        }

        return response.get("response").toString();
    }

    public AiIssueSuggestionResponse suggestIssue(
            String title,
            String description) {

        if (title == null || title.isBlank()
                || description == null || description.isBlank()) {
            throw new BadRequestException(
                    "Issue title and description are required"
            );
        }

        List<String> categoryNames = categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .toList();

        if (categoryNames.isEmpty()) {
            throw new IllegalStateException(
                    "No issue categories are configured"
            );
        }

        String prompt = """
                You are a civic issue classification system.
                         
                Your task is to classify the reported civic issue using the
                provided categories and priority levels.
                             
                 AVAILABLE CATEGORIES:
                 %s
                 
                 AVAILABLE PRIORITIES:
                 LOW, MEDIUM, HIGH, CRITICAL
                 
                 PRIORITY GUIDELINES:
                 
                 LOW:
                 Minor inconvenience with limited impact and no significant safety risk.
                 Examples: routine garbage collection delay, minor cleanliness issue.
                 
                 MEDIUM:
                 An issue affecting residents or public services that needs timely
                 attention but does not pose a significant immediate danger.
                 Examples: a streetlight out in a low-risk area, a minor water supply issue.
                 
                 HIGH:
                 A serious issue causing substantial disruption, sanitation concerns,
                 or a meaningful safety risk that requires prompt attention.
                 Examples: multiple streetlights out on a busy road, a large pothole
                 creating a substantial traffic hazard.
                 
                 CRITICAL:
                 An immediate, serious threat to life or public safety that requires
                 urgent intervention.
                 Examples: an exposed sparking electrical wire accessible to pedestrians,
                 a major active hazard posing imminent danger.
                 
                 IMPORTANT RULES:
                 1. Do not assign HIGH or CRITICAL solely because an issue sounds serious.
                 2. Do not assign CRITICAL merely because an accident could potentially occur.
                 3. Consider severity, immediacy, exposure, and the information actually provided.
                 4. Do not invent circumstances absent from the report.
                 5. Select exactly one category from the available categories.
                 6. Select exactly one priority from the available priorities.
                 7. Return only a valid JSON object with string fields:
                    category, priority, explanation.
                 8. Do not use Markdown fences or additional text.
                 
                 ISSUE TITLE:
                 %s
                 
                 ISSUE DESCRIPTION:
                 %s
                """.formatted(
                String.join(", ", categoryNames),
                title,
                description
        );

        String rawResponse = generateResponse(prompt);

        try {
            JsonNode json = objectMapper.readTree(rawResponse);

            String categoryName = requiredText(json, "category");
            String priorityName = requiredText(json, "priority");
            String explanation = requiredText(json, "explanation");

            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new BadRequestException(
                            "AI returned an unknown category: " + categoryName
                    ));

            IssuePriority priority;

            try {
                priority = IssuePriority.valueOf(
                        priorityName.toUpperCase(java.util.Locale.ROOT)
                );
            } catch (IllegalArgumentException exception) {
                throw new BadRequestException(
                        "AI returned an invalid priority: " + priorityName
                );
            }

            return new AiIssueSuggestionResponse(
                    category.getName(),
                    priority.name(),
                    explanation
            );

        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Ollama returned an invalid JSON response",
                    exception
            );
        }
    }

    private String requiredText(JsonNode json, String field) {
        JsonNode value = json.get(field);

        if (value == null || !value.isTextual()
                || value.asText().isBlank()) {
            throw new BadRequestException(
                    "AI response is missing a valid " + field
            );
        }

        return value.asText().trim();
    }
}
