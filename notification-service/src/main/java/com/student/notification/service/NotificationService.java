package com.student.notification.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    // This annotation tells Spring to watch the "student-topic"
    @KafkaListener(topics = "student-topic", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("----------------------------------------------");
        System.out.println("NOTIFICATION SERVICE RECEIVED A MESSAGE:");
        System.out.println("Action: " + message);
        System.out.println("Status: Email notification sent to the student!");
        System.out.println("----------------------------------------------");
    }
}