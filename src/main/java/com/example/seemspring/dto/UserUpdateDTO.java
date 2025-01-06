package com.example.seemspring.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserUpdateDTO {
    private String id;
    private String name;
    private String email;
    private Integer age; // Pour les valeurs d'âge (entier)
    private String country;
    private String city;
    private String bio;
    private Boolean emailValid; // Champ pour valider l'email
    private Boolean phoneNumberValid; // Champ pour valider le numéro de téléphone
    private String phoneNumber;
    private List<String> interests;
    private String gender;

    // Getters et Setters
}
