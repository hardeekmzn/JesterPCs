package io.virinchi.jesterpcs.dao;

import io.virinchi.jesterpcs.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Add user
    public int addUser(User user) {

        jdbcTemplate.update(
                "insert into users (full_name, username, email, password) values (?, ?, ?, ?)",
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );

        return jdbcTemplate.queryForObject(
                "select user_id from users where username = ?",
                Integer.class,
                user.getUsername()
        );
    }

    // Map database row to User
    private User mapUser(java.sql.ResultSet rs)
            throws java.sql.SQLException {

        User user = new User();

        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setEmailVerified(rs.getBoolean("email_verified"));
        user.setRole(rs.getString("role"));

        Timestamp createdAt = rs.getTimestamp("created_at");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }

    // Get user by ID
    public User getUserById(int userId) {

        List<User> users = jdbcTemplate.query(
                "select * from users where user_id = ?",
                (rs, rowNum) -> mapUser(rs),
                userId
        );

        return users.isEmpty() ? null : users.get(0);
    }

    // Get user by username
    public User getUserByUsername(String username) {

        List<User> users = jdbcTemplate.query(
                "select * from users where username = ?",
                (rs, rowNum) -> mapUser(rs),
                username
        );

        return users.isEmpty() ? null : users.get(0);
    }

    // Get user by email
    public User getUserByEmail(String email) {

        List<User> users = jdbcTemplate.query(
                "select * from users where email = ?",
                (rs, rowNum) -> mapUser(rs),
                email
        );

        return users.isEmpty() ? null : users.get(0);
    }

    // Update profile information
    public void updateProfile(
            int userId,
            String fullName,
            String username,
            String email,
            String phone,
            String address) {

        jdbcTemplate.update(
                "update users " +
                        "set full_name = ?, username = ?, email = ?, phone = ?, address = ? " +
                        "where user_id = ?",
                fullName,
                username,
                email,
                phone,
                address,
                userId
        );
    }

    // Update password
    public void updatePassword(int userId, String password) {

        jdbcTemplate.update(
                "update users set password = ? where user_id = ?",
                password,
                userId
        );
    }

    // Delete user account
    public void deleteUser(int userId) {

        jdbcTemplate.update(
                "delete from users where user_id = ?",
                userId
        );
    }

    // Delete a normal user from admin panel
    public void deleteUserByAdmin(
            int userId,
            int adminUserId) {

        jdbcTemplate.update(
                """
                delete from users
                where user_id = ?
                and user_id <> ?
                and role <> 'ADMIN'
                """,
                userId,
                adminUserId
        );
    }

    // Check username
    public boolean usernameExists(String username) {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from users where username = ?",
                Integer.class,
                username
        );

        return count != null && count > 0;
    }

    // Check username for another user
    public boolean usernameExistsForOtherUser(
            String username,
            int userId) {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from users " +
                        "where username = ? and user_id <> ?",
                Integer.class,
                username,
                userId
        );

        return count != null && count > 0;
    }

    // Check email
    public boolean emailExists(String email) {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from users where email = ?",
                Integer.class,
                email
        );

        return count != null && count > 0;
    }

    // Check email for another user
    public boolean emailExistsForOtherUser(
            String email,
            int userId) {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from users " +
                        "where email = ? and user_id <> ?",
                Integer.class,
                email,
                userId
        );

        return count != null && count > 0;
    }

    // Get all users
    public List<User> getAllUsers() {

        String sql =
                "select user_id, full_name, username, email, " +
                        "phone, address, email_verified, role, created_at " +
                        "from users order by user_id desc";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    User user = new User();

                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setEmailVerified(
                            rs.getBoolean("email_verified"));
                    user.setRole(rs.getString("role"));

                    Timestamp createdAt =
                            rs.getTimestamp("created_at");

                    if (createdAt != null) {
                        user.setCreatedAt(
                                createdAt.toLocalDateTime());
                    }

                    return user;
                }
        );
    }

    // Get total number of users
    public int getUserCount() {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from users",
                Integer.class
        );

        return count != null ? count : 0;
    }

    // Get number of verified users
    public int getVerifiedUserCount() {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from users where email_verified = true",
                Integer.class
        );

        return count != null ? count : 0;
    }

    // Get number of unverified users
    public int getUnverifiedUserCount() {

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from users where email_verified = false",
                Integer.class
        );

        return count != null ? count : 0;
    }

    // Verify email
    public void verifyEmail(int userId) {

        jdbcTemplate.update(
                "update users set email_verified = true where user_id = ?",
                userId
        );
    }

    // Mark email as unverified
    public void markEmailUnverified(int userId) {

        jdbcTemplate.update(
                "update users set email_verified = false where user_id = ?",
                userId
        );
    }
}