package com.SAMUDRA.messaging_system.DTO;

public class UserSearchResponse {

    private Long id;
    private String username;
    private String profilePicUrl;

    public UserSearchResponse(Long id, String username, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.profilePicUrl = profilePicUrl;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getProfilePicUrl() { return profilePicUrl; }
}