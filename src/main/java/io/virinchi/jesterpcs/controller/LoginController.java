package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.LoginVerificationDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.User;
import io.virinchi.jesterpcs.util.EmailService;
import io.virinchi.jesterpcs.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class LoginController {

    private final UserDAO userDAO;
    private final LoginVerificationDAO loginVerificationDAO;
    private final EmailService emailService;

    public LoginController(
            UserDAO userDAO,
            LoginVerificationDAO loginVerificationDAO,
            EmailService emailService) {
        this.userDAO = userDAO;
        this.loginVerificationDAO = loginVerificationDAO;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public String login(
            String username,
            String password,
            Model model) {

        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            model.addAttribute("error", "Username or password is incorrect.");
            return "login";
        }

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            model.addAttribute("error", "Username or password is incorrect.");
            return "login";
        }

        if (!user.isEmailVerified()) {
            model.addAttribute("error", "Please verify your email before logging in.");
            return "login";
        }

        loginVerificationDAO.deleteTokensForUser(user.getUserId());

        String token = UUID.randomUUID().toString();

        loginVerificationDAO.addLoginToken(
                user.getUserId(),
                token
        );

        emailService.sendLoginVerificationEmail(
                user.getEmail(),
                token
        );

        model.addAttribute("type", "login");
        model.addAttribute("email", user.getEmail());

        return "waiting";
    }
}