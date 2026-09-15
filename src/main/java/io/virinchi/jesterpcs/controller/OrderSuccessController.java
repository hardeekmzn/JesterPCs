package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.OrderDAO;
import io.virinchi.jesterpcs.model.Order;
import io.virinchi.jesterpcs.model.User;
import io.virinchi.jesterpcs.dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderSuccessController {

    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    public OrderSuccessController(
            OrderDAO orderDAO,
            UserDAO userDAO) {

        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
    }

    @GetMapping("/order-success")
    public String orderSuccess(
            @RequestParam("orderId") int orderId,
            HttpSession session,
            Model model) {

        Integer userId =
                (Integer) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        User user = userDAO.getUserById(userId);

        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        }

        Order order =
                orderDAO.getOrderById(orderId);

        if (order == null || order.getUserId() != userId) {
            return "redirect:/marketplace";
        }

        model.addAttribute("order", order);

        // Get the products included in this order
        model.addAttribute(
                "orderItems",
                orderDAO.getOrderItems(orderId)
        );

        return "order-success";
    }
}