package com.example.seemspring.repository;

import com.example.seemspring.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatchRepository extends MongoRepository<Match, String> {
    public List<Match> findByUserId1OrUserId2(String userId1, String userId2);

}
