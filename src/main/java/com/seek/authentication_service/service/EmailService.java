package com.seek.authentication_service.service;

import java.util.Map;

public interface EmailService {
    void sendSimpleEmail(String toEmail, String subject, String body);

    void sendHtmlEmail(String toEmail, String subject, Map<String, Object> variables, String templateName);
}
