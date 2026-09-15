package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.OrderDAO;
import io.virinchi.jesterpcs.dao.PrebuiltDAO;
import io.virinchi.jesterpcs.dao.ProductDAO;
import io.virinchi.jesterpcs.dao.ReviewDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.dao.WishlistDAO;
import io.virinchi.jesterpcs.model.Order;
import io.virinchi.jesterpcs.model.PreBuilts;
import io.virinchi.jesterpcs.model.Product;
import io.virinchi.jesterpcs.model.Review;
import io.virinchi.jesterpcs.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final PrebuiltDAO prebuiltDAO;
    private final ReviewDAO reviewDAO;
    private final WishlistDAO wishlistDAO;

    public HomeController(
            UserDAO userDAO,
            ProductDAO productDAO,
            OrderDAO orderDAO,
            PrebuiltDAO prebuiltDAO,
            ReviewDAO reviewDAO,
            WishlistDAO wishlistDAO) {

        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
        this.prebuiltDAO = prebuiltDAO;
        this.reviewDAO = reviewDAO;
        this.wishlistDAO = wishlistDAO;
    }

    private Integer getUserId(HttpSession session) {
        return (Integer) session.getAttribute("loggedInUserId");
    }

    private User getUser(HttpSession session) {

        Integer userId = getUserId(session);

        return userId == null
                ? null
                : userDAO.getUserById(userId);
    }

    @GetMapping("/")
    public String index(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user != null) {
            model.addAttribute("user", user);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/marketplace")
    public String marketplace(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user != null) {
            model.addAttribute("user", user);

            model.addAttribute(
                    "wishlistProductIds",
                    wishlistDAO.getWishlist(user.getUserId())
                            .stream()
                            .map(item -> item.getProductId())
                            .toList()
            );
        } else {
            model.addAttribute(
                    "wishlistProductIds",
                    List.of()
            );
        }

        model.addAttribute(
                "products",
                productDAO.getAllProducts()
        );

        return "marketplace";
    }

    @PostMapping("/wishlist/add-ajax")
    @ResponseBody
    public Map<String, Object> addWishlist(
            int productId,
            HttpSession session) {

        Map<String, Object> result =
                new HashMap<>();

        Integer userId =
                getUserId(session);

        if (userId == null) {
            result.put("success", false);
            result.put("loggedIn", false);
            return result;
        }

        wishlistDAO.addItem(
                userId,
                productId
        );

        result.put("success", true);
        result.put("loggedIn", true);
        result.put(
                "count",
                wishlistDAO.getWishlistCount(userId)
        );

        return result;
    }

    @PostMapping("/wishlist/remove-ajax")
    @ResponseBody
    public Map<String, Object> removeWishlist(
            int productId,
            HttpSession session) {

        Map<String, Object> result =
                new HashMap<>();

        Integer userId =
                getUserId(session);

        if (userId == null) {
            result.put("success", false);
            result.put("loggedIn", false);
            return result;
        }

        wishlistDAO.removeItem(
                userId,
                productId
        );

        result.put("success", true);
        result.put("loggedIn", true);
        result.put(
                "count",
                wishlistDAO.getWishlistCount(userId)
        );

        return result;
    }

    @GetMapping("/wishlist")
    public String wishlist(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        }

        model.addAttribute(
                "user",
                user
        );

        model.addAttribute(
                "wishlist",
                wishlistDAO.getWishlist(
                        user.getUserId()
                )
        );

        return "wishlist";
    }

    @GetMapping("/custom")
    public String custom(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user != null &&
                "ADMIN".equalsIgnoreCase(user.getRole())) {

            return "redirect:/admin";
        }

        model.addAttribute(
                "products",
                productDAO.getAllProducts()
        );

        return "custom";
    }

    @GetMapping("/builds")
    public String builds(Model model) {

        List<Product> products =
                productDAO.getAllProducts();

        List<Product> budget =
                getProductsByTier(
                        products,
                        "budget");

        List<Product> mid =
                getProductsByTier(
                        products,
                        "mid");

        List<Product> max =
                getProductsByTier(
                        products,
                        "max");

        model.addAttribute(
                "budgetProducts",
                budget);

        model.addAttribute(
                "midProducts",
                mid);

        model.addAttribute(
                "maxProducts",
                max);

        model.addAttribute(
                "budgetTotal",
                String.format(
                        "%,.0f",
                        getBuildTotal(budget))
        );

        model.addAttribute(
                "midTotal",
                String.format(
                        "%,.0f",
                        getBuildTotal(mid))
        );

        model.addAttribute(
                "maxTotal",
                String.format(
                        "%,.0f",
                        getBuildTotal(max))
        );

        // Get admin-created pre-built PCs from database
        List<PreBuilts> prebuilts =
                prebuiltDAO.getAllPrebuilts();

        model.addAttribute(
                "prebuilts",
                prebuilts
        );

        return "builds";
    }

    private List<Product> getProductsByTier(
            List<Product> products,
            String tier) {

        return products.stream()
                .filter(product ->
                        tier.equalsIgnoreCase(
                                product.getPerformanceTier()))
                .sorted(
                        Comparator.comparingInt(
                                Product::getCategoryId))
                .toList();
    }

    private double getBuildTotal(
            List<Product> products) {

        return products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    @GetMapping("/account")
    public String account(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        }

        List<Order> orders =
                orderDAO.getOrdersByUserId(
                        user.getUserId());

        Map<Integer, List<Map<String, Object>>>
                orderItemsByOrder =
                new HashMap<>();

        for (Order order : orders) {

            orderItemsByOrder.put(
                    order.getOrderId(),
                    orderDAO.getOrderItems(
                            order.getOrderId())
            );
        }

        model.addAttribute(
                "user",
                user);

        model.addAttribute(
                "orders",
                orders);

        model.addAttribute(
                "orderItemsByOrder",
                orderItemsByOrder);

        return "accounts";
    }

    @PostMapping("/account/orders/{id}/cancel")
    public String cancelOrder(
            @PathVariable("id") int id,
            HttpSession session) {

        Integer userId =
                getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        User user =
                getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(
                user.getRole())) {

            return "redirect:/admin";
        }

        orderDAO.cancelOrder(
                id,
                userId);

        return "redirect:/account";
    }

    @GetMapping("/account/reviews")
    public String accountReviews(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
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

    @GetMapping("/about")
    public String about() {
        return "aboutus";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    // Give the header access to the logged-in user
    @GetMapping("/header.html")
    public String header(
            HttpSession session,
            Model model) {

        User user = getUser(session);

        if (user != null) {
            model.addAttribute(
                    "user",
                    user);
        }

        return "header";
    }

    @GetMapping("/footer.html")
    public String footer() {
        return "footer";
    }

    @GetMapping("/product/{id}")
    public String productDetails(
            @PathVariable("id") int id,
            HttpSession session,
            Model model) {

        Product product =
                productDAO.getProductById(id);

        if (product == null) {
            return "redirect:/marketplace";
        }

        User user =
                getUser(session);

        if (user != null) {
            model.addAttribute(
                    "user",
                    user);
        }

        Integer userId =
                getUserId(session);

        int loggedInUserId =
                userId == null ? 0 : userId;

        // Get product reviews
        List<Review> reviews =
                reviewDAO.getReviewsByProductId(
                        id,
                        loggedInUserId
                );

        // Get comments for each review
        Map<Integer,
                List<io.virinchi.jesterpcs.model.ReviewComment>>
                reviewCommentsByReview =
                new HashMap<>();

        for (Review review : reviews) {

            reviewCommentsByReview.put(
                    review.getReviewId(),
                    reviewDAO.getCommentsByReviewId(
                            review.getReviewId())
            );
        }

        model.addAttribute(
                "reviews",
                reviews
        );

        model.addAttribute(
                "reviewCommentsByReview",
                reviewCommentsByReview
        );

        model.addAttribute(
                "averageRating",
                reviewDAO.getAverageRating(id)
        );

        model.addAttribute(
                "ratingBreakdown",
                reviewDAO.getRatingBreakdown(id)
        );

        boolean canReview = false;
        boolean alreadyReviewed = false;

        if (userId != null) {

            canReview =
                    reviewDAO.hasPurchasedProduct(
                            userId,
                            id
                    );

            alreadyReviewed =
                    reviewDAO.hasReviewedProduct(
                            userId,
                            id
                    );
        }

        model.addAttribute(
                "canReview",
                canReview
        );

        model.addAttribute(
                "alreadyReviewed",
                alreadyReviewed
        );

        model.addAttribute(
                "product",
                product);

        return "product-details";
    }

    @GetMapping("/product/{id}/reviews")
    public String productReviews(
            @PathVariable("id") int id,
            HttpSession session,
            Model model) {

        Product product =
                productDAO.getProductById(id);

        if (product == null) {
            return "redirect:/marketplace";
        }

        User user =
                getUser(session);

        if (user != null) {
            model.addAttribute(
                    "user",
                    user);
        }

        return "redirect:/product/" +
                id +
                "#reviews";
    }
}