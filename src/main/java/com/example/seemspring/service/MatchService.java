package com.example.seemspring.service;

import com.example.seemspring.model.Match;
import com.example.seemspring.model.User;
import com.example.seemspring.repository.MatchRepository;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {
@Autowired
    private MatchRepository matchRepository;
@Autowired
    private UserRepository userRepository;

public Match createMatch(String userid1, String userid2) {
    Match match = new Match(userid1, userid2);
    matchRepository.save(match);

    User user1 = userRepository.findById(userid1).orElseThrow(()-> new RuntimeException("User not found"));
    User user2 = userRepository.findById(userid2).orElseThrow(()-> new RuntimeException("User not found"));

    user1.getMatches().add(userid2);
    user2.getMatches().add(userid1);
    userRepository.save(user1);
    userRepository.save(user2);
    return match;
}

public List<Match> getMatchesForUser(String userid) {
    return matchRepository.findByUserId1OrUserId2(userid, userid);
}

public Boolean likeAndCheckMatch(String userid1, String userid2) {
    User user1 = userRepository.findById(userid1).orElseThrow(()-> new RuntimeException("User not found"));
    User user2 = userRepository.findById(userid2).orElseThrow(()-> new RuntimeException("User not found"));
    user2.getLikes().add(userid1);
    userRepository.save(user2);
    if (user1.getLikes().contains(userid2)) {
        createMatch(userid1, userid2);
        return true;
    }
    return false;
}

}
