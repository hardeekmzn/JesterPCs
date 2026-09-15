package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final UserDAO userDAO;

    public AuthController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/auth-status")
    public Map<String, Boolean> authStatus(HttpSession session) {

        Map<String, Boolean> status = new HashMap<>();

        Integer userId =
                (Integer) session.getAttribute("loggedInUserId");

        if (userId == null) {
            status.put("loggedIn", false);
            status.put("admin", false);
            return status;
        }

        User user = userDAO.getUserById(userId);

        boolean loggedIn = user != null;
        boolean admin = loggedIn && "ADMIN".equals(user.getRole());

        status.put("loggedIn", loggedIn);
        status.put("admin", admin);

        return status;
    }
}