package com.pet.pethubapi.domain.tvshow.audit;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
public final class TvMazeRequestAudit {

    @MongoId
    private String requestId;
    private LocalDateTime timestamp;
    private TvMazeRequestType requestType;

    public TvMazeRequestAudit(String requestId, TvMazeRequestType requestType) {
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
        this.requestType = requestType;
    }

}
