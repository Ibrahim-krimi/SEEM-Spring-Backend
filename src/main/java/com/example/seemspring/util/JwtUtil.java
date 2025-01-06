package com.example.seemspring.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
private static final String SECRET = "139DA139CC4813D26177E6259ADBC";

public String generateToken(String userId, String role){
    return Jwts.builder()
            .setSubject(userId) // c'est important pour identifier qui est l'utilisateur
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis()+86400000))
            .signWith(SignatureAlgorithm.HS256,SECRET) // algo HS256
            .compact(); // et la creation
}
//elle renvoie les données (claims) contenues dans le JWT.
//methode pour Extraiare les information d'un JWT
    public Claims extractTokenClaim(String token){
    return  Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody();
    }

//methode pour extraiare l'id d'un Token
    public String extractUserId(String token){
        return extractTokenClaim(token).getSubject();
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class); // Récupère le rôle comme une chaîne
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

// Valider le Token pour voir s'il est valide
    public boolean validateToken(String token){
    try {
        extractTokenClaim(token);
        return true;
    }
    catch (MalformedJwtException | SignatureException e){
        return false;
    }
    }
}
