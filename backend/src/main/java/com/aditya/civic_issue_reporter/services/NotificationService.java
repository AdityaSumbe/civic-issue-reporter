package com.aditya.civic_issue_reporter.services;

import com.aditya.civic_issue_reporter.entity.Notification;
import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.repository.NotificationRepository;
import com.aditya.civic_issue_reporter.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(User user, String message) {
        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }
    private NotificationResponse toNotificationResponse(
            Notification notification
    ) {
        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setUserId(notification.getUser().getId());
        response.setMessage(notification.getMessage());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }

    public Page<NotificationResponse> getUserNotifications(
            Long userId,
            Pageable pageable
    ) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toNotificationResponse);
    }
}