package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.CategoryDAO;
import io.virinchi.jesterpcs.dao.OrderDAO;
import io.virinchi.jesterpcs.dao.PrebuiltDAO;
import io.virinchi.jesterpcs.dao.ProductDAO;
import io.virinchi.jesterpcs.dao.UserDAO;
import io.virinchi.jesterpcs.model.Category;
import io.virinchi.jesterpcs.model.Order;
import io.virinchi.jesterpcs.model.PreBuilts;
import io.virinchi.jesterpcs.model.Product;
import io.virinchi.jesterpcs.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.List;

@Controller
public class AdminController {

    private static final String TEXT_PATTERN =
            "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,'()&+/_-]{1,149}$";

    private static final String BRAND_PATTERN =
            "^[\\p{L}\\p{N}][\\p{L}\\p{N} .&'_-]{1,49}$";

    private static final String KEY_PATTERN =
            "^[A-Za-z0-9_-]{3,50}$";

    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final OrderDAO orderDAO;
    private final PrebuiltDAO prebuiltDAO;

    public AdminController(
            UserDAO userDAO,
            ProductDAO productDAO,
            CategoryDAO categoryDAO,
            OrderDAO orderDAO,
            PrebuiltDAO prebuiltDAO) {

        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.orderDAO = orderDAO;
        this.prebuiltDAO = prebuiltDAO;
    }

    // Check whether logged-in user is an admin
    private User getAdminUser(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return null;
        }

