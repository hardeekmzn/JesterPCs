package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.Cart;
import io.virinchi.jesterpcs.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartDAO {

    private final JdbcTemplate jdbcTemplate;
    private final ProductDAO productDAO;

    public CartDAO(
            JdbcTemplate jdbcTemplate,
            ProductDAO productDAO) {

        this.jdbcTemplate = jdbcTemplate;
        this.productDAO = productDAO;
    }

    // Get a user's cart, creating one if needed
    public Cart getOrCreateCart(int userId) {

        String findSql =
                "select cart_id, user_id from cart where user_id = ?";

        List<Cart> carts = jdbcTemplate.query(
                findSql,
                (rs, rowNum) -> new Cart(
                        rs.getInt("cart_id"),
                        rs.getInt("user_id")
                ),
                userId
        );

        if (!carts.isEmpty()) {
            return carts.get(0);
        }

        String insertSql =
                "insert into cart (user_id) values (?)";

        jdbcTemplate.update(insertSql, userId);

        Integer cartId = jdbcTemplate.queryForObject(
                "select cart_id from cart where user_id = ?",
                Integer.class,
                userId
        );

        return new Cart(cartId, userId);
    }

    // Add a product or increase its quantity
    public void addItem(int userId, int productId) {

        Cart cart = getOrCreateCart(userId);

        // Check current product stock
        io.virinchi.jesterpcs.model.Product product =
                productDAO.getProductById(productId);

        if (product == null || product.getStockQuantity() <= 0) {
            return;
        }

        String sql = """
                insert into cart_items (
                    cart_id,
                    product_id,
                    quantity
                )
                values (?, ?, 1)
                on duplicate key update
                    quantity = quantity + 1
                """;

        jdbcTemplate.update(
                sql,
                cart.getCartId(),
                productId
        );

        // Make sure cart quantity does not go above stock
        String limitSql = """
                update cart_items
                set quantity = ?
                where cart_id = ?
                and product_id = ?
                and quantity > ?
                """;

        jdbcTemplate.update(
                limitSql,
                product.getStockQuantity(),
                cart.getCartId(),
                productId,
                product.getStockQuantity()
        );
    }

    // Get all items in a user's cart
    public List<CartItem> getCartItems(int userId) {

        Cart cart = getOrCreateCart(userId);

        String sql = """
                select
                    cart_item_id,
                    cart_id,
                    product_id,
                    quantity
                from cart_items
                where cart_id = ?
                order by cart_item_id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    CartItem item = new CartItem();

                    item.setCartItemId(
                            rs.getInt("cart_item_id")
                    );

                    item.setCartId(
                            rs.getInt("cart_id")
                    );

                    item.setProductId(
                            rs.getInt("product_id")
                    );

                    item.setQuantity(
                            rs.getInt("quantity")
                    );

                    item.setProduct(
                            productDAO.getProductById(
                                    item.getProductId()
                            )
                    );

                    return item;
                },
                cart.getCartId()
        );
    }

    // Update item quantity
    public void updateQuantity(
            int userId,
            int productId,
            int quantity) {

        Cart cart = getOrCreateCart(userId);

        if (quantity <= 0) {
            removeItem(userId, productId);
            return;
        }

        // Do not allow quantity above current stock
        io.virinchi.jesterpcs.model.Product product =
                productDAO.getProductById(productId);

        if (product == null || product.getStockQuantity() <= 0) {
            removeItem(userId, productId);
            return;
        }

        quantity = Math.min(
                quantity,
                product.getStockQuantity()
        );

        String sql = """
                update cart_items
                set quantity = ?
                where cart_id = ?
                and product_id = ?
                """;

        jdbcTemplate.update(
                sql,
                quantity,
                cart.getCartId(),
                productId
        );
    }

    // Remove an item
    public void removeItem(
            int userId,
            int productId) {

        Cart cart = getOrCreateCart(userId);

        String sql = """
                delete from cart_items
                where cart_id = ?
                and product_id = ?
                """;

        jdbcTemplate.update(
                sql,
                cart.getCartId(),
                productId
        );
    }

    // Empty the cart
    public void clearCart(int userId) {

        Cart cart = getOrCreateCart(userId);

        String sql =
                "delete from cart_items where cart_id = ?";

        jdbcTemplate.update(
                sql,
                cart.getCartId()
        );
    }

    // Get number of items in cart
    public int getCartItemCount(int userId) {

        Cart cart = getOrCreateCart(userId);

        String sql = """
                select coalesce(sum(quantity), 0)
                from cart_items
                where cart_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                cart.getCartId()
        );

        return count != null ? count : 0;
    }
}