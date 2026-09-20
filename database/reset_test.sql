USE jesterpcs;


-- Reset test data while keeping the admin account

DELETE FROM review_upvotes;


DELETE FROM review_comments;


DELETE FROM reviews;


DELETE FROM wishlist;


DELETE FROM password_reset;


DELETE FROM login_verification;


DELETE FROM email_verification;


DELETE FROM cart_items;


DELETE FROM cart;


DELETE FROM order_items;


DELETE FROM orders;


DELETE FROM users
WHERE role <> 'ADMIN';