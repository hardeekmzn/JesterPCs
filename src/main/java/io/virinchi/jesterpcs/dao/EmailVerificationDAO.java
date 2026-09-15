package io.virinchi.jesterpcs.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmailVerificationDAO {

    private final JdbcTemplate jdbcTemplate;

    public EmailVerificationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addVerificationToken(int userId, String token) {

        String sql = "insert into email_verification (user_id, token) values (?, ?)";

        jdbcTemplate.update(
                sql,
                userId,
                token
        );
    }

    public Integer getUserIdByToken(String token) {

        String sql = "select user_id from email_verification where token = ?";

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return rs.getInt("user_id");
                    }
                    return null;
                },
                token
        );
    }

    public void deleteToken(String token) {

        String sql = "delete from email_verification where token = ?";

        jdbcTemplate.update(sql, token);
    }

    public void deleteTokensForUser(int userId) {

        String sql = "delete from email_verification where user_id = ?";

        jdbcTemplate.update(sql, userId);
    }
}