package com.example.seemspring.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
