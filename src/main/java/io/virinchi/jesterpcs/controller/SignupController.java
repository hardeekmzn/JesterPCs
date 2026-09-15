package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.EmailVerificationDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.User;
import io.virinchi.jesterpcs.util.EmailService;
import io.virinchi.jesterpcs.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class SignupController {

    private final UserDAO userDAO;
    private final EmailVerificationDAO emailVerificationDAO;
    private final EmailService emailService;

    public SignupController(
            UserDAO userDAO,
            EmailVerificationDAO emailVerificationDAO,
            EmailService emailService) {
        this.userDAO = userDAO;
        this.emailVerificationDAO = emailVerificationDAO;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public String signup(
            String fullname,
            String username,
            String email,
            String password,
            String confirm,
            Model model) {

        model.addAttribute("type", "signup");

        if (!password.equals(confirm)) {
            model.addAttribute("error", "Passwords do not match.");
            return "signup";
        }

        if (password.length() < 8) {
            model.addAttribute("error",
                    "Password must be at least 8 characters. You new to this?");
            return "signup";
        }

        User existingUsername = userDAO.getUserByUsername(username);

        if (existingUsername != null) {

            if (existingUsername.isEmailVerified()) {
                model.addAttribute("error",
                        "Username taken lol. Better luck next time when we make another website ig.");
                return "signup";
            }

            resendVerification(
                    existingUsername.getUserId(),
                    existingUsername.getEmail()
            );

            model.addAttribute("email", existingUsername.getEmail());

            return "waiting";
        }

        User existingEmail = userDAO.getUserByEmail(email);

        if (existingEmail != null) {

            if (existingEmail.isEmailVerified()) {
                model.addAttribute("error",
                        "Email already registered. Check your notes or something.");
                return "signup";
            }

            resendVerification(
                    existingEmail.getUserId(),
                    existingEmail.getEmail()
            );

            model.addAttribute("email", existingEmail.getEmail());

            return "waiting";
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(
                fullname,
                username,
                email,
                hashedPassword
        );

        int userId = userDAO.addUser(user);

        String token = UUID.randomUUID().toString();

        emailVerificationDAO.addVerificationToken(userId, token);

        emailService.sendVerificationEmail(email, token);

        model.addAttribute("email", email);

        return "waiting";
    }

    private void resendVerification(int userId, String email) {

        String token = UUID.randomUUID().toString();

        emailVerificationDAO.deleteTokensForUser(userId);

        emailVerificationDAO.addVerificationToken(userId, token);

        emailService.sendVerificationEmail(email, token);
    }
}