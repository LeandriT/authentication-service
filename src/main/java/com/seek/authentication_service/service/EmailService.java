package com.seek.authentication_service.service;

public interface EmailService {
    void sendSimpleEmail(String toEmail, String subject, String body);
}
