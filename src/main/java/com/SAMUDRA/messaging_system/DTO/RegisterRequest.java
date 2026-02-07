package com.SAMUDRA.messaging_system.DTO;

public class RegisterRequest {

    private String username;
    private String email;
    private String password;
    private String profilePicUrl;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String profilePicUrl) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicUrl = profilePicUrl;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
}
