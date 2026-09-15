package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.EmailVerificationDAO;
import io.virinchi.jesterpcs.dao.LoginVerificationDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.User;
import io.virinchi.jesterpcs.util.EmailService;
import io.virinchi.jesterpcs.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserDAO userDAO;
    private final EmailVerificationDAO emailVerificationDAO;
    private final LoginVerificationDAO loginVerificationDAO;
    private final EmailService emailService;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,49}$");

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_.]{3,30}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(?:\\+977[- ]?)?(?:98|97|96)\\d{8}$");

    public AccountController(
            UserDAO userDAO,
            EmailVerificationDAO emailVerificationDAO,
            LoginVerificationDAO loginVerificationDAO,
            EmailService emailService) {

        this.userDAO = userDAO;
        this.emailVerificationDAO = emailVerificationDAO;
        this.loginVerificationDAO = loginVerificationDAO;
        this.emailService = emailService;
    }

    // Update profile information
    @PostMapping("/profile")
    public Map<String, Object> updateProfile(
            @RequestBody Map<String, String> data,
            HttpSession session) {

        Integer userId =
                (Integer) session.getAttribute(
                        "loggedInUserId");

        if (userId == null) {
            return Map.of(
                    "success", false,
                    "message", "Please log in first."
            );
        }

        User user =
                userDAO.getUserById(userId);

        if (user == null) {
            return Map.of(
                    "success", false,
                    "message", "User account not found."
            );
        }

        String fullName =
                data.get("fullName") == null
                        ? ""
                        : data.get("fullName").trim();

        String username =
                data.get("username") == null
                        ? ""
                        : data.get("username").trim();

        String email =
                data.get("email") == null
                        ? ""
                        : data.get("email").trim();

        String phone =
                data.get("phone") == null
                        ? ""
                        : data.get("phone").trim();

        String address =
                data.get("address") == null
                        ? ""
                        : data.get("address").trim();

        if (fullName.isBlank()
                || username.isBlank()
                || email.isBlank()) {

            return Map.of(
                    "success", false,
                    "message",
                    "Please fill in all required fields."
            );
        }

        if (fullName.length() > 50) {
            return Map.of(
                    "success", false,
                    "message",
                    "Full name must be 50 characters or less."
            );
        }

        if (!NAME_PATTERN.matcher(fullName).matches()) {
            return Map.of(
                    "success", false,
                    "message",
                    "Please enter a valid full name."
            );
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return Map.of(
                    "success", false,
                    "message",
                    "Username must be 3-30 characters and use only letters, numbers, dots, or underscores."
            );
        }

        if (email.length() > 254) {
            return Map.of(
                    "success", false,
                    "message",
                    "Email address is too long."
            );
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return Map.of(
                    "success", false,
                    "message",
                    "Please enter a valid email address."
            );
        }

        if (!phone.isBlank()
                && !PHONE_PATTERN.matcher(phone).matches()) {

            return Map.of(
                    "success", false,
                    "message",
                    "Please enter a valid Nepal mobile number."
            );
        }

        if (address.length() > 200) {
            return Map.of(
                    "success", false,
                    "message",
                    "Address must be 200 characters or less."
            );
        }

        if (userDAO.usernameExistsForOtherUser(
                username,
                userId)) {

            return Map.of(
                    "success", false,
                    "message", "Username is already in use."
            );
        }

        if (userDAO.emailExistsForOtherUser(
                email,
                userId)) {

            return Map.of(
                    "success", false,
                    "message", "Email is already in use."
            );
        }

        boolean emailChanged =
                !email.equalsIgnoreCase(
                        user.getEmail());

        userDAO.updateProfile(
                userId,
                fullName,
                username,
                email,
                phone,
                address
        );

        if (emailChanged) {

            String token =
                    UUID.randomUUID().toString();

            emailVerificationDAO.deleteTokensForUser(
                    userId);

            emailVerificationDAO.addVerificationToken(
                    userId,
                    token
            );

            userDAO.markEmailUnverified(
                    userId);

            emailService.sendVerificationEmail(
                    email,
                    token
            );

            return Map.of(
                    "success", true,
                    "message",
                    "Profile updated. Please verify your new email."
            );
        }

        return Map.of(
                "success", true,
                "message",
                "Profile updated successfully."
        );
    }

    // Change account password
    @PostMapping("/password")
    public Map<String, Object> updatePassword(
            @RequestBody Map<String, String> data,
            HttpSession session) {

        Integer userId =
                (Integer) session.getAttribute(
                        "loggedInUserId");

        if (userId == null) {
            return Map.of(
                    "success", false,
                    "message",
                    "Please log in first."
            );
        }

        String currentPassword =
                data.get("currentPassword") == null
                        ? ""
                        : data.get("currentPassword");

        String newPassword =
                data.get("newPassword") == null
                        ? ""
                        : data.get("newPassword");

        if (currentPassword.isBlank()
                || newPassword.isBlank()) {

            return Map.of(
                    "success", false,
                    "message",
                    "Please fill in all password fields."
            );
        }

        if (newPassword.length() < 8) {
            return Map.of(
                    "success", false,
                    "message",
                    "Password must be at least 8 characters."
            );
        }

        if (newPassword.length() > 72) {
            return Map.of(
                    "success", false,
                    "message",
                    "Password must be 72 characters or less."
            );
        }

        User user =
                userDAO.getUserById(userId);

        if (user == null) {
            return Map.of(
                    "success", false,
                    "message",
                    "User account not found."
            );
        }

        if (!PasswordUtil.checkPassword(
                currentPassword,
                user.getPassword())) {

            return Map.of(
                    "success", false,
                    "message",
                    "Current password is incorrect."
            );
        }

        if (PasswordUtil.checkPassword(
                newPassword,
                user.getPassword())) {

            return Map.of(
                    "success", false,
                    "message",
                    "New password must be different."
            );
        }

        userDAO.updatePassword(
                userId,
                PasswordUtil.hashPassword(newPassword)
        );

        return Map.of(
                "success", true,
                "message",
                "Password updated successfully."
        );
    }

    // Delete account
    @PostMapping("/delete")
    public Map<String, Object> deleteAccount(
            HttpSession session) {

        Integer userId =
                (Integer) session.getAttribute(
                        "loggedInUserId");

        if (userId == null) {
            return Map.of(
                    "success", false,
                    "message",
                    "Please log in first."
            );
        }

        User user =
                userDAO.getUserById(userId);

        if (user == null) {
            return Map.of(
                    "success", false,
                    "message",
                    "User account not found."
            );
        }

        emailVerificationDAO.deleteTokensForUser(
                userId);

        loginVerificationDAO.deleteTokensForUser(
                userId);

        userDAO.deleteUser(userId);

        session.invalidate();

        return Map.of(
                "success", true,
                "message",
                "Account deleted successfully."
        );
    }
}