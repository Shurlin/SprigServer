package xyz.shurlin.sprigserver.dto;

public class RegisterResponse {
    private String token;
    private String username;

    public RegisterResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
