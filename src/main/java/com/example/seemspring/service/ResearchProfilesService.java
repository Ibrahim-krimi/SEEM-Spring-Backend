package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.MatchRepository;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ResearchProfilesService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public  List<User> getRandomUserByGender(String id,String gender,Integer Nombredeprofile) {

        Query query =new Query();
        query.addCriteria(Criteria.where("gender").is(gender)).addCriteria(Criteria.where("id").is(id).not());
        List<User> users = mongoTemplate.find(query, User.class);
        Collections.shuffle(users);
        List<User> usersToReturn = users.subList(0, Nombredeprofile);

        return users.isEmpty()?null:usersToReturn;
    }




}
