package com.pet.pethubapi.infrastructure.tvmaze.audit;

public interface TvMazeRequestAuditor {

    void auditGetAllShows();

    void auditShowByName();

    void auditShowById();

    void validateRequestOverflow();

}
