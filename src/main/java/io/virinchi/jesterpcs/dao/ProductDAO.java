package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.Product;
import io.virinchi.jesterpcs.util.EmailService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDAO {

    private final JdbcTemplate jdbcTemplate;
    private final EmailService emailService;

    public ProductDAO(
            JdbcTemplate jdbcTemplate,
            EmailService emailService) {

        this.jdbcTemplate = jdbcTemplate;
        this.emailService = emailService;
    }

    // Get all products
    public List<Product> getAllProducts() {

        String sql = """
        select
            p.*,
            c.category_name
        from products p
        join categories c
            on p.category_id = c.category_id
        order by p.product_id desc
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapProduct(rs)
        );
    }

    // Get one product
    public Product getProductById(int productId) {

        String sql = """
        select
            p.*,
            c.category_name
        from products p
        join categories c
            on p.category_id = c.category_id
        where p.product_id = ?
        """;

        List<Product> products =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> mapProduct(rs),
                        productId
                );

        return products.isEmpty()
                ? null
                : products.get(0);
    }

    // Add product
    public void addProduct(Product product) {

        String sql = """
                insert into products (
                    category_id,
                    product_name,
                    brand,
                    price,
                    description,
                    specifications,
                    image_url,
                    stock_quantity,
                    socket_type,
                    ram_type,
                    ram_speed,
                    storage_interface,
                    wattage,
                    gpu_length,
                    cooler_height,
                    performance_tier
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                product.getCategoryId(),
                product.getProductName(),
                product.getBrand(),
                product.getPrice(),
                product.getDescription(),
                product.getSpecifications(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.getSocketType(),
                product.getRamType(),
                product.getRamSpeed(),
                product.getStorageInterface(),
                product.getWattage(),
                product.getGpuLength(),
                product.getCoolerHeight(),
                product.getPerformanceTier()
        );
    }

    // Update product
    public void updateProduct(Product product) {

        Integer oldStock =
                jdbcTemplate.queryForObject(
                        """
                        select stock_quantity
                        from products
                        where product_id = ?
                        """,
                        Integer.class,
                        product.getProductId());

        String sql = """
                update products
                set
                    category_id = ?,
                    product_name = ?,
                    brand = ?,
                    price = ?,
                    description = ?,
                    specifications = ?,
                    image_url = ?,
                    stock_quantity = ?,
                    socket_type = ?,
                    ram_type = ?,
                    ram_speed = ?,
                    storage_interface = ?,
                    wattage = ?,
                    gpu_length = ?,
                    cooler_height = ?,
                    performance_tier = ?
                where product_id = ?
                """;

        jdbcTemplate.update(
                sql,
                product.getCategoryId(),
                product.getProductName(),
                product.getBrand(),
                product.getPrice(),
                product.getDescription(),
                product.getSpecifications(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.getSocketType(),
                product.getRamType(),
                product.getRamSpeed(),
                product.getStorageInterface(),
                product.getWattage(),
                product.getGpuLength(),
                product.getCoolerHeight(),
                product.getPerformanceTier(),
                product.getProductId()
        );

        Product updatedProduct =
                getProductById(product.getProductId());

        sendStockAlerts(
                updatedProduct,
                oldStock == null
                        ? product.getStockQuantity()
                        : oldStock,
                product.getStockQuantity());
    }

    // Reduce product stock
    public Product reduceStock(
            int productId,
            int quantity) {

        Product product =
                getProductById(productId);

        if (product == null) {
            throw new IllegalStateException(
                    "Product no longer exists");
        }

        int oldStock =
                product.getStockQuantity();

        if (quantity > oldStock) {
            throw new IllegalStateException(
                    "Not enough stock");
        }

        int newStock =
                oldStock - quantity;

        int updatedRows =
                jdbcTemplate.update(
                        """
                        update products
                        set stock_quantity = ?
                        where product_id = ?
                        and stock_quantity >= ?
                        """,
                        newStock,
                        productId,
                        quantity);

        if (updatedRows == 0) {
            throw new IllegalStateException(
                    "Not enough stock");
        }

        product.setStockQuantity(newStock);

        sendStockAlerts(
                product,
                oldStock,
                newStock);

        return product;
    }

    // Delete product
    public void deleteProduct(int productId) {

        String sql =
                "delete from products where product_id = ?";

        jdbcTemplate.update(
                sql,
                productId);
    }

    // Send stock alerts when crossing a stock threshold
    private void sendStockAlerts(
            Product product,
            int oldStock,
            int newStock) {

        if (product == null ||
                newStock >= oldStock) {
            return;
        }

        boolean reachedFive =
                oldStock > 5 &&
                        newStock <= 5;

        boolean reachedOne =
                oldStock > 1 &&
                        newStock <= 1;

        if (reachedFive) {
            emailService.sendLowStockEmail(
                    product,
                    newStock);
        }

        if (reachedOne) {
            emailService.sendLowStockEmail(
                    product,
                    newStock);
        }
    }

    // Convert a database row into a Product
    private Product mapProduct(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Product product = new Product();

        product.setProductId(
                rs.getInt("product_id")
        );

        product.setCategoryId(
                rs.getInt("category_id")
        );

        product.setProductName(
                rs.getString("product_name")
        );

        product.setBrand(
                rs.getString("brand")
        );

        product.setPrice(
                rs.getDouble("price")
        );

        product.setDescription(
                rs.getString("description")
        );

        product.setSpecifications(
                rs.getString("specifications")
        );

        product.setImageUrl(
                rs.getString("image_url")
        );

        product.setStockQuantity(
                rs.getInt("stock_quantity")
        );

        product.setCategoryName(
                rs.getString("category_name")
        );

        if (rs.getTimestamp("date_added") != null) {
            product.setDateAdded(
                    rs.getTimestamp("date_added").toString()
            );
        }

        product.setSocketType(
                rs.getString("socket_type")
        );

        product.setRamType(
                rs.getString("ram_type")
        );

        int ramSpeed =
                rs.getInt("ram_speed");

        product.setRamSpeed(
                rs.wasNull()
                        ? null
                        : ramSpeed
        );

        product.setStorageInterface(
                rs.getString("storage_interface")
        );

        int wattage =
                rs.getInt("wattage");

        product.setWattage(
                rs.wasNull()
                        ? null
                        : wattage
        );

        double gpuLength =
                rs.getDouble("gpu_length");

        product.setGpuLength(
                rs.wasNull()
                        ? null
                        : gpuLength
        );

        double coolerHeight =
                rs.getDouble("cooler_height");

        product.setCoolerHeight(
                rs.wasNull()
                        ? null
                        : coolerHeight
        );

        product.setPerformanceTier(
                rs.getString("performance_tier")
        );

        return product;
    }
}