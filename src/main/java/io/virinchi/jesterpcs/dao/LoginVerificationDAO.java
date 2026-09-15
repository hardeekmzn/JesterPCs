package io.virinchi.jesterpcs.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoginVerificationDAO {

    private final JdbcTemplate jdbcTemplate;

    public LoginVerificationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLoginToken(int userId, String token) {

        String sql = "insert into login_verification (user_id, token) values (?, ?)";

        jdbcTemplate.update(
                sql,
                userId,
                token
        );
    }

    public Integer getUserIdByToken(String token) {

        String sql = "select user_id from login_verification where token = ?";

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

        String sql = "delete from login_verification where token = ?";

        jdbcTemplate.update(sql, token);
    }

    public void deleteTokensForUser(int userId) {

        String sql = "delete from login_verification where user_id = ?";

        jdbcTemplate.update(sql, userId);
    }
}