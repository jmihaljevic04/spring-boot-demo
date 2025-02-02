package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.domain.ApiApplicationProperties;
import com.pet.pethubapi.domain.tvshow.audit.TvMazeRequestAudit;
import com.pet.pethubapi.domain.tvshow.audit.TvMazeRequestAuditRepository;
import com.pet.pethubapi.domain.tvshow.audit.TvMazeRequestType;
import com.pet.pethubapi.infrastructure.tvmaze.audit.TvMazeRequestAuditorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TvMazeRequestAuditorShould {

    @Mock
    private TvMazeRequestAuditRepository requestAuditRepositoryMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ApiApplicationProperties applicationPropertiesMock;

    @InjectMocks
    private TvMazeRequestAuditorImpl tvMazeRequestAuditor;

    @Test
    void shouldAuditGetAllShows() {
        tvMazeRequestAuditor.auditGetAllShows();

        var auditEntityCaptor = ArgumentCaptor.forClass(TvMazeRequestAudit.class);
        verify(requestAuditRepositoryMock).save(auditEntityCaptor.capture());
        verifyNoMoreInteractions(requestAuditRepositoryMock);

        var expectedAuditEntity = new TvMazeRequestAudit(TvMazeRequestType.GET_ALL_SHOWS);
        var actualAuditEntity = auditEntityCaptor.getValue();
        assertThat(actualAuditEntity).usingRecursiveComparison().ignoringFields("timestamp").isEqualTo(expectedAuditEntity);
        assertThat(actualAuditEntity.getTimestamp()).isNotNull();
    }

    @Test
    void shouldAuditShowByName() {
        tvMazeRequestAuditor.auditShowByName();

        var auditEntityCaptor = ArgumentCaptor.forClass(TvMazeRequestAudit.class);
        verify(requestAuditRepositoryMock).save(auditEntityCaptor.capture());
        verifyNoMoreInteractions(requestAuditRepositoryMock);

        var expectedAuditEntity = new TvMazeRequestAudit(TvMazeRequestType.GET_SHOW_BY_NAME);
        var actualAuditEntity = auditEntityCaptor.getValue();
        assertThat(actualAuditEntity).usingRecursiveComparison().ignoringFields("timestamp").isEqualTo(expectedAuditEntity);
        assertThat(actualAuditEntity.getTimestamp()).isNotNull();
    }

    @Test
    void shouldAuditShowById() {
        tvMazeRequestAuditor.auditShowById();

        var auditEntityCaptor = ArgumentCaptor.forClass(TvMazeRequestAudit.class);
        verify(requestAuditRepositoryMock).save(auditEntityCaptor.capture());
        verifyNoMoreInteractions(requestAuditRepositoryMock);

        var expectedAuditEntity = new TvMazeRequestAudit(TvMazeRequestType.GET_SHOW_BY_ID);
        var actualAuditEntity = auditEntityCaptor.getValue();
        assertThat(actualAuditEntity).usingRecursiveComparison().ignoringFields("timestamp").isEqualTo(expectedAuditEntity);
        assertThat(actualAuditEntity.getTimestamp()).isNotNull();
    }

    @Test
    void shouldValidateRequestOverflow_andDontThrowException_ifNumberOfRequestsAreUnderLimit() {
        var rateLimitMock = mock(ApiApplicationProperties.TvMazeProperties.TvMazeRateLimitProperties.class);
        when(rateLimitMock.getMaxDaily()).thenReturn(2);
        when(rateLimitMock.getMaxPerInterval()).thenReturn(3);
        when(rateLimitMock.getTimeInterval()).thenReturn(10);
        var tvMazePropertiesMock = mock(ApiApplicationProperties.TvMazeProperties.class);
        when(applicationPropertiesMock.getTvMaze()).thenReturn(tvMazePropertiesMock);
        when(tvMazePropertiesMock.getRateLimit()).thenReturn(rateLimitMock);

        var midnightToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        when(requestAuditRepositoryMock.countByTimestampAfter(midnightToday)).thenReturn(1L);

        var nowMinusTimeInterval = LocalDateTime.now().minusSeconds(10).withNano(0);
        when(requestAuditRepositoryMock.countByTimestampAfter(nowMinusTimeInterval)).thenReturn(2L);

        tvMazeRequestAuditor.validateRequestOverflow();

        verifyNoMoreInteractions(applicationPropertiesMock);
        verifyNoMoreInteractions(requestAuditRepositoryMock);
    }

    @Test
    void shouldValidateRequestOverflow_andThrowException_ifNumberOfRequestsInADay_isOverLimit() {
        var rateLimitMock = mock(ApiApplicationProperties.TvMazeProperties.TvMazeRateLimitProperties.class);
        when(rateLimitMock.getMaxDaily()).thenReturn(2);
        var tvMazePropertiesMock = mock(ApiApplicationProperties.TvMazeProperties.class);
        when(applicationPropertiesMock.getTvMaze()).thenReturn(tvMazePropertiesMock);
        when(tvMazePropertiesMock.getRateLimit()).thenReturn(rateLimitMock);

        var midnightToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        when(requestAuditRepositoryMock.countByTimestampAfter(midnightToday)).thenReturn(2L);

        assertThatThrownBy(() -> tvMazeRequestAuditor.validateRequestOverflow())
            .isInstanceOf(TvMazeIntegrationException.class)
            .hasMessage("Number of requests executed starting from midnight exceeds daily request limit of: 2! Please wait until tomorrow!");

        verifyNoMoreInteractions(applicationPropertiesMock);
        verifyNoMoreInteractions(requestAuditRepositoryMock);
    }

    @Test
    void shouldValidateRequestOverflow_andThrowException_ifNumberOfRequestsInADefinedInterval_isOverLimit() {
        var rateLimitMock = mock(ApiApplicationProperties.TvMazeProperties.TvMazeRateLimitProperties.class);
        when(rateLimitMock.getMaxDaily()).thenReturn(2);
        when(rateLimitMock.getMaxPerInterval()).thenReturn(3);
        when(rateLimitMock.getTimeInterval()).thenReturn(10);
        var tvMazePropertiesMock = mock(ApiApplicationProperties.TvMazeProperties.class);
        when(applicationPropertiesMock.getTvMaze()).thenReturn(tvMazePropertiesMock);
        when(tvMazePropertiesMock.getRateLimit()).thenReturn(rateLimitMock);

        var midnightToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        when(requestAuditRepositoryMock.countByTimestampAfter(midnightToday)).thenReturn(1L);

        var nowMinusTimeInterval = LocalDateTime.now().minusSeconds(10).withNano(0);
        when(requestAuditRepositoryMock.countByTimestampAfter(nowMinusTimeInterval)).thenReturn(3L);

        assertThatThrownBy(() -> tvMazeRequestAuditor.validateRequestOverflow())
            .isInstanceOf(TvMazeIntegrationException.class)
            .hasMessage(
                "Number of requests executed starting in last: 10 seconds exceeds request limit of: 3 requests! Please wait for next: 10 seconds!");

        verifyNoMoreInteractions(applicationPropertiesMock);
        verifyNoMoreInteractions(requestAuditRepositoryMock);
    }

}
