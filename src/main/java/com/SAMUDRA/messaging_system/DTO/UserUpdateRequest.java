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


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

}