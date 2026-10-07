package com.aditya.civic_issue_reporter.controller;

import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.repository.UserRepository;
import com.aditya.civic_issue_reporter.services.NotificationService;
import com.aditya.civic_issue_reporter.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository
    ) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(
                notificationService.getUserNotifications(
                        user.getId(),
                        pageable
                )
        );
    }
}