package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return this.userRepository.findById(id);
    }
    public User save(User user) {
        return  this.userRepository.save(user);
    }
    public User update(User userdetail) {
    return userdetail;
    }
}
