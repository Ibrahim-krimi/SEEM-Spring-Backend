package com.example.seemspring.service;

import com.example.seemspring.ENUM.MatchStatus;
import com.example.seemspring.model.Match;
import com.example.seemspring.model.User;
import com.example.seemspring.repository.MatchRepository;
import com.example.seemspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Match> getMatches(String userId) {
        return matchRepository.findByUserId1OrUserId2(userId, userId);
    }

    public boolean likeUser(String userId, String targetUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("Target User not found"));

        if (!user.getLikes().contains(targetUserId)) {
            user.getLikes().add(targetUserId);
            userRepository.save(user);
        }

        if (targetUser.getLikes().contains(userId)) {
            createMatch(userId, targetUserId);
            return true;
        }

        return false;
    }

    public Match createMatch(String userId1, String userId2) {
        Match match = new Match(userId1, userId2);
        matchRepository.save(match);

        User user1 = userRepository.findById(userId1).orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(userId2).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user1.getMatches().contains(userId2)) {
            user1.getMatches().add(userId2);
        }
        if (!user2.getMatches().contains(userId1)) {
            user2.getMatches().add(userId1);
        }

        userRepository.save(user1);
        userRepository.save(user2);

        return match;
    }

    public Map<User, Match> getMatchesWithUserEntities(String userId) {
        List<Match> matches = getMatches(userId);
        Map<User, Match> userMatches = new HashMap<>();

        for (Match match : matches) {
            String otherUserId = match.getUserId1().equals(userId) ? match.getUserId2() : match.getUserId1();
            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser != null) {
                userMatches.put(otherUser, match);
            }
        }

        return userMatches;
    }

    public Match changestatusMatch(String matchid, MatchStatus  matchStatus) {
        Match match = matchRepository.findById(matchid).orElseThrow(() -> new RuntimeException("Match not found"));
        match.setStatus(matchStatus);
        matchRepository.save(match);
        return match;
    }
}


