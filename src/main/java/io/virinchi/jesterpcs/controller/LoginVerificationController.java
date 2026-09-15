package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.LoginVerificationDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginVerificationController {

    private final LoginVerificationDAO loginVerificationDAO;
    private final UserDAO userDAO;

    public LoginVerificationController(
            LoginVerificationDAO loginVerificationDAO,
            UserDAO userDAO) {

        this.loginVerificationDAO = loginVerificationDAO;
        this.userDAO = userDAO;
    }

    // Verify login using the email token
    @GetMapping("/login-verify")
    public String verifyLogin(
            @RequestParam String token,
            HttpSession session) {

        Integer userId =
                loginVerificationDAO.getUserIdByToken(token);

        if (userId == null) {
            return "redirect:/login";
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {
            return "redirect:/login";
        }

        // Store the user in the session
        session.setAttribute("user", user);

        // Keep the user ID in the session
        session.setAttribute("loggedInUserId", userId);

        loginVerificationDAO.deleteToken(token);

        return "redirect:/";
    }
}