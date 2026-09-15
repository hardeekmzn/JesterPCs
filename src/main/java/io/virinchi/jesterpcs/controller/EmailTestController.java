package io.virinchi.jesterpcs.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailTestController {

    private final JavaMailSender mailSender;

    public EmailTestController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @GetMapping("/test-email")
    public String testEmail() {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("YOUR_EMAIL@gmail.com");
        message.setSubject("JesterPCs Email Test");
        message.setText("This is a test email from JesterPCs.");

        mailSender.send(message);

        return "Email sent successfully!";
    }
}