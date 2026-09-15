package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get all categories
    public List<Category> getAllCategories() {

        String sql =
                "select category_id, category_name " +
                        "from categories " +
                        "order by category_id";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                )
        );
    }

    // Add new category
    public void addCategory(String categoryName) {

        jdbcTemplate.update(
                "insert into categories (category_name) values (?)",
                categoryName
        );
    }

    // Get one category
    public Category getCategoryById(int categoryId) {

        List<Category> categories = jdbcTemplate.query(
                "select category_id, category_name from categories where category_id = ?",
                (rs, rowNum) -> new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                ),
                categoryId
        );

        return categories.isEmpty()
                ? null
                : categories.get(0);
    }

    // Update category
    public void updateCategory(
            int categoryId,
            String categoryName) {

        jdbcTemplate.update(
                "update categories set category_name = ? where category_id = ?",
                categoryName,
                categoryId
        );
    }

    // Delete category
    public void deleteCategory(int categoryId) {

        jdbcTemplate.update(
                "delete from categories where category_id = ?",
                categoryId
        );
    }

    // Check if products use a category
    public boolean hasProducts(int categoryId) {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from products where category_id = ?",
                Integer.class,
                categoryId
        );

        return count != null && count > 0;
    }

    // Check duplicate category name
    public boolean categoryExists(
            String categoryName,
            int categoryId) {

        Integer count = jdbcTemplate.queryForObject(
                """
                select count(*)
                from categories
                where lower(category_name) = lower(?)
                and category_id <> ?
                """,
                Integer.class,
                categoryName,
                categoryId
        );

        return count != null && count > 0;
    }

    // Check duplicate category name when adding
    public boolean categoryExists(String categoryName) {

        Integer count = jdbcTemplate.queryForObject(
                """
                select count(*)
                from categories
                where lower(category_name) = lower(?)
                """,
                Integer.class,
                categoryName
        );

        return count != null && count > 0;
    }
}