package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.CartDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.CartItem;
import io.virinchi.jesterpcs.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    private final CartDAO cartDAO;
    private final UserDAO userDAO;

    public CartController(
            CartDAO cartDAO,
            UserDAO userDAO) {

        this.cartDAO = cartDAO;
        this.userDAO = userDAO;
    }

    private Integer getLoggedInUserId(HttpSession session) {
        return (Integer) session.getAttribute("loggedInUserId");
    }

    private User getLoggedInUser(HttpSession session) {

        Integer userId = getLoggedInUserId(session);

        return userId == null
                ? null
                : userDAO.getUserById(userId);
    }

    private boolean isAdmin(HttpSession session) {

        User user = getLoggedInUser(session);

        return user != null
                && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private double getSubtotal(List<CartItem> cartItems) {

        return cartItems.stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item ->
                        item.getProduct().getPrice()
                                * item.getQuantity())
                .sum();
    }

    @GetMapping("/cart")
    public String cart(
            HttpSession session,
            Model model) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getLoggedInUserId(session);

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

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute(
                "itemCount",
                cartDAO.getCartItemCount(userId)
        );
        model.addAttribute(
                "subtotal",
                getSubtotal(cartItems)
        );

        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam("productId") int productId,
            HttpSession session) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        cartDAO.addItem(userId, productId);

        return "redirect:/cart";
    }

    @PostMapping("/cart/add-ajax")
    @ResponseBody
    public Map<String, Object> addToCartAjax(
            @RequestParam("productId") int productId,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        if (isAdmin(session)) {
            result.put("success", false);
            result.put("loggedIn", true);
            result.put("admin", true);

            return result;
        }

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            result.put("success", false);
            result.put("loggedIn", false);

            return result;
        }

        cartDAO.addItem(userId, productId);

        result.put("success", true);
        result.put("loggedIn", true);
        result.put(
                "count",
                cartDAO.getCartItemCount(userId)
        );

        return result;
    }

    @PostMapping("/cart/update")
    public String updateCart(
            @RequestParam("productId") int productId,
            @RequestParam("quantity") int quantity,
            HttpSession session) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        cartDAO.updateQuantity(
                userId,
                productId,
                quantity
        );

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam("productId") int productId,
            HttpSession session) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        cartDAO.removeItem(userId, productId);

        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        cartDAO.clearCart(userId);

        return "redirect:/cart";
    }

    @GetMapping("/cart/count")
    @ResponseBody
    public int cartCount(HttpSession session) {

        if (isAdmin(session)) {
            return 0;
        }

        Integer userId = getLoggedInUserId(session);

        return userId == null
                ? 0
                : cartDAO.getCartItemCount(userId);
    }

    @PostMapping("/cart/add-build")
    public String addBuildToCart(
            @RequestParam("productIds") List<Integer> productIds,
            HttpSession session) {

        if (isAdmin(session)) {
            return "redirect:/admin";
        }

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        for (Integer productId : productIds) {

            if (productId != null) {
                cartDAO.addItem(userId, productId);
            }
        }

        return "redirect:/cart";
    }
}