package com.example.seemspring.controller;

import com.example.seemspring.model.User;
import com.example.seemspring.service.BunnyCdnService;
import com.example.seemspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserInformationController {

    @Autowired
    private UserService userService;
    @Autowired
    private BunnyCdnService bunnyCdnService;

    @GetMapping("/information/{id}")
    public ResponseEntity<?> getUserInformation(@PathVariable String id) {
        if (this.userService.findById(id)==null) {
            return ResponseEntity.notFound().build();
        }
        try {
            User user = this.userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PatchMapping("/updateUserInformation")
    public ResponseEntity<?> updateUserInformation(@RequestBody Map<String, Object> updates) {
        String userId = (String) updates.get("id");

        if (userId == null) {
            return ResponseEntity.badRequest().body("L'ID de l'utilisateur est obligatoire");
        }

        try {
            User updatedUser = this.userService.updatePartial(userId, updates);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/{id}/photos")
    public ResponseEntity<?> FirstConnectionUser(@PathVariable String id, @RequestParam("photos") List<MultipartFile> photos, @RequestParam Map<String, Object> updates) {
        try {
            Map<User, String> userWithMessage = this.bunnyCdnService.uploadUserPhotos(id, photos);

            User updatedUser = this.userService.updatePartial(id, updates);

            return ResponseEntity.ok(updatedUser);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }




    public ResponseEntity<?> testApiPhoto(){
        return ResponseEntity.ok("success");
    }



    public ResponseEntity<?> updateUserPhoto(){
        return ResponseEntity.ok("success");
    }

}
