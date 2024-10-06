package com.example.seemspring.service;

import com.example.seemspring.model.User;
import com.example.seemspring.repository.UserRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
                String remoteFileName = userId + "-" + System.currentTimeMillis() + "-" + filenames.get(i);
                try (InputStream inputStream = files.get(i)) {
                    boolean done = ftpClient.storeFile(remoteFileName, inputStream);
                    if (done) {
                        uploadedUrls.add(pullZoneUrl + "/" + remoteFileName);
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

            return Map.of(user,"L'upload est en cours, vous serez notifié une fois terminé");
        } catch (IOException e) {
            return Map.of(user,e.getMessage());
        }
    }



}
