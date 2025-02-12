package com.rudra.notes_App.Filters;

import jakarta.servlet.FilterChain;
import com.rudra.notes_App.Util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilters extends OncePerRequestFilter {

    @Lazy
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Check if Authorization header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);  // Extract JWT token
            username = jwtUtil.extractUsername(jwt);  // Extract username from the token
        }

        // If a username is found, load user details
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // Load user details from the service

            // If the JWT is valid, set the authentication
            if (jwtUtil.validateToken(jwt)) {
                // Create an authentication token with user details and empty authorities (no roles)
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, new ArrayList<>());  // Empty authorities

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // Set request-specific details

                // Set the authentication in the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }

}
