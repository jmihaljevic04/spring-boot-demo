package com.pet.pethubapi.domain.tvshow.audit;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
public final class TvMazeRequestAudit {

    @MongoId(FieldType.OBJECT_ID)
    private String requestId;

    @Indexed
    private LocalDateTime timestamp;

    @Field(targetType = FieldType.STRING)
    private TvMazeRequestType requestType;

    public TvMazeRequestAudit(TvMazeRequestType requestType) {
        this.timestamp = LocalDateTime.now();
        this.requestType = requestType;
    }

}
