package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.Review;
import io.virinchi.jesterpcs.model.ReviewComment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ReviewDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get all reviews for one product
    public List<Review> getReviewsByProductId(
            int productId,
            int userId) {

        String sql = """
                select
                    r.review_id,
                    r.product_id,
                    r.user_id,
                    r.rating,
                    r.review_text,
                    r.created_at,
                    u.username,

                    (
                        select count(*)
                        from review_upvotes ru
                        where ru.review_id = r.review_id
                    ) as upvote_count,

                    case
                        when exists (
                            select 1
                            from review_upvotes ru2
                            where ru2.review_id = r.review_id
                            and ru2.user_id = ?
                        )
                        then true
                        else false
                    end as user_upvoted

                from reviews r
                join users u
                    on r.user_id = u.user_id

                where r.product_id = ?

                order by r.created_at desc
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Review review =
                            new Review();

                    review.setReviewId(
                            rs.getInt("review_id"));

                    review.setProductId(
                            rs.getInt("product_id"));

                    review.setUserId(
                            rs.getInt("user_id"));

                    review.setRating(
                            rs.getInt("rating"));

                    review.setReviewText(
                            rs.getString("review_text"));

                    Timestamp createdAt =
                            rs.getTimestamp("created_at");

                    if (createdAt != null) {
                        review.setCreatedAt(
                                createdAt.toLocalDateTime());
                    }

                    review.setUsername(
                            rs.getString("username"));

                    review.setUpvoteCount(
                            rs.getInt("upvote_count"));

                    review.setUserUpvoted(
                            rs.getBoolean("user_upvoted"));

                    return review;
                },
                userId,
                productId
        );
    }

    // Get all reviews written by one user
    public List<Review> getReviewsByUserId(
            int userId) {

        String sql = """
                select
                    r.review_id,
                    r.product_id,
                    r.user_id,
                    r.rating,
                    r.review_text,
                    r.created_at,
                    u.username,

                    (
                        select count(*)
                        from review_upvotes ru
                        where ru.review_id = r.review_id
                    ) as upvote_count,

                    (
                        select count(*)
                        from review_comments rc
                        where rc.review_id = r.review_id
                    ) as comment_count

                from reviews r
                join users u
                    on r.user_id = u.user_id

                where r.user_id = ?

                order by r.created_at desc
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Review review =
                            new Review();

                    review.setReviewId(
                            rs.getInt("review_id"));

                    review.setProductId(
                            rs.getInt("product_id"));

                    review.setUserId(
                            rs.getInt("user_id"));

                    review.setRating(
                            rs.getInt("rating"));

                    review.setReviewText(
                            rs.getString("review_text"));

                    Timestamp createdAt =
                            rs.getTimestamp("created_at");

                    if (createdAt != null) {
                        review.setCreatedAt(
                                createdAt.toLocalDateTime());
                    }

                    review.setUsername(
                            rs.getString("username"));

                    review.setUpvoteCount(
                            rs.getInt("upvote_count"));

                    return review;
                },
                userId
        );
    }

    // Get comments for one review
    public List<ReviewComment> getCommentsByReviewId(
            int reviewId) {

        String sql = """
                select
                    rc.comment_id,
                    rc.review_id,
                    rc.user_id,
                    rc.comment_text,
                    rc.created_at,
                    u.username

                from review_comments rc
                join users u
                    on rc.user_id = u.user_id

                where rc.review_id = ?

                order by rc.created_at asc
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    ReviewComment comment =
                            new ReviewComment();

                    comment.setCommentId(
                            rs.getInt("comment_id"));

                    comment.setReviewId(
                            rs.getInt("review_id"));

                    comment.setUserId(
                            rs.getInt("user_id"));

                    comment.setCommentText(
                            rs.getString("comment_text"));

                    Timestamp createdAt =
                            rs.getTimestamp("created_at");

                    if (createdAt != null) {
                        comment.setCreatedAt(
                                createdAt.toLocalDateTime());
                    }

                    comment.setUsername(
                            rs.getString("username"));

                    return comment;
                },
                reviewId
        );
    }

    // Check if user purchased and received the product
    public boolean hasPurchasedProduct(
            int userId,
            int productId) {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        select count(*)
                        from orders o
                        join order_items oi
                            on o.order_id = oi.order_id
                        where o.user_id = ?
                        and oi.product_id = ?
                        and o.status = 'Delivered'
                        """,
                        Integer.class,
                        userId,
                        productId
                );

        return count != null && count > 0;
    }

    // Check if user already reviewed the product
    public boolean hasReviewedProduct(
            int userId,
            int productId) {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        select count(*)
                        from reviews
                        where user_id = ?
                        and product_id = ?
                        """,
                        Integer.class,
                        userId,
                        productId
                );

        return count != null && count > 0;
    }

    // Add review
    public void addReview(
            int productId,
            int userId,
            int rating,
            String reviewText) {

        jdbcTemplate.update(
                """
                insert into reviews (
                    product_id,
                    user_id,
                    rating,
                    review_text
                )
                values (?, ?, ?, ?)
                """,
                productId,
                userId,
                rating,
                reviewText
        );
    }

    // Update user's own review
    public int updateReview(
            int reviewId,
            int userId,
            int rating,
            String reviewText) {

        return jdbcTemplate.update(
                """
                update reviews
                set rating = ?,
                    review_text = ?
                where review_id = ?
                and user_id = ?
                """,
                rating,
                reviewText,
                reviewId,
                userId
        );
    }

    // Delete user's own review
    public int deleteReview(
            int reviewId,
            int userId) {

        return jdbcTemplate.update(
                """
                delete from reviews
                where review_id = ?
                and user_id = ?
                """,
                reviewId,
                userId
        );
    }

    // Get average rating
    public double getAverageRating(
            int productId) {

        Number average =
                jdbcTemplate.queryForObject(
                        """
                        select coalesce(
                            avg(rating),
                            0
                        )
                        from reviews
                        where product_id = ?
                        """,
                        Number.class,
                        productId
                );

        return average == null
                ? 0
                : average.doubleValue();
    }

    // Get rating breakdown
    public int[] getRatingBreakdown(
            int productId) {

        int[] breakdown =
                new int[6];

        jdbcTemplate.query(
                """
                select
                    rating,
                    count(*) as total
                from reviews
                where product_id = ?
                group by rating
                """,
                rs -> {

                    int rating =
                            rs.getInt("rating");

                    int total =
                            rs.getInt("total");

                    if (rating >= 1 &&
                            rating <= 5) {

                        breakdown[rating] =
                                total;
                    }
                },
                productId
        );

        return breakdown;
    }

    // Add comment
    public void addComment(
            int reviewId,
            int userId,
            String commentText) {

        jdbcTemplate.update(
                """
                insert into review_comments (
                    review_id,
                    user_id,
                    comment_text
                )
                values (?, ?, ?)
                """,
                reviewId,
                userId,
                commentText
        );
    }

    // Check if review exists
    public boolean reviewExists(
            int reviewId) {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        select count(*)
                        from reviews
                        where review_id = ?
                        """,
                        Integer.class,
                        reviewId
                );

        return count != null && count > 0;
    }

    // Check if user already upvoted a review
    public boolean hasUpvoted(
            int reviewId,
            int userId) {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        select count(*)
                        from review_upvotes
                        where review_id = ?
                        and user_id = ?
                        """,
                        Integer.class,
                        reviewId,
                        userId
                );

        return count != null && count > 0;
    }

    // Add upvote
    public void addUpvote(
            int reviewId,
            int userId) {

        jdbcTemplate.update(
                """
                insert into review_upvotes (
                    review_id,
                    user_id
                )
                values (?, ?)
                """,
                reviewId,
                userId
        );
    }

    // Remove upvote
    public void removeUpvote(
            int reviewId,
            int userId) {

        jdbcTemplate.update(
                """
                delete from review_upvotes
                where review_id = ?
                and user_id = ?
                """,
                reviewId,
                userId
        );
    }

    // Check if comment exists
    public boolean commentExists(
            int commentId) {

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        select count(*)
                        from review_comments
                        where comment_id = ?
                        """,
                        Integer.class,
                        commentId
                );

        return count != null && count > 0;
    }
}