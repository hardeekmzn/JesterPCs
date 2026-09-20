USE jesterpcs;


-- Users

CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       full_name VARCHAR(100) NOT NULL,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email_verified BOOLEAN DEFAULT FALSE,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       phone VARCHAR(20),
                       address VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_ID_CACHE 1;


-- Email verification

CREATE TABLE email_verification (
                                    verification_id INT PRIMARY KEY AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    token VARCHAR(255) NOT NULL UNIQUE,

                                    FOREIGN KEY (user_id)
                                        REFERENCES users(user_id)
                                        ON DELETE CASCADE
);


-- Login verification

CREATE TABLE login_verification (
                                    verification_id INT PRIMARY KEY AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    token VARCHAR(255) NOT NULL UNIQUE,

                                    FOREIGN KEY (user_id)
                                        REFERENCES users(user_id)
                                        ON DELETE CASCADE
);


-- Categories

CREATE TABLE categories (
                            category_id INT PRIMARY KEY AUTO_INCREMENT,
                            category_name VARCHAR(50) NOT NULL UNIQUE
);


-- Products

CREATE TABLE products (
                          product_id INT PRIMARY KEY AUTO_INCREMENT,
                          category_id INT NOT NULL,
                          product_name VARCHAR(150) NOT NULL,
                          brand VARCHAR(50) NOT NULL,
                          price DECIMAL(12, 2) NOT NULL,
                          description TEXT,
                          specifications TEXT,
                          image_url VARCHAR(500),
                          stock_quantity INT DEFAULT 0,
                          date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          socket_type VARCHAR(50),
                          ram_type VARCHAR(20),
                          ram_speed INT,
                          storage_interface VARCHAR(20),
                          wattage INT,
                          gpu_length DECIMAL(6, 2),
                          cooler_height DECIMAL(6, 2),
                          performance_tier VARCHAR(20),

                          FOREIGN KEY (category_id)
                              REFERENCES categories(category_id)
);


-- Cart

CREATE TABLE cart (
                      cart_id INT PRIMARY KEY AUTO_INCREMENT,
                      user_id INT NOT NULL UNIQUE,

                      FOREIGN KEY (user_id)
                          REFERENCES users(user_id)
                          ON DELETE CASCADE
);


-- Cart items

CREATE TABLE cart_items (
                            cart_item_id INT PRIMARY KEY AUTO_INCREMENT,
                            cart_id INT NOT NULL,
                            product_id INT NOT NULL,
                            quantity INT NOT NULL DEFAULT 1,

                            FOREIGN KEY (cart_id)
                                REFERENCES cart(cart_id)
                                ON DELETE CASCADE,

                            FOREIGN KEY (product_id)
                                REFERENCES products(product_id)
                                ON DELETE CASCADE,

                            UNIQUE (cart_id, product_id)
);


-- Orders

CREATE TABLE orders (
                        order_id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT NOT NULL,
                        total_amount DECIMAL(12, 2) NOT NULL,
                        delivery_address VARCHAR(255) NOT NULL,
                        phone VARCHAR(20) NOT NULL,
                        status VARCHAR(30) NOT NULL DEFAULT 'Pending',
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        FOREIGN KEY (user_id)
                            REFERENCES users(user_id)
                            ON DELETE CASCADE
);


-- Order items

CREATE TABLE order_items (
                             order_item_id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             price DECIMAL(12, 2) NOT NULL,

                             FOREIGN KEY (order_id)
                                 REFERENCES orders(order_id)
                                 ON DELETE CASCADE,

                             FOREIGN KEY (product_id)
                                 REFERENCES products(product_id)
                                 ON DELETE CASCADE
);


-- password reset tokens

CREATE TABLE password_reset (
                                reset_id INT PRIMARY KEY AUTO_INCREMENT,
                                user_id INT NOT NULL,
                                token VARCHAR(255) NOT NULL UNIQUE,
                                expires_at TIMESTAMP NOT NULL,

                                FOREIGN KEY (user_id)
                                    REFERENCES users(user_id)
                                    ON DELETE CASCADE
);


-- Create table for pre-built PC information

CREATE TABLE prebuilt_pcs (
                              prebuilt_id INT PRIMARY KEY AUTO_INCREMENT,
                              build_key VARCHAR(50) NOT NULL UNIQUE,
                              name VARCHAR(100) NOT NULL,
                              title VARCHAR(150) NOT NULL,
                              description TEXT,
                              target_resolution VARCHAR(50),
                              performance VARCHAR(50),
                              best_for VARCHAR(100),
                              image_url VARCHAR(500),
                              date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Connect pre-built PCs with existing marketplace products

CREATE TABLE prebuilt_products (
                                   prebuilt_id INT NOT NULL,
                                   product_id INT NOT NULL,
                                   quantity INT NOT NULL DEFAULT 1,

                                   PRIMARY KEY (prebuilt_id, product_id),

                                   FOREIGN KEY (prebuilt_id)
                                       REFERENCES prebuilt_pcs(prebuilt_id)
                                       ON DELETE CASCADE,

                                   FOREIGN KEY (product_id)
                                       REFERENCES products(product_id)
                                       ON DELETE CASCADE
);


-- Store product reviews

CREATE TABLE reviews (
                         review_id INT PRIMARY KEY AUTO_INCREMENT,
                         product_id INT NOT NULL,
                         user_id INT NOT NULL,
                         rating INT NOT NULL,
                         review_text TEXT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         FOREIGN KEY (product_id)
                             REFERENCES products(product_id)
                             ON DELETE CASCADE,

                         FOREIGN KEY (user_id)
                             REFERENCES users(user_id)
                             ON DELETE CASCADE,

                         UNIQUE (product_id, user_id)
);


-- Store reviews comments

CREATE TABLE review_comments (
                                 comment_id INT PRIMARY KEY AUTO_INCREMENT,
                                 review_id INT NOT NULL,
                                 user_id INT NOT NULL,
                                 comment_text TEXT NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                 FOREIGN KEY (review_id)
                                     REFERENCES reviews(review_id)
                                     ON DELETE CASCADE,

                                 FOREIGN KEY (user_id)
                                     REFERENCES users(user_id)
                                     ON DELETE CASCADE
);


-- Store review upvotes

CREATE TABLE review_upvotes (
                                review_id INT NOT NULL,
                                user_id INT NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                PRIMARY KEY (review_id, user_id),

                                FOREIGN KEY (review_id)
                                    REFERENCES reviews(review_id)
                                    ON DELETE CASCADE,

                                FOREIGN KEY (user_id)
                                    REFERENCES users(user_id)
                                    ON DELETE CASCADE
);


-- Wishlist

CREATE TABLE wishlist (
                          wishlist_id INT PRIMARY KEY AUTO_INCREMENT,
                          user_id INT NOT NULL,
                          product_id INT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          UNIQUE KEY unique_user_product (user_id, product_id),

                          FOREIGN KEY (user_id)
                              REFERENCES users(user_id)
                              ON DELETE CASCADE,

                          FOREIGN KEY (product_id)
                              REFERENCES products(product_id)
                              ON DELETE CASCADE
);