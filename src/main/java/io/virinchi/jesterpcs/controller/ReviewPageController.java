package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.ProductDAO;
import io.virinchi.jesterpcs.dao.ReviewDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.Product;
import io.virinchi.jesterpcs.model.Review;
import io.virinchi.jesterpcs.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReviewPageController {

    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final ReviewDAO reviewDAO;

    public ReviewPageController(
            UserDAO userDAO,
            ProductDAO productDAO,
            ReviewDAO reviewDAO) {

        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.reviewDAO = reviewDAO;
    }

    private User getUser(
            HttpSession session) {

        Integer userId =
                (Integer) session.getAttribute(
                        "loggedInUserId"
                );

        if (userId == null) {
            return null;
        }

        return userDAO.getUserById(userId);
    }

    @GetMapping({
            "/account/my-reviews",
            "/my-reviews"
    })
    public String myReviews(
            HttpSession session,
            Model model) {

        User user =
                getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(
                user.getRole())) {

            return "redirect:/admin";
        }

        List<Review> reviews =
                reviewDAO.getReviewsByUserId(
                        user.getUserId()
                );

        Map<Integer, Product> productsByReview =
                new HashMap<>();

        for (Review review : reviews) {

            Product product =
                    productDAO.getProductById(
                            review.getProductId()
                    );

            if (product != null) {

                productsByReview.put(
                        review.getReviewId(),
                        product
                );
            }
        }

        model.addAttribute(
                "user",
                user
        );

        model.addAttribute(
                "reviews",
                reviews
        );

        model.addAttribute(
                "productsByReview",
                productsByReview
        );

        return "my-reviews";
    }
}