package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.PasswordResetDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.User;
import io.virinchi.jesterpcs.util.EmailService;
import io.virinchi.jesterpcs.util.PasswordUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    private final UserDAO userDAO;
    private final PasswordResetDAO passwordResetDAO;
    private final EmailService emailService;

    public ForgotPasswordController(
            UserDAO userDAO,
            PasswordResetDAO passwordResetDAO,
            EmailService emailService) {

        this.userDAO = userDAO;
        this.passwordResetDAO = passwordResetDAO;
        this.emailService = emailService;
    }

    // Show forgot password page
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    // Send password reset email
    @PostMapping("/forgot-password")
    public String sendResetEmail(
            @RequestParam("email") String email,
            Model model) {

        email = email.trim();

        User user =
                userDAO.getUserByEmail(email);

        if (user != null) {

            passwordResetDAO.deleteTokensForUser(
                    user.getUserId());

            String token =
                    UUID.randomUUID().toString();

            Timestamp expiresAt =
                    Timestamp.valueOf(
                            LocalDateTime.now()
                                    .plusMinutes(30)
                    );

            passwordResetDAO.addResetToken(
                    user.getUserId(),
                    token,
                    expiresAt
            );

            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    token
            );
        }

        model.addAttribute(
                "success",
                "If an account exists with that email, a password reset link has been sent."
        );

        return "forgot-password";
    }

    // Show reset password page
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam("token") String token,
            Model model) {

        Integer userId =
                passwordResetDAO.getUserIdByToken(token);

        if (userId == null) {
            model.addAttribute(
                    "error",
                    "This password reset link is invalid or has expired."
            );

            return "reset-password";
        }

        model.addAttribute("token", token);

        return "reset-password";
    }

    // Update password
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        Integer userId =
                passwordResetDAO.getUserIdByToken(token);

        if (userId == null) {
            model.addAttribute(
                    "error",
                    "This password reset link is invalid or has expired."
            );

            return "reset-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            model.addAttribute("token", token);

            return "reset-password";
        }

        if (password.length() < 8) {
            model.addAttribute(
                    "error",
                    "Password must be at least 8 characters."
            );

            model.addAttribute("token", token);

            return "reset-password";
        }

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        userDAO.updatePassword(
                userId,
                hashedPassword
        );

        passwordResetDAO.deleteToken(token);

        return "redirect:/login?reset=success";
    }
}