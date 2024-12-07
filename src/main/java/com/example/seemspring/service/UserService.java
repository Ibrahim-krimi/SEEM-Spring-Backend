package com.example.seemspring.service;

import com.example.seemspring.dto.UserUpdateDTO;
import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public User findById(String id) {
        return this.userRepository.findById(id).orElse(null);
    }
    public User save(User user) {
        return  this.userRepository.save(user);
    }
    public User update(User userdetail) {
    return userdetail;
    }


    public User updatePartial(String id, UserUpdateDTO updates) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + id);
        }

        User user = userOptional.get();

        // Mise à jour des champs si présents dans le DTO
        if (updates.getName() != null) {
            user.setName(updates.getName());
        }

        if (updates.getEmail() != null) {
            user.setEmail(updates.getEmail());
        }

        if (updates.getAge() != null) {
            // Conversion en chaîne si nécessaire
            user.setAge(String.valueOf(updates.getAge()));
        }

        if (updates.getPhoneNumber() != null) {
            user.setPhoneNumber(updates.getPhoneNumber());
        }

        if (updates.getCountry() != null) {
            user.setCountry(updates.getCountry());
        }

        if (updates.getCity() != null) {
            user.setCity(updates.getCity());
        }

        if (updates.getBio() != null) {
            user.setBio(updates.getBio());
        }

        if (updates.getEmailValid() != null) {
            user.setEmailValid(updates.getEmailValid());
        }

        if (updates.getPhoneNumberValid() != null) {
            user.setPhoneNumberValid(updates.getPhoneNumberValid());
        }

        // Gestion des intérêts (conversion JSON ou liste brute)
        if (updates.getInterests() != null) {
            try {
                /*
                if (updates.getInterests() instanceof String) {
                    // Si les intérêts sont fournis en tant que JSON string
                    String interestsString = (String) updates.getInterests();
                    List<String> interests = objectMapper.readValue(interestsString, new TypeReference<List<String>>() {});
                    user.setInterests(interests);
                } else if (updates.getInterests() instanceof List) {
                    // Si les intérêts sont déjà une liste
                    user.setInterests((List<String>) updates.getInterests());
                }

                 */
            } catch (Exception e) {
                System.err.println("Les intérêts fournis ne sont pas valides : " + e.getMessage());
            }
        }

        // Sauvegarde de l'utilisateur mis à jour
        return userRepository.save(user);
    }

}
