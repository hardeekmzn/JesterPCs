USE jesterpcs;

-- Database overview

SHOW TABLES;


-- Describe

DESCRIBE users;

DESCRIBE email_verification;

DESCRIBE login_verification;

DESCRIBE categories;

DESCRIBE products;

DESCRIBE cart;

DESCRIBE cart_items;

DESCRIBE orders;

DESCRIBE order_items;

DESCRIBE password_reset;

DESCRIBE prebuilt_pcs;

DESCRIBE prebuilt_products;

DESCRIBE reviews;

DESCRIBE review_comments;

DESCRIBE review_upvotes;

DESCRIBE wishlist;


-- Selects

SELECT *
FROM users;


SELECT
    user_id,
    username,
    email,
    email_verified,
    role
FROM users;


SELECT *
FROM email_verification;


SELECT *
FROM login_verification;


SELECT *
FROM products;


SELECT
    product_id,
    product_name,
    category_id,
    brand,
    price,
    stock_quantity,
    performance_tier
FROM products
ORDER BY product_id DESC;


SELECT *
FROM categories;


SELECT *
FROM cart;


SELECT *
FROM cart_items;


SELECT *
FROM orders
ORDER BY order_id DESC;


SELECT
    order_id,
    user_id,
    total_amount,
    status
FROM orders
ORDER BY order_id DESC;


SELECT *
FROM order_items;


SELECT *
FROM password_reset;


SELECT *
FROM prebuilt_pcs;


SELECT *
FROM prebuilt_products;


SELECT *
FROM reviews;


SELECT *
FROM review_comments;


SELECT *
FROM review_upvotes;


SELECT *
FROM wishlist;


-- Check product count

SELECT COUNT(*) AS total_products
FROM products;


-- Check categories

SELECT *
FROM categories
ORDER BY category_id;


-- Check products

SELECT
    product_id,
    category_id,
    product_name,
    brand,
    price,
    performance_tier,
    stock_quantity
FROM products
ORDER BY product_id;


-- Check stock

SELECT
    product_id,
    product_name,
    price,
    stock_quantity
FROM products
WHERE product_id = YOUR_PRODUCT_ID;


-- Check new pre-built tables

SHOW TABLES;


-- Check pre-built PC table

DESCRIBE prebuilt_pcs;


-- Check pre-built product table

DESCRIBE prebuilt_products;