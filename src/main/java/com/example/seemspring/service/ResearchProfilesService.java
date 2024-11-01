package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.MatchRepository;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ResearchProfilesService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;



    @Async
    public List<User> getRandomUserByGenderWithInterst(String id, String gender, Integer Nombredeprofile) {
        // Récupérer l'utilisateur actuel par ID
        User currentUser = userRepository.findById(id).orElse(null);
        if (currentUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Récupérer les profils déjà vus
        List<String> viewedProfiles = currentUser.getViewedProfiles();
        if (viewedProfiles == null) {
            viewedProfiles = Collections.emptyList(); // si la liste est vide
        }

        // Récupérer les intérêts de l'utilisateur actuel
        List<String> currentUserInterests = currentUser.getInterests();
        if (currentUserInterests == null) {
            currentUserInterests = Collections.emptyList(); // si pas d'intérêts
        }

        // Première requête : chercher des utilisateurs qui partagent des intérêts et qui ne sont pas encore vus
        Query queryWithSharedInterests = new Query();
        queryWithSharedInterests.addCriteria(Criteria.where("gender").is(gender));
        queryWithSharedInterests.addCriteria(Criteria.where("id").ne(id)); // Exclure l'utilisateur lui-même
        queryWithSharedInterests.addCriteria(Criteria.where("id").nin(viewedProfiles)); // Exclure les utilisateurs déjà vus
        if (!currentUserInterests.isEmpty()) {
            queryWithSharedInterests.addCriteria(Criteria.where("interests").in(currentUserInterests)); // Critère d'intérêts partagés
        }

        // Rechercher les utilisateurs avec des intérêts communs
        List<User> usersWithSharedInterests = mongoTemplate.find(queryWithSharedInterests, User.class);

        // Compléter avec d'autres utilisateurs si nécessaire (sans contrainte d'intérêts)
        if (usersWithSharedInterests.size() < Nombredeprofile) {
            Query queryWithoutSharedInterests = new Query();
            queryWithoutSharedInterests.addCriteria(Criteria.where("gender").is(gender));
            queryWithoutSharedInterests.addCriteria(Criteria.where("id").ne(id)); // Exclure l'utilisateur lui-même
            queryWithoutSharedInterests.addCriteria(Criteria.where("id").nin(viewedProfiles)); // Exclure les utilisateurs déjà vus
            queryWithoutSharedInterests.addCriteria(Criteria.where("interests").nin(currentUserInterests)); // Exclure les intérêts déjà cherchés

            // Rechercher des utilisateurs sans intérêt partagé
            List<User> otherUsers = mongoTemplate.find(queryWithoutSharedInterests, User.class);

            // Ajouter les autres utilisateurs pour compléter la liste
            usersWithSharedInterests.addAll(otherUsers);
        }

        // Mélanger la liste finale des utilisateurs
        Collections.shuffle(usersWithSharedInterests);

        // Limiter le nombre d'utilisateurs retournés
        List<User> usersToReturn = usersWithSharedInterests.size() > Nombredeprofile ? usersWithSharedInterests.subList(0, Nombredeprofile) : usersWithSharedInterests;

        // Ajouter les utilisateurs vus à la liste des profils déjà vus
        List<String> newViewedProfiles = usersToReturn.stream().map(User::getId).toList();
        currentUser.getViewedProfiles().addAll(newViewedProfiles);

        // Sauvegarder les modifications
        userRepository.save(currentUser);

        // Retourner la liste des utilisateurs à afficher
        return usersToReturn.isEmpty() ? null : usersToReturn;
    }


      /*
     @Async
    public List<User> getRandomUserByGender(String id, String gender, Integer Nombredeprofile) {
        // Récupérer l'utilisateur actuel par ID
        User currentUser = userRepository.findById(id).orElse(null);
        if (currentUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<String> viewedProfiles = currentUser.getViewedProfiles();
        if (viewedProfiles == null) {
            viewedProfiles = Collections.emptyList(); // si la liste est vide
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("gender").is(gender));
        query.addCriteria(Criteria.where("id").ne(id)); // Exclure l'utilisateur lui-même
        query.addCriteria(Criteria.where("id").nin(viewedProfiles)); // Exclure les utilisateurs déjà vus

        List<User> users = mongoTemplate.find(query, User.class);

        Collections.shuffle(users);

        // Sélectionner un sous-ensemble selon le nombre requis
        List<User> usersToReturn = users.size() > Nombredeprofile ? users.subList(0, Nombredeprofile) : users;

        // Ajouter les utilisateurs vus à la liste des profils déjà vus
        List<String> newViewedProfiles = usersToReturn.stream().map(User::getId).toList();
        currentUser.getViewedProfiles().addAll(newViewedProfiles);

        userRepository.save(currentUser);

        return users.isEmpty() ? null : usersToReturn;
    }
     */



}
