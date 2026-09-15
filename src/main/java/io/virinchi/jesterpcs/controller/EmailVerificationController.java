package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.EmailVerificationDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailVerificationController {

    private final EmailVerificationDAO emailVerificationDAO;
    private final UserDAO userDAO;

    public EmailVerificationController(
            EmailVerificationDAO emailVerificationDAO,
            UserDAO userDAO) {
        this.emailVerificationDAO = emailVerificationDAO;
        this.userDAO = userDAO;
    }

    @GetMapping("/verify")
    public String verifyEmail(
            @RequestParam String token,
            Model model) {

        Integer userId = emailVerificationDAO.getUserIdByToken(token);

        if (userId == null) {
            model.addAttribute("error", "Invalid or expired verification link.");
            return "verification";
        }

        userDAO.verifyEmail(userId);
        emailVerificationDAO.deleteToken(token);

        model.addAttribute("success", "Your email has been verified successfully!");

        return "verification";
    }
}