package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.CartItem;
import io.virinchi.jesterpcs.model.Order;
import io.virinchi.jesterpcs.model.Product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderDAO(
            JdbcTemplate jdbcTemplate,
            CartDAO cartDAO,
            ProductDAO productDAO) {

        this.jdbcTemplate = jdbcTemplate;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /* Create order */

    @Transactional
    public int createOrder(
            int userId,
            String address,
            String phone,
            double total) {

        List<CartItem> items =
                cartDAO.getCartItems(userId);

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "Cart is empty");
        }

        for (CartItem item : items) {

            if (item.getProduct() == null) {
                throw new IllegalStateException(
                        "Product no longer exists");
            }

            // Check stock before creating the order
            if (item.getQuantity()
                    > item.getProduct().getStockQuantity()) {

                throw new IllegalStateException(
                        "Not enough stock");
            }
        }

        // Create order and get the generated ID directly
        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            """
                            INSERT INTO orders
                            (user_id, total_amount, delivery_address, phone, status)
                            VALUES (?, ?, ?, ?, 'Pending')
                            """,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setInt(1, userId);
            ps.setDouble(2, total);
            ps.setString(3, address);
            ps.setString(4, phone);

            return ps;

        }, keyHolder);

        int orderId =
                keyHolder.getKey().intValue();

        for (CartItem item : items) {

            // Reduce stock and send low-stock alerts
            Product product =
                    productDAO.reduceStock(
                            item.getProductId(),
                            item.getQuantity());

            jdbcTemplate.update("""
                    INSERT INTO order_items
                    (order_id, product_id, quantity, price)
                    VALUES (?, ?, ?, ?)
                    """,
                    orderId,
                    item.getProductId(),
                    item.getQuantity(),
                    product.getPrice());
        }

        // Clear cart only after order items and stock succeed
        cartDAO.clearCart(userId);

        return orderId;
    }

    /* Map database row to Order */

    private Order mapOrder(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Order order = new Order();

        order.setOrderId(
                rs.getInt("order_id"));

        order.setUserId(
                rs.getInt("user_id"));

        order.setTotalAmount(
                rs.getDouble("total_amount"));

        order.setDeliveryAddress(
                rs.getString("delivery_address"));

        order.setPhone(
                rs.getString("phone"));

        order.setStatus(
                rs.getString("status"));

        if (rs.getTimestamp("order_date") != null) {
            order.setOrderDate(
                    rs.getTimestamp("order_date")
                            .toLocalDateTime());
        }

        return order;
    }

    /* Get one order */

    public Order getOrderById(int orderId) {

        List<Order> orders =
                jdbcTemplate.query(
                        """
                        SELECT order_id,
                               user_id,
                               total_amount,
                               delivery_address,
                               phone,
                               status,
                               order_date
                        FROM orders
                        WHERE order_id = ?
                        """,
                        (rs, rowNum) ->
                                mapOrder(rs),
                        orderId);

        return orders.isEmpty()
                ? null
                : orders.get(0);
    }

    /* Get all orders */

    public List<Order> getAllOrders() {

        return jdbcTemplate.query(
                """
                SELECT order_id,
                       user_id,
                       total_amount,
                       delivery_address,
                       phone,
                       status,
                       order_date
                FROM orders
                ORDER BY order_id DESC
                """,
                (rs, rowNum) ->
                        mapOrder(rs));
    }

    /* Get orders for one user */

    public List<Order> getOrdersByUserId(
            int userId) {

        return jdbcTemplate.query(
                """
                SELECT order_id,
                       user_id,
                       total_amount,
                       delivery_address,
                       phone,
                       status,
                       order_date
                FROM orders
                WHERE user_id = ?
                ORDER BY order_id DESC
                """,
                (rs, rowNum) ->
                        mapOrder(rs),
                userId);
    }

    /* Update order status */

    @Transactional
    public void updateStatus(
            int orderId,
            String newStatus) {

        Order order =
                getOrderById(orderId);

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order not found");
        }

        String currentStatus =
                order.getStatus();

        String status =
                newStatus.trim();

        boolean valid =
                switch (currentStatus) {

                    case "Pending" ->
                            status.equals("Pending") ||
                                    status.equals("Processing") ||
                                    status.equals("Cancelled");

                    case "Processing" ->
                            status.equals("Processing") ||
                                    status.equals("Shipped") ||
                                    status.equals("Cancelled");

                    case "Shipped" ->
                            status.equals("Shipped") ||
                                    status.equals("Delivered");

                    case "Delivered" ->
                            status.equals("Delivered");

                    case "Cancelled" ->
                            status.equals("Cancelled");

                    default ->
                            false;
                };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid order status change");
        }

        if (status.equals("Cancelled") &&
                !currentStatus.equals("Cancelled")) {

            restoreOrderStock(orderId);
        }

        jdbcTemplate.update(
                """
                UPDATE orders
                SET status = ?
                WHERE order_id = ?
                """,
                status,
                orderId);
    }

    /* Cancel order for its owner */

    @Transactional
    public void cancelOrder(
            int orderId,
            int userId) {

        Order order =
                getOrderById(orderId);

        if (order == null ||
                order.getUserId() != userId) {

            throw new IllegalArgumentException(
                    "Order not found");
        }

        if (!order.getStatus().equals("Pending") &&
                !order.getStatus().equals("Processing")) {

            throw new IllegalStateException(
                    "This order can no longer be cancelled");
        }

        int updatedRows =
                jdbcTemplate.update(
                        """
                        UPDATE orders
                        SET status = 'Cancelled'
                        WHERE order_id = ?
                          AND user_id = ?
                          AND status IN ('Pending', 'Processing')
                        """,
                        orderId,
                        userId);

        if (updatedRows == 0) {
            throw new IllegalStateException(
                    "This order can no longer be cancelled");
        }

        restoreOrderStock(orderId);
    }

    /* Return cancelled order items to stock */

    private void restoreOrderStock(
            int orderId) {

        List<Map<String, Object>> items =
                jdbcTemplate.query(
                        """
                        SELECT product_id,
                               quantity
                        FROM order_items
                        WHERE order_id = ?
                        """,
                        (rs, rowNum) -> {

                            Map<String, Object> item =
                                    new HashMap<>();

                            item.put(
                                    "productId",
                                    rs.getInt("product_id"));

                            item.put(
                                    "quantity",
                                    rs.getInt("quantity"));

                            return item;
                        },
                        orderId);

        for (Map<String, Object> item : items) {

            int productId =
                    (Integer) item.get("productId");

            int quantity =
                    (Integer) item.get("quantity");

            int updatedRows =
                    jdbcTemplate.update(
                            """
                            UPDATE products
                            SET stock_quantity =
                                stock_quantity + ?
                            WHERE product_id = ?
                            """,
                            quantity,
                            productId);

            if (updatedRows == 0) {
                throw new IllegalStateException(
                        "Product no longer exists");
            }
        }
    }

    public List<Map<String, Object>> getOrderItems(
            int orderId) {

        return jdbcTemplate.query(
                """
                SELECT oi.product_id,
                       p.product_name,
                       oi.quantity,
                       oi.price
                FROM order_items oi
                JOIN products p
                  ON oi.product_id = p.product_id
                WHERE oi.order_id = ?
                """,
                (rs, rowNum) -> {

                    Map<String, Object> item =
                            new HashMap<>();

                    item.put(
                            "productId",
                            rs.getInt("product_id"));

                    item.put(
                            "productName",
                            rs.getString(
                                    "product_name"));

                    item.put(
                            "quantity",
                            rs.getInt("quantity"));

                    item.put(
                            "price",
                            rs.getDouble("price"));

                    return item;
                },
                orderId);
    }
}