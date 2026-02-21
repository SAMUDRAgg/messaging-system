package com.SAMUDRA.messaging_system.DTO;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String profilePicUrl;

    public UserProfileResponse(Long id, String username, String email, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfilePicUrl() { return profilePicUrl; }

}