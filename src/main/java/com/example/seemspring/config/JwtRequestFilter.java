package com.example.seemspring.config;

import com.example.seemspring.model.Admin;
import com.example.seemspring.service.AdminService;
import com.example.seemspring.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminService adminService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String userId = null;
        String jwtToken = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            userId = jwtUtil.extractUserId(jwtToken);
            role = jwtUtil.extractRole(jwtToken);

        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Validation du Token
            if (jwtUtil.validateToken(jwtToken)) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }

                // Créer une authentification utilisateur
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Vérification si c'est un administrateur (logique supplémentaire)
                Admin admin = adminService.findByid(userId);
                if (admin != null && "ROLE_ADMIN".equals(role)) {
                    // Ajouter explicitement le rôle admin si nécessaire
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    authentication = new UsernamePasswordAuthenticationToken(admin, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
