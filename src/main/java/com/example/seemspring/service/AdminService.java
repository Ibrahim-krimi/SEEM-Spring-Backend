package com.example.seemspring.service;


import com.example.seemspring.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.seemspring.model.Admin;
import com.example.seemspring.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository   ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Admin> authenticateAdmin(String email, String password) {
        Optional<Admin> admin = adminRepository.findByUsername(email);
        if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword())) {
            return admin;
        }
        return Optional.empty();
    }

    public Admin findByid(String id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        return admin;
    }
}
