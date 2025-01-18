package com.pet.pethubapi.infrastructure.tvmaze.audit;

import com.pet.pethubapi.domain.ApiApplicationProperties;
import com.pet.pethubapi.domain.tvshow.audit.TvMazeRequestAudit;
import com.pet.pethubapi.domain.tvshow.audit.TvMazeRequestAuditRepository;
import com.pet.pethubapi.domain.tvshow.audit.TvMazeRequestType;
import com.pet.pethubapi.infrastructure.tvmaze.TvMazeIntegrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TvMazeRequestAuditorImpl implements TvMazeRequestAuditor {

    private final TvMazeRequestAuditRepository auditRepository;
    private final ApiApplicationProperties applicationProperties;

    @Override
    public void auditGetAllShows() {
        auditRequest(TvMazeRequestType.GET_ALL_SHOWS);
    }

    @Override
    public void auditShowByName() {
        auditRequest(TvMazeRequestType.GET_SHOW_BY_NAME);
    }

    @Override
    public void auditShowById() {
        auditRequest(TvMazeRequestType.GET_SHOW_BY_ID);
    }

    @Override
    public void validateRequestOverflow() {
        validateDailyRequestLimit();
        validateRequestRateLimit();
    }

    private void auditRequest(TvMazeRequestType requestType) {
        auditRepository.save(new TvMazeRequestAudit(requestType));
    }

    private void validateDailyRequestLimit() {
        final var dailyRequestLimit = applicationProperties.getTvMaze().getRateLimit().getMaxDaily();
        final var midnightToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        final var numberOfRequestsToday = auditRepository.countByTimestampAfter(midnightToday);

        if (numberOfRequestsToday >= dailyRequestLimit) {
            throw new TvMazeIntegrationException("Number of requests executed starting from midnight exceeds daily request limit of: " + dailyRequestLimit + "! Please wait until tomorrow!");
        }
    }

    private void validateRequestRateLimit() {
        final var requestRateLimit = applicationProperties.getTvMaze().getRateLimit().getMaxPerInterval();
        final var requestRateLimitInterval = applicationProperties.getTvMaze().getRateLimit().getTimeInterval();

        final var numberOfRequests = auditRepository.countByTimestampAfter(LocalDateTime.now().minusSeconds(requestRateLimitInterval).withNano(0));

        if (numberOfRequests >= requestRateLimit) {
            throw new TvMazeIntegrationException("Number of requests executed starting in last: " + requestRateLimitInterval + " seconds exceeds request limit of: " + requestRateLimit + " requests! Please wait for next: " + requestRateLimitInterval + " seconds!");
        }
    }

}
