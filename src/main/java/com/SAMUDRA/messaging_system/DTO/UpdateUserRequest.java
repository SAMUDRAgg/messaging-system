package com.SAMUDRA.messaging_system.DTO;

public class UpdateUserRequest {
    private String username;
    private String email;
    private String profilePicUrl;
    private String password; // optional: only if user wants to change

    public UpdateUserRequest() {}

    public UpdateUserRequest(String username, String email, String profilePicUrl, String password) {
        this.username = username;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.password = password;
    }

    // getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
