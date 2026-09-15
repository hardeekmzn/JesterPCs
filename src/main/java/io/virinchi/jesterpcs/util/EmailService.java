package io.virinchi.jesterpcs.util;

import io.virinchi.jesterpcs.model.Product;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${jesterpcs.admin.email}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String token) {

        String verificationLink =
                "http://localhost:8080/verify?token=" + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject(
                "JesterPCs Email Verification");

        message.setText(
                "Welcome to JesterPCs!\n\n" +
                        "Thank you for creating an account.\n\n" +
                        "Please click the link below to verify your email address:\n\n" +
                        verificationLink +
                        "\n\n" +
                        "If you did not create this account, you can ignore this email."
        );

        mailSender.send(message);
    }

    public void sendLoginVerificationEmail(
            String email,
            String token) {

        String verificationLink =
                "http://localhost:8080/login-verify?token=" + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject(
                "JesterPCs Login Verification");

        message.setText(
                "Someone is trying to sign in to your JesterPCs account.\n\n" +
                        "If this was you, click the link below to complete your login:\n\n" +
                        verificationLink +
                        "\n\n" +
                        "If you did not try to log in, you can ignore this email."
        );

        mailSender.send(message);
    }

    // Send password reset email
    public void sendPasswordResetEmail(
            String email,
            String token) {

        String resetLink =
                "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject(
                "JesterPCs Password Reset");

        message.setText(
                "We received a request to reset your JesterPCs password.\n\n" +
                        "Click the link below to create a new password:\n\n" +
                        resetLink +
                        "\n\n" +
                        "This link will expire after 30 minutes.\n\n" +
                        "If you did not request a password reset, you can ignore this email."
        );

        mailSender.send(message);
    }

    // Send low stock email
    public void sendLowStockEmail(
            Product product,
            int stockQuantity) {

        boolean critical =
                stockQuantity <= 1;

        String subject =
                critical
                        ? "JesterPCs Critical Stock Alert"
                        : "JesterPCs Low Stock Alert";

        String messageText =
                critical
                        ? """
                        Critical Stock Alert

                        A product critically low on stock.

                        Product: %s
                        Product ID: %d
                        Remaining Stock: %d

                        Only 1 unit is left. Please restock.
                        """.formatted(
                        product.getProductName(),
                        product.getProductId(),
                        stockQuantity)
                        : """
                        Low Stock Alert

                        Product stock very low.

                        Product: %s
                        Product ID: %d
                        Remaining Stock: %d

                        idk what message to put for restocking but just restock gng.
                        """.formatted(
                        product.getProductName(),
                        product.getProductId(),
                        stockQuantity);


        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(adminEmail);
        message.setSubject(subject);
        message.setText(messageText);

        mailSender.send(message);
    }
}