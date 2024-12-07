package com.example.seemspring.controller;

import com.example.seemspring.ENUM.MatchStatus;
import com.example.seemspring.model.Match;
import com.example.seemspring.model.User;
import com.example.seemspring.service.MatchService;
import com.example.seemspring.service.ResearchProfilesService;
import com.example.seemspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service")
public class UserMatchAndLikeController {

    @Autowired
    UserService userService;

    @Autowired
    MatchService matchService;

    @Autowired
    ResearchProfilesService researchProfilesService;


    @GetMapping("/getProfiles")
    public ResponseEntity<?> getProfiles(@RequestParam("id") String userid
            , @RequestParam("genre") String genderLokingFor
            , @RequestParam("nombre") int nombredeProfile
    ) {
        try {
            List<User> profileList =this.researchProfilesService.getRandomUserByGenderWithInterst(userid, genderLokingFor, nombredeProfile);
            return ResponseEntity.ok(profileList);
        }catch (Exception e){

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne est survenue.");        }
    }

    @GetMapping("/like")
    public ResponseEntity<?>  likeProfile(@RequestParam("id")String id,
                                          @RequestParam("SecondUserid") String secondUserid){
        try {
            Boolean checkMatch=this.matchService.likeUser(id, secondUserid);
            return ResponseEntity.ok(checkMatch);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/getMatches")
    public ResponseEntity<?> getMatches(@RequestParam("id")String id){
        try {
            Map<User, Match> matchesList =this.matchService.getMatchesWithUserEntities(id);
            return ResponseEntity.ok(matchesList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());        }
    }

    @PostMapping("changeMatchStatus")
    public ResponseEntity<?> changeStatusMatch(@RequestParam("id")String matchid, @RequestBody MatchStatus matchStatus)
    {
        try {
            Match match=this.matchService.changestatusMatch(matchid, matchStatus);
            return ResponseEntity.ok(match);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




}
