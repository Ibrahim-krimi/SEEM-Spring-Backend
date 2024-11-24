package com.example.seemspring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class UserDTO {
    @Id
    @NotBlank(message = "ID est obligatoire")
    private String id;
    private String name;
    @Email(message = "Email invalide")
    private String email;
    private String age;
    private String phoneNumber;
    private String country;
    private String city;
    private String bio;
    private List<String> interests;
    private Boolean emailValid;
    private Boolean phoneNumberValid;
    private  List<String> images;
}

