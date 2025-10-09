package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try finding by username
        Optional<User> user = userRepo.findByUsername(identifier);

        // If not found, try email
        if (user.isEmpty()) {
            user = userRepo.findByEmail(identifier);
        }

        // If still not found, throw exception
        if (user.isPresent()) {
            return new UserPrincipal(user.get());
        } else {
            throw new UsernameNotFoundException("User not found with username/email: " + identifier);
        }
    }
}
