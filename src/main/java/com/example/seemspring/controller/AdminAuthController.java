package com.example.seemspring.controller;

import com.example.seemspring.model.Admin;
import com.example.seemspring.model.User;
import com.example.seemspring.service.AdminService;
import com.example.seemspring.service.UserService;
import com.example.seemspring.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> userCreate(@RequestBody Map<String, String> request){
        String email = request.get("email");
        String password = request.get("password");

        adminService.createUser(email, password);

        return  ResponseEntity.ok(Map.of("message", "user created"));
    }
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            Admin admin = adminService.authenticateAdmin(email, password).orElse(null);
            if (admin == null) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé ou mot de passe incorrect.");
            }

            String jwtToken = jwtUtil.generateToken(admin.getId(), "ROLE_ADMIN");
            return ResponseEntity.ok(Map.of("token", jwtToken, "admin", admin));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la connexion : " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Accès autorisé.");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users;
        try {
            users = userService.getAllUsers();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/manage-user-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> manageUserStatus(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        Boolean isBlocked = (Boolean) request.get("isBlocked");

        try {
            userService.updateUserBlockedStatus(email, isBlocked);
            String action = isBlocked ? "blocked" : "unblocked";
            return ResponseEntity.ok(Map.of("message", "User " + action + " successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



}
