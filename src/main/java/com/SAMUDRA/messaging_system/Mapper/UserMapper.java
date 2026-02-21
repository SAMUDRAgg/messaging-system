package com.SAMUDRA.messaging_system.Mapper;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DTO.UserProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileResponse mapToProfile(User user) {

        if (user == null) {
            return null;
        }

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicUrl()
        );
    }
}