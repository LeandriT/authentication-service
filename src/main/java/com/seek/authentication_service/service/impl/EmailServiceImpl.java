package com.seek.authentication_service.service.impl;

import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // Usaremos Thymeleaf como motor de plantillas


    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("soporte@innovatechnologies.com");
    }

    @Override
    public void sendHtmlEmail(String toEmail, String subject, Map<String, Object> variables, String templateName) {
        try {
            // Crear el mensaje
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Recuperación de Contraseña - Innova Technologies");
            helper.setFrom("soporte@innovatechnologies.com");

            // Crear contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("name", variables.get("fullName"));
            context.setVariable("temporaryPassword", variables.get("password"));

            // Procesar el template
            String htmlContent = templateEngine.process("email-template", context);
            helper.setText(htmlContent, true);

            // Adjuntar la imagen como contenido inline
            FileSystemResource image = new FileSystemResource(new File("src/main/resources/static/images/logo.png"));
            helper.addInline("logoImage", image);

            // Enviar correo
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new GenericException("Error al enviar el correo: " + e.getMessage());
        }
    }
}