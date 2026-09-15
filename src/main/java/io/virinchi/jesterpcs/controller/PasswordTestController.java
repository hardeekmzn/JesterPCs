package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.util.PasswordUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordTestController {

    @GetMapping("/test-password")
    public String testPassword() {

        String password = "test1234";

        String hashedPassword = PasswordUtil.hashPassword(password);

        boolean matches = PasswordUtil.checkPassword(password, hashedPassword);

        return "Password: " + password +
                "<br>Hashed: " + hashedPassword +
                "<br>Matches: " + matches;
    }
}