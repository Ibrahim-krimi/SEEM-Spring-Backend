package com.example.seemspring.controller;

import com.example.seemspring.model.User;
import com.example.seemspring.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody  Map<String, String> request ) {
            String accesToken = request.get("access_token");
            String email = request.get("email");
            
            try{
                User user = googleAuthService.handleGoogleLogin(accesToken, email);
                
                String jwtToken = createJwtToken(user);
                return ResponseEntity.ok(Map.of("token", jwtToken, "user", user));
                
            }catch (IllegalArgumentException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        
    }

    private String createJwtToken(User user) {
        return "jwt-token-exemple";
    }
}
