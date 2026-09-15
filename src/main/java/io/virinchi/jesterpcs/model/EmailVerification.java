package io.virinchi.jesterpcs.model;

public class EmailVerification {

    private int verificationId;
    private int userId;
    private String token;

    public EmailVerification() {
    }

    public EmailVerification(int verificationId, int userId, String token) {
        this.verificationId = verificationId;
        this.userId = userId;
        this.token = token;
    }

    public EmailVerification(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public int getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(int verificationId) {
        this.verificationId = verificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}