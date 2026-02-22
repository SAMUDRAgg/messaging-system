package com.SAMUDRA.messaging_system.filter;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.Service.JwtService;
import com.SAMUDRA.messaging_system.Service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepo userRepo;

    // ⚡ Add in-memory caches to prevent repeated DB hits
    private static final Map<Long, User> userCache = new ConcurrentHashMap<>();
    private static final Map<String, UserDetails> userDetailsCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        Long userId = null;

        // 1️⃣ Extract token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                userId = jwtService.extractUserId(token);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid or expired JWT token");
            }
        }

        // 2️⃣ Validate and set authentication
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ⚡ Check cache before hitting DB
            User user = userCache.computeIfAbsent(userId, id ->
                    userRepo.findById(id).orElse(null)
            );

            if (user != null) {
                // ⚡ Cache userDetails too, avoid loadUserByUsername() hit every time
                UserDetails userDetails = userDetailsCache.computeIfAbsent(user.getUsername(), uname ->
                        userDetailsService.loadUserByUsername(uname)
                );

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ Authenticated user: " + user.getUsername());
                } else {
                    throw new BadCredentialsException("Invalid or expired JWT token" + " for userId: " + userId);
                }
            } else {
                throw new BadCredentialsException("Invalid or expired JWT token" + " for userId: " + userId);
            }
        }

        filterChain.doFilter(request, response);
    }
}
