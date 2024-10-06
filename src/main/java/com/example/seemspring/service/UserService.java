package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public User findById(String id) {
        return this.userRepository.findById(id).orElse(null);
    }
    public User save(User user) {
        return  this.userRepository.save(user);
    }
    public User update(User userdetail) {
    return userdetail;
    }


    public User updatePartial(String id, Map<String, Object> updates) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + id);
        }

        User user = userOptional.get();

        // Vérifier les champs et mettre à jour les valeurs si elles existent dans la requête
        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("age")) {
            user.setAge((Integer) updates.get("age"));
        }
        if (updates.containsKey("phoneNumber")) {
            user.setPhoneNumber((String) updates.get("phoneNumber"));
        }
        if (updates.containsKey("country")) {
            user.setCountry((String) updates.get("country"));
        }
        if (updates.containsKey("city")) {
            user.setCity((String) updates.get("city"));
        }
        if (updates.containsKey("bio")) {
            user.setBio((String) updates.get("bio"));
        }
        if (updates.containsKey("interests")) {
            user.setInterests((List<String>) updates.get("interests"));
        }
        if (updates.containsKey("emailValid")) {
            user.setEmailValid((Boolean) updates.get("emailValid"));
        }
        if (updates.containsKey("phoneNumberValid")) {
            user.setPhoneNumberValid((Boolean) updates.get("phoneNumberValid"));
        }
        if (updates.containsKey("images")) {
            user.setImages((List<String>) updates.get("images"));
        }

        return userRepository.save(user);
    }
}
