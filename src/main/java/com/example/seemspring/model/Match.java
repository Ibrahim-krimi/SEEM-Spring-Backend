package com.example.seemspring.model;

import com.example.seemspring.ENUM.MatchStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "matches")
public class Match {
    @Id
    private String  id;
    private String  userId1;
    private String  userId2;
    private Date matchedAt;

    private MatchStatus status;

    public Match(String userId1, String userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.matchedAt = new Date();
        this.status=MatchStatus.NOT_STARTED; // comme tinder ils ont pas encore commencer a parler
    }
}
