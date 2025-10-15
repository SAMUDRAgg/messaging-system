package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // ✅ Try username first, then email
        User user = userRepo.findByUsername(identifier)
                .or(() -> userRepo.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username/email: " + identifier));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled: " + identifier);
        }

        return new UserPrincipal(user);
    }
}
