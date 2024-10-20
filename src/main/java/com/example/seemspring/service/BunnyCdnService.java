package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
public class BunnyCdnService {

    @Autowired
    private  UserRepository userRepository;

    @Value("${bunny.ftp.host}")
    private String ftpHost;

    @Value("${bunny.ftp.username}")
    private String ftpUsername;

    @Value("${bunny.ftp.password}")
    private String ftpPassword;

    @Value("${bunny.pull.zone.url}")
    private String pullZoneUrl;

    //private final RestTemplate restTemplate = new RestTemplate();
    private final OkHttpClient client = new OkHttpClient();

    @Async
    public CompletableFuture<List<String>> uploadFilesAsync(String userId, List<InputStream> files, List<String> filenames) {
        FTPClient ftpClient = new FTPClient();
        List<String> uploadedUrls = new ArrayList<>();

        try {
            ftpClient.connect(ftpHost);
            ftpClient.login(ftpUsername, ftpPassword);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            for (int i = 0; i < files.size(); i++) {

                String cleanedFileName = cleanFileName(filenames.get(i));
                String remoteFileName = "UsersPhotos/" + userId + "-" + System.currentTimeMillis() + "-" + cleanedFileName;
                remoteFileName = remoteFileName.replaceAll("\\s+", "");

                try (InputStream inputStream = files.get(i)) {
                    boolean done = ftpClient.storeFile(remoteFileName, inputStream);
                    if (done) {
                        uploadedUrls.add(pullZoneUrl + "/UsersPhotos/" + remoteFileName);
                    } else {
                        throw new IOException("Failed to upload file: " + filenames.get(i));
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error connecting to BunnyCDN FTP: " + ex.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return CompletableFuture.completedFuture(uploadedUrls);
    }



    public Map<User,String> uploadUserPhotos(String id, List<MultipartFile> photos) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return Map.of(user,"User not found");
        }

        if (photos.isEmpty()) {
            return Map.of(user,"pas de photos");
        }

        try {
            List<InputStream> fileStreams = new ArrayList<>();
            List<String> filenames = new ArrayList<>();
            for (MultipartFile photo : photos) {
                fileStreams.add(photo.getInputStream());
                filenames.add(photo.getOriginalFilename());
            }

            uploadFilesAsync(id, fileStreams, filenames)
                    .thenAccept(uploadedUrls -> {
                        if (user.getImages() == null) {
                            user.setImages(new ArrayList<>());
                        }
                        user.getImages().addAll(uploadedUrls);
                        if (user.getImages().size() > 7) {
                            user.setImages(user.getImages().subList(0, 7)); // Limiter à 7 photos max
                        }
                        userRepository.save(user);
                    });

            return Map.of(user,"L'upload est en cours");
        } catch (IOException e) {
            return Map.of(user,e.getMessage());
        }
    }


    public Map<User, String> uploadAndDeleteUserPhotos(String id, List<MultipartFile> photos, List<String> pathphotoDelete) {

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return Map.of(user,"User not found");
        }



        try {
            if (!photos.isEmpty()) {
                List<InputStream> fileStreams = new ArrayList<>();
                List<String> filenames = new ArrayList<>();
                for (MultipartFile photo : photos) {
                    fileStreams.add(photo.getInputStream());
                    filenames.add(photo.getOriginalFilename());
                }

                uploadFilesAsync(id, fileStreams, filenames)
                        .thenAccept(uploadedUrls -> {
                            if (user.getImages() == null) {
                                user.setImages(new ArrayList<>());
                            }
                            user.getImages().addAll(uploadedUrls);
                            if (user.getImages().size() > 7) {
                                user.setImages(user.getImages().subList(0, 7)); // Limiter à 7 photos max
                            }
                            userRepository.save(user);
                        });
            }
            if (!pathphotoDelete.isEmpty()) {
                pathphotoDeleteFromBunnyCDN(pathphotoDelete).thenAccept( deleted ->{
                    if (deleted){
                     user.getImages().removeIf(pathphotoDelete::contains);
                     userRepository.save(user);
                    }
                });
            }


            return Map.of(user,"L'upload et le suppression sont  en cours");
        } catch (IOException e) {
            return Map.of(user,e.getMessage());
        }
    }

    /*public CompletableFuture<Map<User, String>> deleteUserPhotos(String id, List<String> pathphotoDelete) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return CompletableFuture.completedFuture(Map.of(user, "User not found"));
        }

        if (!pathphotoDelete.isEmpty()) {
            if (!user.getImages().containsAll(pathphotoDelete)) {
                return CompletableFuture.completedFuture(Map.of(user, "Certaines photos ne sont pas associées à cet utilisateur"));
            }

            return pathphotoDeleteFromBunnyCDN(pathphotoDelete).thenApply(deleted -> {
                if (deleted) {
                    user.getImages().removeIf(pathphotoDelete::contains);
                    userRepository.save(user);
                    return Map.of(user, "Suppression réussie");
                } else {
                    return Map.of(user, "Échec de la suppression des photos");
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return Map.of(user, "Une erreur est survenue lors de la suppression des photos");
            });
        }

        return CompletableFuture.completedFuture(Map.of(user, "Aucune photo à supprimer"));
    }
     */

    @Async
    public CompletableFuture<Boolean> pathphotoDeleteFromBunnyCDN(List<String> pathphotoDelete) {
        FTPClient client = new FTPClient();
        return CompletableFuture.supplyAsync(()->{
      try {
          client.connect(ftpHost);
          client.login(ftpUsername, ftpPassword);
          client.enterLocalPassiveMode();
          for (String path : pathphotoDelete) {
              client.deleteFile(path);
          }
          client.logout();
          client.disconnect();
          return true;
      } catch (SocketException e) {
            e.printStackTrace();
            return false;
          } catch (IOException e) {
            e.printStackTrace();
            return false;
          }
        });
    }

    @Async
    public CompletableFuture<Boolean> deletePhoto(String url, String dir) {
        // URL complète pour supprimer la photo
        try {
            String filename = getFileNameFromUrl(url);
            System.out.println(filename);
            Request request = new Request.Builder().
                    url("https://storage.bunnycdn.com/"+this.ftpUsername+"/"+dir+"/"+filename)
                    .delete(null)
                    .addHeader("AccessKey",this.ftpPassword)
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                System.out.println("Photo supprimée avec succès: " + url);
                return CompletableFuture.completedFuture(true);
            } else {
                System.err.println("Erreur lors de la suppression de la photo: " + response.body().string());
                return CompletableFuture.completedFuture(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    public static String getFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
    private String cleanFileName(String filename) {
        String cleanedFileName = filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "");  // Garder les lettres, chiffres, points et tirets

        int extensionIndex = cleanedFileName.lastIndexOf('.');
        String baseName = (extensionIndex > 0) ? cleanedFileName.substring(0, extensionIndex) : cleanedFileName;
        String extension = (extensionIndex > 0) ? cleanedFileName.substring(extensionIndex) : "";

        if (baseName.length() > 50) {
            baseName = baseName.substring(0, 50);
        }

        return baseName + extension;
    }

}
