package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.Product;
import io.virinchi.jesterpcs.model.Wishlist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistDAO {

    private final JdbcTemplate jdbcTemplate;
    private final ProductDAO productDAO;

    public WishlistDAO(
            JdbcTemplate jdbcTemplate,
            ProductDAO productDAO) {

        this.jdbcTemplate = jdbcTemplate;
        this.productDAO = productDAO;
    }

    // Add a product to wishlist
    public void addItem(
            int userId,
            int productId) {

        String sql = """
                insert ignore into wishlist (
                    user_id,
                    product_id
                )
                values (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                userId,
                productId
        );
    }

    // Remove a product from wishlist
    public void removeItem(
            int userId,
            int productId) {

        String sql = """
                delete from wishlist
                where user_id = ?
                and product_id = ?
                """;

        jdbcTemplate.update(
                sql,
                userId,
                productId
        );
    }

    // Check if a product is already in wishlist
    public boolean isWishlisted(
            int userId,
            int productId) {

        String sql = """
                select count(*)
                from wishlist
                where user_id = ?
                and product_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId,
                productId
        );

        return count != null && count > 0;
    }

    // Get all wishlist items for a user
    public List<Wishlist> getWishlist(
            int userId) {

        String sql = """
                select
                    wishlist_id,
                    user_id,
                    product_id
                from wishlist
                where user_id = ?
                order by created_at desc
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Wishlist wishlist =
                            new Wishlist();

                    wishlist.setWishlistId(
                            rs.getInt("wishlist_id")
                    );

                    wishlist.setUserId(
                            rs.getInt("user_id")
                    );

                    wishlist.setProductId(
                            rs.getInt("product_id")
                    );

                    Product product =
                            productDAO.getProductById(
                                    wishlist.getProductId()
                            );

                    wishlist.setProduct(product);

                    return wishlist;
                },
                userId
        );
    }

    // Get number of wishlist items
    public int getWishlistCount(
            int userId) {

        String sql = """
                select count(*)
                from wishlist
                where user_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId
        );

        return count != null ? count : 0;
    }
}