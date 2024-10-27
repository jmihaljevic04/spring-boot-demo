package com.pet.pethubapi.domain.tvshow.audit;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TvMazeRequestAuditRepository extends MongoRepository<TvMazeRequestAudit, String> {

    long countByTimestampAfter(LocalDateTime timestamp);

}
