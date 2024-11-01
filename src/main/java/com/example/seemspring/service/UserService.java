package com.example.seemspring.service;

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


    public User updatePartial(String id, Map<String, Object> updates) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + id);
        }

        User user = userOptional.get();

        // convertitre les champs integer to string ?


        // Validation et conversion pour l'âge
        if (updates.containsKey("age")) {
            String ageString = updates.get("age").toString();
            user.setAge(ageString);

        }
        if (updates.containsKey("interests")) {
            try {
                if (updates.get("interests") instanceof String) {
                    String interestsString = (String) updates.get("interests");
                    List<String> interests = objectMapper.readValue(interestsString, new TypeReference<List<String>>() {});
                    user.setInterests(interests);
                } else if (updates.get("interests") instanceof List) {
                    user.setInterests((List<String>) updates.get("interests"));
                }
            } catch (Exception e) {
                System.err.println("Les intérêts fournis ne sont pas valides.");
            }
        }

        // Vérifier les champs et mettre à jour les valeurs si elles existent dans la requête
        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }

        if (updates.containsKey("phoneNumber")) {
            String phoneNumber = updates.get("phoneNumber").toString();
            user.setPhoneNumber(phoneNumber);
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
        if (updates.containsKey("emailValid")) {
            user.setEmailValid((Boolean) updates.get("emailValid"));
        }
        if (updates.containsKey("phoneNumberValid")) {
            user.setPhoneNumberValid((Boolean) updates.get("phoneNumberValid"));
        }

        return userRepository.save(user);
    }
}
