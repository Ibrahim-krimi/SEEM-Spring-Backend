package com.example.seemspring.controller;

import com.example.seemspring.dto.UserUpdateDTO;
import com.example.seemspring.model.User;
import com.example.seemspring.service.BunnyCdnService;
import com.example.seemspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    public ResponseEntity<?> updateUserInformation(@RequestBody UserUpdateDTO updates) {
        String userId =  updates.getId();

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
    @PostMapping(value = "/{id}/FirstConnection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> FirstConnectionUser(@PathVariable String id, @RequestPart("photos") List<MultipartFile> photos,        @RequestPart("updates") UserUpdateDTO updates // JSON pour UserUpdateDTO
    ) {
        try {
            User updatedUser = this.userService.updatePartial(id, updates);

            Map<User, String> userWithMessage = this.bunnyCdnService.uploadUserPhotos(id, photos);

            return ResponseEntity.ok(userWithMessage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/updatePhoto")
    public ResponseEntity<?> updatePhoto(@PathVariable String id,@RequestParam("photos") List<MultipartFile> photos,List<String> PathphotoDelete) {
        try {
            Map<User, String> userWithMessage = this.bunnyCdnService.uploadAndDeleteUserPhotos(id, photos,PathphotoDelete);

            return ResponseEntity.ok(userWithMessage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
/*
    @DeleteMapping("/{id}/updatePhoto")
    public CompletableFuture<ResponseEntity<Map<User, String>>> deletePhoto(@PathVariable String id, @RequestBody List<String> photourl) {
        return bunnyCdnService.deleteUserPhotos(id, photourl)
                .thenApply(result -> ResponseEntity.ok(result))
                .exceptionally(ex -> {
                    return ResponseEntity.badRequest().body(Map.<User, String>of(null, "Une erreur est survenue : " + ex.getMessage()));
                });
    }

 */
@DeleteMapping("/{id}/deletePhoto")
public CompletableFuture<ResponseEntity<?>> deletePhoto(@PathVariable String id, @RequestBody List<String> photoUrls) {
    List<CompletableFuture<Boolean>> deletionResults = new ArrayList<>();

    for (String photoUrl : photoUrls) {
        deletionResults.add(bunnyCdnService.deletePhoto(id,photoUrl, "UsersPhotos"));
    }

    return CompletableFuture.allOf(deletionResults.toArray(new CompletableFuture[0]))
            .thenApply(voidResult -> {
                boolean hasFailure = deletionResults.stream()
                        .map(CompletableFuture::join)  // Bloquer pour obtenir le résultat des futures
                        .anyMatch(result -> !result);  // Vérifier si un des résultats est `false`

                if (hasFailure) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erreur lors de la suppression d'une ou plusieurs photos.");
                } else {
                    return ResponseEntity.ok("Photos supprimées avec succès.");
                }
            });
}



}
