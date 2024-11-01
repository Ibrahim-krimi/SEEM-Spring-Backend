package com.example.seemspring.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    //private String googleId;
    private Integer age;
    //private String profileImage;
    private String phoneNumber;
    private String country;
    private String city;
    private String bio;
    private List<String> interests;
    private Boolean emailValid;
    private Boolean phoneNumberValid;
    private  List<String> images;

    //////////////////////////////////////////////////// matching ////////
    private List<String> viewedProfiles;
    private String gender;
    private String lookingForGenders;
    //  private Integer minAgePreference;
    // private Integer maxAgePreference;
    // private Double locationLatitude;
    //  private Double locationLongitude;
    // List<String> matchingIntersts;

    //// Liste de MATCH

    private List<String> likes = new ArrayList<>();    // IDs des utilisateurs likés
    private List<String> matches = new ArrayList<>();  // IDs des matchs
}
