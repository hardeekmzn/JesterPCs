package io.virinchi.jesterpcs.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetDAO {

    private final JdbcTemplate jdbcTemplate;

    public PasswordResetDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Save a password reset token
    public void addResetToken(
            int userId,
            String token,
            java.sql.Timestamp expiresAt) {

        jdbcTemplate.update(
                "insert into password_reset (user_id, token, expires_at) values (?, ?, ?)",
                userId,
                token,
                expiresAt
        );
    }

    // Get user ID from a reset token
    public Integer getUserIdByToken(String token) {

        return jdbcTemplate.query(
                """
                select user_id
                from password_reset
                where token = ?
                and expires_at > current_timestamp
                """,
                rs -> {
                    if (rs.next()) {
                        return rs.getInt("user_id");
                    }
                    return null;
                },
                token
        );
    }

    // Delete one reset token
    public void deleteToken(String token) {

        jdbcTemplate.update(
                "delete from password_reset where token = ?",
                token
        );
    }

    // Delete existing reset tokens for a user
    public void deleteTokensForUser(int userId) {

        jdbcTemplate.update(
                "delete from password_reset where user_id = ?",
                userId
        );
    }
}