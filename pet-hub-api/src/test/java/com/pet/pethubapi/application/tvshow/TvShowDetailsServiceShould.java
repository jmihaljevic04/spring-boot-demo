package com.pet.pethubapi.application.tvshow;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.pet.pethubapi.application.tvshow.TvShowDetailsServiceImpl.SHOW_MATCHED_SCORE_MIN_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TvShowDetailsServiceShould {

    @Mock
    private TvShowDetailsRepository tvShowDetailsRepositoryMock;

    @InjectMocks
    private TvShowDetailsServiceImpl tvShowDetailsService;

    @Test
    void shouldGetAllShows() {
        var pageable = PageRequest.of(0, 10);

        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setName("Sopranos");

        when(tvShowDetailsRepositoryMock.getAllShows(pageable.getPageNumber())).thenReturn(List.of(mockedTvShow));

        var result = tvShowDetailsService.getAllShows(pageable);

        assertThat(result).isEqualTo(List.of(mockedTvShow));
        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldGetShowDetailsByName() {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setName("The Sopranos");
        var mockedResponse = new TvShowSearchResponse();
        mockedResponse.setShow(mockedTvShow);
        mockedResponse.setScore(0.9);

        when(tvShowDetailsRepositoryMock.getShowDetailsByName("Sopranos")).thenReturn(List.of(mockedResponse));

        var result = tvShowDetailsService.getShowDetailsByName("Sopranos");

        assertThat(result).isEqualTo(List.of(mockedResponse));
        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldGetShowDetailsByName_butOnlyWithHighEnoughScore() {
        var mockedTvShowHit = new TvShowDTO();
        mockedTvShowHit.setName("The Sopranos");
        var mockedTvShowMiss = new TvShowDTO();
        mockedTvShowMiss.setName("Sex And City");
        var mockedHitResponse = new TvShowSearchResponse();
        mockedHitResponse.setShow(mockedTvShowHit);
        mockedHitResponse.setScore(0.99);
        var mockedMissResponse = new TvShowSearchResponse();
        mockedMissResponse.setShow(mockedTvShowHit);
        mockedMissResponse.setScore(SHOW_MATCHED_SCORE_MIN_THRESHOLD - 0.1);

        when(tvShowDetailsRepositoryMock.getShowDetailsByName("Sopranos")).thenReturn(List.of(mockedHitResponse, mockedMissResponse));

        var result = tvShowDetailsService.getShowDetailsByName("Sopranos");

        assertThat(result).isEqualTo(List.of(mockedHitResponse));
        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  Sopranos  ", "\nSopra\rnos\t"})
    void shouldGetShowDetailsByName_withNotNormalizedName(String inputShowName) {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setName("The Sopranos");
        var mockedResponse = new TvShowSearchResponse();
        mockedResponse.setShow(mockedTvShow);
        mockedResponse.setScore(0.9);

        when(tvShowDetailsRepositoryMock.getShowDetailsByName("Sopranos")).thenReturn(List.of(mockedResponse));

        var result = tvShowDetailsService.getShowDetailsByName(inputShowName);

        assertThat(result).isEqualTo(List.of(mockedResponse));
        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsByName_withEmptyName() {
        assertThatThrownBy(() -> tvShowDetailsService.getShowDetailsByName("  "))
            .isInstanceOf(InvalidShowDetailsActionException.class)
            .hasMessage("TV Show name is empty!");

        verifyNoInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsByName_withMissingName() {
        assertThatThrownBy(() -> tvShowDetailsService.getShowDetailsByName(null))
            .isInstanceOf(InvalidShowDetailsActionException.class)
            .hasMessage("TV Show name is empty!");

        verifyNoInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsByName_withTooLowScore() {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setName("The Sopranos");
        var mockedResponse = new TvShowSearchResponse();
        mockedResponse.setShow(mockedTvShow);
        mockedResponse.setScore(SHOW_MATCHED_SCORE_MIN_THRESHOLD - 0.01);

        when(tvShowDetailsRepositoryMock.getShowDetailsByName("Sopr")).thenReturn(List.of(mockedResponse));

        assertThatThrownBy(() -> tvShowDetailsService.getShowDetailsByName("Sopr"))
            .isInstanceOf(InvalidShowDetailsActionException.class)
            .hasMessage("TV Show with name: 'Sopr' not found!");

        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldGetShowDetailsById() {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setId(1L);

        when(tvShowDetailsRepositoryMock.getShowDetailsById(1L)).thenReturn(Optional.of(mockedTvShow));

        var result = tvShowDetailsService.getShowDetailsById(1L);

        assertThat(result).isEqualTo(mockedTvShow);
        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsById_withMissingId() {
        assertThatThrownBy(() -> tvShowDetailsService.getShowDetailsById(null))
            .isInstanceOf(InvalidShowDetailsActionException.class)
            .hasMessage("TV Show ID is invalid!");

        verifyNoInteractions(tvShowDetailsRepositoryMock);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -10, -100})
    void shouldThrowException_whenGetShowDetailsById_withIdBelowOne(long inputShowId) {
        assertThatThrownBy(() -> tvShowDetailsService.getShowDetailsById(inputShowId))
            .isInstanceOf(InvalidShowDetailsActionException.class)
            .hasMessage("TV Show ID is invalid!");

        verifyNoInteractions(tvShowDetailsRepositoryMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsById_withNonExistingId() {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setId(1L);

        when(tvShowDetailsRepositoryMock.getShowDetailsById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tvShowDetailsService.getShowDetailsById(999L))
            .isInstanceOf(InvalidShowDetailsActionException.class)
            .hasMessage("TV Show with ID: 999 not found!");

        verifyNoMoreInteractions(tvShowDetailsRepositoryMock);
    }

}
