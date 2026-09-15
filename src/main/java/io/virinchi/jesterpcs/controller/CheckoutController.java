package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.CartDAO;
import io.virinchi.jesterpcs.dao.OrderDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.CartItem;
import io.virinchi.jesterpcs.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.Pattern;

@Controller
public class CheckoutController {

    private static final double DELIVERY = 200;

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(?:\\+977[- ]?)?(?:98|97|96)\\d{8}$");

    private final CartDAO cartDAO;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    public CheckoutController(
            CartDAO cartDAO,
            OrderDAO orderDAO,
            UserDAO userDAO) {

        this.cartDAO = cartDAO;
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
    }

    private Integer getUserId(HttpSession session) {

        return (Integer) session.getAttribute(
                "loggedInUserId");
    }

    private User getUser(HttpSession session) {

        Integer userId = getUserId(session);

        return userId == null
                ? null
                : userDAO.getUserById(userId);
    }

    private boolean isAdmin(HttpSession session) {

        User user = getUser(session);

        return user != null
                && "ADMIN".equalsIgnoreCase(
                user.getRole());
    }

    private double getSubtotal(
            List<CartItem> cartItems) {

        return cartItems.stream()
                .filter(item ->
                        item.getProduct() != null)
                .mapToDouble(item ->
                        item.getProduct().getPrice()
                                * item.getQuantity())
                .sum();
    }

    // Check whether all cart quantities are still in stock
    private boolean hasEnoughStock(
            List<CartItem> cartItems) {

        for (CartItem item : cartItems) {

            if (item.getProduct() == null) {
                return false;
            }

            if (item.getQuantity()
                    > item.getProduct()
                    .getStockQuantity()) {

                return false;
            }
        }

        return true;
    }

    // Check checkout address and phone
    private String validateCheckoutData(
            String address,
            String phone) {

        if (address == null ||
                address.trim().isBlank()) {

            return "Delivery address is required.";
        }

        if (phone == null ||
                phone.trim().isBlank()) {

            return "Phone number is required.";
        }

        address = address.trim();
        phone = phone.trim();

        if (address.length() < 5) {
            return "Delivery address must be at least 5 characters.";
        }

        if (address.length() > 200) {
            return "Delivery address must be 200 characters or less.";
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "Please enter a valid Nepal mobile number.";
        }

        return null;
    }

    @GetMapping("/checkout")
    public String checkout(
            HttpSession session,
            Model model) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        List<CartItem> cartItems =
                cartDAO.getCartItems(userId);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // Check stock before showing checkout
        if (!hasEnoughStock(cartItems)) {
            return "redirect:/cart";
        }

        double subtotal =
                getSubtotal(cartItems);

        double total =
                subtotal + DELIVERY;

        model.addAttribute(
                "user",
                user);

        model.addAttribute(
                "cartItems",
                cartItems);

        model.addAttribute(
                "subtotal",
                subtotal);

        model.addAttribute(
                "delivery",
                DELIVERY);

        model.addAttribute(
                "total",
                total);

        return "checkout";
    }

    @PostMapping("/checkout/payment")
    public String payment(
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            HttpSession session,
            Model model) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId =
                getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems =
                cartDAO.getCartItems(userId);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // Check stock again before payment
        if (!hasEnoughStock(cartItems)) {
            return "redirect:/cart";
        }

        address =
                address == null
                        ? ""
                        : address.trim();

        phone =
                phone == null
                        ? ""
                        : phone.trim();

        String validationError =
                validateCheckoutData(
                        address,
                        phone);

        if (validationError != null) {

            User user =
                    userDAO.getUserById(userId);

            double subtotal =
                    getSubtotal(cartItems);

            double total =
                    subtotal + DELIVERY;

            model.addAttribute(
                    "user",
                    user);

            model.addAttribute(
                    "cartItems",
                    cartItems);

            model.addAttribute(
                    "subtotal",
                    subtotal);

            model.addAttribute(
                    "delivery",
                    DELIVERY);

            model.addAttribute(
                    "total",
                    total);

            model.addAttribute(
                    "address",
                    address);

            model.addAttribute(
                    "phone",
                    phone);

            model.addAttribute(
                    "error",
                    validationError);

            return "checkout";
        }

        double subtotal =
                getSubtotal(cartItems);

        double total =
                subtotal + DELIVERY;

        session.setAttribute(
                "checkoutAddress",
                address);

        session.setAttribute(
                "checkoutPhone",
                phone);

        model.addAttribute(
                "cartItems",
                cartItems);

        model.addAttribute(
                "subtotal",
                subtotal);

        model.addAttribute(
                "delivery",
                DELIVERY);

        model.addAttribute(
                "total",
                total);

        model.addAttribute(
                "address",
                address);

        model.addAttribute(
                "phone",
                phone);

        return "payment";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            HttpSession session) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId =
                getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        String address =
                (String) session.getAttribute(
                        "checkoutAddress");

        String phone =
                (String) session.getAttribute(
                        "checkoutPhone");

        if (address == null ||
                phone == null) {

            return "redirect:/checkout";
        }

        String validationError =
                validateCheckoutData(
                        address,
                        phone);

        if (validationError != null) {
            return "redirect:/checkout";
        }

        List<CartItem> cartItems =
                cartDAO.getCartItems(userId);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        if (!hasEnoughStock(cartItems)) {
            return "redirect:/cart";
        }

        double total =
                getSubtotal(cartItems)
                        + DELIVERY;

        try {

            int orderId =
                    orderDAO.createOrder(
                            userId,
                            address,
                            phone,
                            total
                    );

            session.removeAttribute(
                    "checkoutAddress");

            session.removeAttribute(
                    "checkoutPhone");

            return "redirect:/order-success?orderId="
                    + orderId;

        } catch (IllegalStateException e) {

            // Stock changed before the final order was placed
            return "redirect:/cart";
        }
    }
}