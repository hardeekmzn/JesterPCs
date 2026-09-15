package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.PreBuilts;
import io.virinchi.jesterpcs.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PrebuiltDAO {

    private final JdbcTemplate jdbcTemplate;
    private final ProductDAO productDAO;

    public PrebuiltDAO(
            JdbcTemplate jdbcTemplate,
            ProductDAO productDAO) {

        this.jdbcTemplate = jdbcTemplate;
        this.productDAO = productDAO;
    }

    // Get all pre-built PCs
    public List<PreBuilts> getAllPrebuilts() {

        String sql = """
                select
                    prebuilt_id,
                    build_key,
                    name,
                    title,
                    description,
                    target_resolution,
                    performance,
                    best_for,
                    image_url
                from prebuilt_pcs
                order by prebuilt_id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    PreBuilts prebuilt = new PreBuilts();

                    prebuilt.setPrebuiltId(
                            rs.getInt("prebuilt_id")
                    );

                    prebuilt.setKey(
                            rs.getString("build_key")
                    );

                    prebuilt.setName(
                            rs.getString("name")
                    );

                    prebuilt.setTitle(
                            rs.getString("title")
                    );

                    prebuilt.setDescription(
                            rs.getString("description")
                    );

                    prebuilt.setTargetResolution(
                            rs.getString("target_resolution")
                    );

                    prebuilt.setPerformance(
                            rs.getString("performance")
                    );

                    prebuilt.setBestFor(
                            rs.getString("best_for")
                    );

                    prebuilt.setImageUrl(
                            rs.getString("image_url")
                    );

                    prebuilt.setProducts(
                            getProductsByPrebuiltId(
                                    rs.getInt("prebuilt_id")
                            )
                    );

                    return prebuilt;
                }
        );
    }

    // Get one pre-built PC
    public PreBuilts getPrebuiltById(int prebuiltId) {

        List<PreBuilts> prebuilts = jdbcTemplate.query(
                """
                select
                    prebuilt_id,
                    build_key,
                    name,
                    title,
                    description,
                    target_resolution,
                    performance,
                    best_for,
                    image_url
                from prebuilt_pcs
                where prebuilt_id = ?
                """,
                (rs, rowNum) -> {

                    PreBuilts prebuilt = new PreBuilts();

                    prebuilt.setPrebuiltId(
                            rs.getInt("prebuilt_id")
                    );

                    prebuilt.setKey(
                            rs.getString("build_key")
                    );

                    prebuilt.setName(
                            rs.getString("name")
                    );

                    prebuilt.setTitle(
                            rs.getString("title")
                    );

                    prebuilt.setDescription(
                            rs.getString("description")
                    );

                    prebuilt.setTargetResolution(
                            rs.getString("target_resolution")
                    );

                    prebuilt.setPerformance(
                            rs.getString("performance")
                    );

                    prebuilt.setBestFor(
                            rs.getString("best_for")
                    );

                    prebuilt.setImageUrl(
                            rs.getString("image_url")
                    );

                    prebuilt.setProducts(
                            getProductsByPrebuiltId(
                                    rs.getInt("prebuilt_id")
                            )
                    );

                    return prebuilt;
                },
                prebuiltId
        );

        return prebuilts.isEmpty()
                ? null
                : prebuilts.get(0);
    }

    // Get products inside a pre-built PC
    public List<Product> getProductsByPrebuiltId(int prebuiltId) {

        String sql = """
                select product_id
                from prebuilt_products
                where prebuilt_id = ?
                order by product_id
                """;

        List<Integer> productIds = jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getInt("product_id"),
                prebuiltId
        );

        List<Product> products = new ArrayList<>();

        for (Integer productId : productIds) {

            Product product =
                    productDAO.getProductById(productId);

            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    // Add a new pre-built PC
    public int addPrebuilt(PreBuilts prebuilt) {

        jdbcTemplate.update(
                """
                insert into prebuilt_pcs (
                    build_key,
                    name,
                    title,
                    description,
                    target_resolution,
                    performance,
                    best_for,
                    image_url
                )
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                prebuilt.getKey(),
                prebuilt.getName(),
                prebuilt.getTitle(),
                prebuilt.getDescription(),
                prebuilt.getTargetResolution(),
                prebuilt.getPerformance(),
                prebuilt.getBestFor(),
                prebuilt.getImageUrl()
        );

        Integer prebuiltId =
                jdbcTemplate.queryForObject(
                        """
                        select prebuilt_id
                        from prebuilt_pcs
                        where build_key = ?
                        """,
                        Integer.class,
                        prebuilt.getKey()
                );

        return prebuiltId;
    }

    // Add a product to a pre-built PC
    public void addProductToPrebuilt(
            int prebuiltId,
            int productId) {

        jdbcTemplate.update(
                """
                insert into prebuilt_products
                (prebuilt_id, product_id)
                values (?, ?)
                """,
                prebuiltId,
                productId
        );
    }

    // Update pre-built PC information
    public void updatePrebuilt(PreBuilts prebuilt) {

        jdbcTemplate.update(
                """
                update prebuilt_pcs
                set
                    build_key = ?,
                    name = ?,
                    title = ?,
                    description = ?,
                    target_resolution = ?,
                    performance = ?,
                    best_for = ?,
                    image_url = ?
                where prebuilt_id = ?
                """,
                prebuilt.getKey(),
                prebuilt.getName(),
                prebuilt.getTitle(),
                prebuilt.getDescription(),
                prebuilt.getTargetResolution(),
                prebuilt.getPerformance(),
                prebuilt.getBestFor(),
                prebuilt.getImageUrl(),
                prebuilt.getPrebuiltId()
        );
    }

    // Remove all products from a pre-built PC
    public void clearPrebuiltProducts(int prebuiltId) {

        jdbcTemplate.update(
                "delete from prebuilt_products where prebuilt_id = ?",
                prebuiltId
        );
    }

    // Replace products inside a pre-built PC
    public void updatePrebuiltProducts(
            int prebuiltId,
            List<Integer> productIds) {

        clearPrebuiltProducts(prebuiltId);

        if (productIds == null) {
            return;
        }

        for (Integer productId : productIds) {

            addProductToPrebuilt(
                    prebuiltId,
                    productId
            );
        }
    }

    // Delete a pre-built PC
    public void deletePrebuilt(int prebuiltId) {

        jdbcTemplate.update(
                "delete from prebuilt_pcs where prebuilt_id = ?",
                prebuiltId
        );
    }
}