        return user;
    }

    // Redirect non-admin users
    private String adminRedirect(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        return "redirect:/";
    }

    // Check blank text
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Check text length
    private boolean validLength(String value, int min, int max) {

        if (isBlank(value)) {
            return false;
        }

        int length = value.trim().length();

        return length >= min && length <= max;
    }

    // Check image URL
    private boolean validUrl(String value) {

        if (isBlank(value)) {
            return false;
        }

        try {
            URI uri = URI.create(value.trim());

            return ("http".equalsIgnoreCase(uri.getScheme()) ||
                    "https".equalsIgnoreCase(uri.getScheme())) &&
                    uri.getHost() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Prepare product form
    private void prepareProductForm(Model model, User user, Product product) {

        model.addAttribute("user", user);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryDAO.getAllCategories());
    }

    // Prepare pre-built form
    private void preparePrebuiltForm(
            Model model,
            User user,
            PreBuilts prebuilt,
            List<Integer> selectedProductIds) {

        model.addAttribute("user", user);
        model.addAttribute("prebuilt", prebuilt);
        model.addAttribute("products", productDAO.getAllProducts());
        model.addAttribute("categories", categoryDAO.getAllCategories());
        model.addAttribute("selectedProductIds", selectedProductIds);
    }

    // Check whether pre-built key already exists
    private boolean prebuiltKeyExists(String key, int prebuiltId) {

        List<PreBuilts> prebuilts = prebuiltDAO.getAllPrebuilts();

        for (PreBuilts prebuilt : prebuilts) {

            if (prebuilt.getPrebuiltId() != prebuiltId &&
                    prebuilt.getKey() != null &&
                    prebuilt.getKey().equalsIgnoreCase(key)) {

                return true;
            }
        }

        return false;
    }

    // Validate product
    private String validateProduct(Product product) {

        String productName = product.getProductName();
        String brand = product.getBrand();
        String imageUrl = product.getImageUrl();
        String description = product.getDescription();
        String specifications = product.getSpecifications();

        if (!validLength(productName, 2, 150) ||
                !productName.trim().matches(TEXT_PATTERN)) {
            return "Product name must be 2 to 150 characters.";
        }

        if (!validLength(brand, 2, 50) ||
                !brand.trim().matches(BRAND_PATTERN)) {
            return "Brand must be 2 to 50 characters.";
        }

        if (product.getCategoryId() <= 0 ||
                categoryDAO.getCategoryById(product.getCategoryId()) == null) {
            return "Please select a valid category.";
        }

        if (product.getPrice() <= 0) {
            return "Price must be greater than 0.";
        }

        if (product.getStockQuantity() < 0) {
            return "Stock quantity cannot be negative.";
        }

        if (!validUrl(imageUrl) || imageUrl.trim().length() > 500) {
            return "Please enter a valid image URL.";
        }

        if (description != null && description.trim().length() > 2000) {
            return "Description cannot exceed 2000 characters.";
        }

        if (specifications != null && specifications.trim().length() > 2000) {
            return "Specifications cannot exceed 2000 characters.";
        }

        if (product.getRamSpeed() != null && product.getRamSpeed() < 0) {
            return "RAM speed cannot be negative.";
        }

        if (product.getWattage() != null && product.getWattage() < 0) {
            return "Wattage cannot be negative.";
        }

        if (product.getGpuLength() != null && product.getGpuLength() < 0) {
            return "GPU length cannot be negative.";
        }

        if (product.getCoolerHeight() != null && product.getCoolerHeight() < 0) {
            return "Cooler height cannot be negative.";
        }

        product.setProductName(productName.trim());
        product.setBrand(brand.trim());
        product.setImageUrl(imageUrl.trim());

        if (description != null) {
            product.setDescription(description.trim());
        }

        if (specifications != null) {
            product.setSpecifications(specifications.trim());
        }

        return null;
    }

    // Validate pre-built PC
    private String validatePrebuilt(
            PreBuilts prebuilt,
            List<Integer> productIds) {

        String name = prebuilt.getName();
        String key = prebuilt.getKey();
        String title = prebuilt.getTitle();
        String description = prebuilt.getDescription();
        String targetResolution = prebuilt.getTargetResolution();
        String performance = prebuilt.getPerformance();
        String bestFor = prebuilt.getBestFor();
        String imageUrl = prebuilt.getImageUrl();

        if (!validLength(name, 2, 100)) {
            return "Build name must be 2 to 100 characters.";
        }

        if (!key.trim().matches(KEY_PATTERN)) {
            return "Build identifier can only contain letters, numbers, _ and -.";
        }

        if (prebuiltKeyExists(key.trim(), prebuilt.getPrebuiltId())) {
            return "Build identifier already exists.";
        }

        if (!isBlank(title) && title.trim().length() > 150) {
            return "Build title cannot exceed 150 characters.";
        }

        if (!isBlank(description) && description.trim().length() > 3000) {
            return "Build description cannot exceed 3000 characters.";
        }

        if (!isBlank(targetResolution) &&
                targetResolution.trim().length() > 50) {
            return "Target resolution cannot exceed 50 characters.";
        }

        if (!isBlank(performance) &&
                performance.trim().length() > 50) {
            return "Performance level cannot exceed 50 characters.";
        }

        if (!isBlank(bestFor) &&
                bestFor.trim().length() > 100) {
            return "Best For cannot exceed 100 characters.";
        }

        if (!isBlank(imageUrl)) {

            if (!validUrl(imageUrl) || imageUrl.trim().length() > 500) {
                return "Please enter a valid PC image URL.";
            }
        }

        if (productIds == null || productIds.isEmpty()) {
            return "Select at least one component.";
        }

        prebuilt.setName(name.trim());
        prebuilt.setKey(key.trim());

        if (!isBlank(title)) {
            prebuilt.setTitle(title.trim());
        }

        if (!isBlank(description)) {
            prebuilt.setDescription(description.trim());
        }

        if (!isBlank(targetResolution)) {
            prebuilt.setTargetResolution(targetResolution.trim());
        }

        if (!isBlank(performance)) {
            prebuilt.setPerformance(performance.trim());
        }

        if (!isBlank(bestFor)) {
            prebuilt.setBestFor(bestFor.trim());
        }

        if (!isBlank(imageUrl)) {
            prebuilt.setImageUrl(imageUrl.trim());
        }

        return null;
    }

    // Admin dashboard
    @GetMapping("/admin")
    public String dashboard(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        model.addAttribute("user", user);

        return "admin";
    }

    // Product routes
    @GetMapping("/admin/products")
    public String products(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        model.addAttribute("user", user);
        model.addAttribute("products", productDAO.getAllProducts());

        return "admin-products";
    }

    @GetMapping("/admin/products/add")
    public String addProduct(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        prepareProductForm(model, user, new Product());

        return "admin-product-form";
    }

    @PostMapping("/admin/products/add")
    public String saveProduct(
            @ModelAttribute Product product,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        String error = validateProduct(product);

        if (error != null) {
            prepareProductForm(model, user, product);
            model.addAttribute("error", error);
            return "admin-product-form";
        }

        productDAO.addProduct(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String editProduct(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        Product product = productDAO.getProductById(id);

        if (product == null) {
            return "redirect:/admin/products";
        }

        prepareProductForm(model, user, product);

        return "admin-product-form";
    }

    @PostMapping("/admin/products/edit/{id}")
    public String updateProduct(
            @PathVariable int id,
            @ModelAttribute Product product,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        product.setProductId(id);

        String error = validateProduct(product);

        if (error != null) {
            prepareProductForm(model, user, product);
            model.addAttribute("error", error);
            return "admin-product-form";
        }

        productDAO.updateProduct(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(
            @PathVariable int id,
            HttpSession session) {

        if (getAdminUser(session) == null) {
            return adminRedirect(session);
        }

        productDAO.deleteProduct(id);

        return "redirect:/admin/products";
    }

    // Category routes
    @GetMapping("/admin/categories")
    public String categories(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        model.addAttribute("user", user);
        model.addAttribute("categories", categoryDAO.getAllCategories());

        return "admin-categories";
    }

    @PostMapping("/admin/categories/add")
    public String saveCategory(
            @RequestParam String categoryName,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        categoryName = categoryName == null ? "" : categoryName.trim();

        if (!validLength(categoryName, 2, 50)) {

            model.addAttribute("user", user);
            model.addAttribute("categories", categoryDAO.getAllCategories());
            model.addAttribute("error",
                    "Category name must be 2 to 50 characters.");

            return "admin-categories";
        }

        if (categoryDAO.categoryExists(categoryName)) {

            model.addAttribute("user", user);
            model.addAttribute("categories", categoryDAO.getAllCategories());
            model.addAttribute("error",
                    "Category already exists.");

            return "admin-categories";
        }

        categoryDAO.addCategory(categoryName);

        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String editCategory(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        Category category = categoryDAO.getCategoryById(id);

        if (category == null) {
            return "redirect:/admin/categories";
        }

        model.addAttribute("user", user);
        model.addAttribute("category", category);

        return "admin-category-edit";
    }

    @PostMapping("/admin/categories/edit/{id}")
    public String updateCategory(
            @PathVariable int id,
            @RequestParam String categoryName,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        Category category = categoryDAO.getCategoryById(id);

        if (category == null) {
            return "redirect:/admin/categories";
        }

        categoryName = categoryName == null ? "" : categoryName.trim();

        if (!validLength(categoryName, 2, 50)) {

            model.addAttribute("user", user);
            model.addAttribute("category", category);
            model.addAttribute("error",
                    "Category name must be 2 to 50 characters.");

            return "admin-category-edit";
        }

        if (categoryDAO.categoryExists(categoryName, id)) {

            model.addAttribute("user", user);
            model.addAttribute("category", category);
            model.addAttribute("error",
                    "Category already exists.");

            return "admin-category-edit";
        }

        categoryDAO.updateCategory(id, categoryName);

        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        if (categoryDAO.hasProducts(id)) {

            model.addAttribute("user", user);
            model.addAttribute("categories", categoryDAO.getAllCategories());
            model.addAttribute(
                    "error",
                    "This category cannot be deleted because products are using it.");

            return "admin-categories";
        }

        categoryDAO.deleteCategory(id);

        return "redirect:/admin/categories";
    }

    // Pre-built PC routes
    @GetMapping("/admin/prebuilts")
    public String prebuilts(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        model.addAttribute("user", user);
        model.addAttribute("prebuilts", prebuiltDAO.getAllPrebuilts());

        return "admin-prebuilts";
    }

    @GetMapping("/admin/prebuilts/add")
    public String addPrebuilt(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        preparePrebuiltForm(
                model,
                user,
                new PreBuilts(),
                List.of());

        return "admin-prebuilt-form";
    }

    @PostMapping("/admin/prebuilts/add")
    public String savePrebuilt(
            @ModelAttribute PreBuilts prebuilt,
            @RequestParam(
                    value = "productIds",
                    required = false
            ) List<Integer> productIds,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        String error = validatePrebuilt(prebuilt, productIds);

        if (error != null) {

            List<Integer> selectedIds =
                    productIds == null ? List.of() : productIds;

            preparePrebuiltForm(
                    model,
                    user,
                    prebuilt,
                    selectedIds);

            model.addAttribute("error", error);

            return "admin-prebuilt-form";
        }

        int prebuiltId = prebuiltDAO.addPrebuilt(prebuilt);

        prebuiltDAO.updatePrebuiltProducts(
                prebuiltId,
                productIds);

        return "redirect:/admin/prebuilts";
    }

    @GetMapping("/admin/prebuilts/edit/{id}")
    public String editPrebuilt(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        PreBuilts prebuilt =
                prebuiltDAO.getPrebuiltById(id);

        if (prebuilt == null) {
            return "redirect:/admin/prebuilts";
        }

        List<Integer> selectedProductIds =
                prebuilt.getProducts()
                        .stream()
                        .map(Product::getProductId)
                        .toList();

        preparePrebuiltForm(
                model,
                user,
                prebuilt,
                selectedProductIds);

        return "admin-prebuilt-form";
    }

    @PostMapping("/admin/prebuilts/edit/{id}")
    public String updatePrebuilt(
            @PathVariable int id,
            @ModelAttribute PreBuilts prebuilt,
            @RequestParam(
                    value = "productIds",
                    required = false
            ) List<Integer> productIds,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        prebuilt.setPrebuiltId(id);

        String error = validatePrebuilt(prebuilt, productIds);

        if (error != null) {

            List<Integer> selectedIds =
                    productIds == null ? List.of() : productIds;

            preparePrebuiltForm(
                    model,
                    user,
                    prebuilt,
                    selectedIds);

            model.addAttribute("error", error);

            return "admin-prebuilt-form";
        }

        prebuiltDAO.updatePrebuilt(prebuilt);

        prebuiltDAO.updatePrebuiltProducts(
                id,
                productIds);

        return "redirect:/admin/prebuilts";
    }

    @GetMapping("/admin/prebuilts/delete/{id}")
    public String deletePrebuilt(
            @PathVariable int id,
            HttpSession session) {

        if (getAdminUser(session) == null) {
            return adminRedirect(session);
        }

        prebuiltDAO.deletePrebuilt(id);

        return "redirect:/admin/prebuilts";
    }

    // Order routes
    @GetMapping("/admin/orders")
    public String orders(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        model.addAttribute("user", user);
        model.addAttribute("orders", orderDAO.getAllOrders());

        return "admin-orders";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable int id,
            @RequestParam String status,
            HttpSession session) {

        if (getAdminUser(session) == null) {
            return adminRedirect(session);
        }

        orderDAO.updateStatus(id, status);

        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String orderDetails(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        Order order = orderDAO.getOrderById(id);

        if (order == null) {
            return "redirect:/admin/orders";
        }

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute(
                "orderItems",
                orderDAO.getOrderItems(id));

        return "admin-order-details";
    }

    // User routes
    @GetMapping("/admin/users")
    public String users(
            HttpSession session,
            Model model) {

        User user = getAdminUser(session);

        if (user == null) {
            return adminRedirect(session);
        }

        model.addAttribute("user", user);
        model.addAttribute("users", userDAO.getAllUsers());

        model.addAttribute(
                "totalUsers",
                userDAO.getUserCount());

        model.addAttribute(
                "verifiedUsers",
                userDAO.getVerifiedUserCount());

        model.addAttribute(
                "unverifiedUsers",
                userDAO.getUnverifiedUserCount());

        return "admin-users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(
            @PathVariable int id,
            HttpSession session) {

        User admin = getAdminUser(session);

        if (admin == null) {
            return adminRedirect(session);
        }

        userDAO.deleteUserByAdmin(
                id,
                admin.getUserId());

        return "redirect:/admin/users";
    }
}