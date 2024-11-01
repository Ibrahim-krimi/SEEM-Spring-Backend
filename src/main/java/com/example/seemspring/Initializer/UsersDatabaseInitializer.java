package com.example.seemspring.Initializer;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class UsersDatabaseInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {

    }
/*
    @Autowired
    UserRepository userRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        List<String> names = Arrays.asList("Alex", "Jordan", "Taylor", "Morgan", "Casey", "Drew", "Jamie", "Reese", "Skyler", "Quinn", "Parker", "Blake");
        List<String> genders = Arrays.asList("male", "female");
        List<String> interestsPool = Arrays.asList("sports", "music", "cinema", "reading", "gaming", "cooking", "hiking", "traveling", "photography", "technology");

        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setName(getRandomName(names));
            user.setGender(getRandomGender(genders));
            user.setAge(getRandomAge(18, 60));
            user.setCountry("Country " + (i % 10 + 1));  // Exemple de pays simplifié
            user.setCity("City " + (i % 10 + 1));        // Exemple de ville simplifié
            user.setBio("This is a sample bio for user " + user.getName());
            user.setInterests(getRandomInterests(interestsPool));
            user.setEmailValid(true);
            user.setPhoneNumberValid(true);
            user.setPhoneNumber("123456789" + i);

            // Ajoute l'utilisateur à la base de données
            userRepository.save(user);
        }
    }

    private String getRandomName(List<String> names) {
        return names.get(random.nextInt(names.size())) + " " + (random.nextInt(900) + 100);  // Exemple: Alex 456
    }

    private String getRandomGender(List<String> genders) {
        return genders.get(random.nextInt(genders.size()));
    }

    private int getRandomAge(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private List<String> getRandomInterests(List<String> interestsPool) {
        int interestCount = random.nextInt(3) + 1;  // 1 à 3 intérêts au hasard
        List<String> selectedInterests = new ArrayList<>();
        for (int i = 0; i < interestCount; i++) {
            String interest = interestsPool.get(random.nextInt(interestsPool.size()));
            if (!selectedInterests.contains(interest)) {
                selectedInterests.add(interest);
            }
        }
        return selectedInterests;
    }

 */
}
