package com.example.device_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class HeaderAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader("H-User-Id");
        String role   = request.getHeader("H-User-Role");
        String email  = request.getHeader("H-User-Email");

        // If headers exist, set authentication. Otherwise, just continue
        if (StringUtils.hasText(userId) && StringUtils.hasText(role)) {
            // Build a simple Authentication with the ROLE as "authority"
            var auth = new UsernamePasswordAuthenticationToken(
                    email != null ? email : userId,
                    null,
                    List.of(new SimpleGrantedAuthority(role)) // <- matches hasAuthority('ADMIN')
            );

            // Optionally attach headers to request attributes for controllers to read if needed
            request.setAttribute("H-User-Id", userId);
            request.setAttribute("H-User-Role", role);
            request.setAttribute("H-User-Email", email);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
}
