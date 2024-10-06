package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class GoogleAuthService {

    @Autowired
    private UserRepository userRepository;


    public User handleGoogleLogin(String accessToken , String email){
        RestTemplate restTemplate = new RestTemplate();
        String googleUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
        try {
            Map<String, Object> response = restTemplate.getForObject(googleUrl, Map.class);

            if (response != null && response.get("email").equals(email)) {
                // token est valide

                User user = userRepository.findByEmail(email);

                if (user == null){
                    user = new User();

                    user.setEmail(email);
                    userRepository.save(user);

                }
                return user;
            }else {
                throw new IllegalArgumentException("Le token est invalide ou l'email ne correspond pas");
            }
        }catch (HttpClientErrorException e){
            throw new IllegalArgumentException("Erreur lors de la verfication");
        }

    }

}
