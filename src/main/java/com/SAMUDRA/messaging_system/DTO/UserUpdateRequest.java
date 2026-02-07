package com.SAMUDRA.messaging_system.DTO;

public class UserUpdateRequest {

    private String username;
    private String email;
    private String password;
    private String profilePicUrl;

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfilePicUrl() { return profilePicUrl; }
}