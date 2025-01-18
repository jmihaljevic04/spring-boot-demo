package com.pet.pethubapi.interfaces.tvshow;

import com.pet.pethubapi.PetIntegrationTest;
import com.pet.pethubapi.application.tvshow.InvalidShowDetailsActionException;
import com.pet.pethubapi.application.tvshow.TvShowDetailsService;
import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import com.pet.pethubapi.interfaces.AuthenticatedUserUtils;
import com.pet.pethubsecurity.jwt.JwtService;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.pet.pethubapi.application.ObjectMapperUtils.OBJECT_MAPPER;
import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Slf4j
@PetIntegrationTest
class TvShowDetailsControllerShould {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private TvShowDetailsService tvShowDetailsServiceMock;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost/api/tv-shows";
        RestAssured.port = port;
    }

    @Test
    void shouldGetAllTvShows_withAdmin_andWithoutPaging() throws Exception {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setId(1L);
        when(tvShowDetailsServiceMock.getAllShows(any())).thenReturn(List.of(mockedTvShow));

        var response = given()
            .basePath("/")
            .header(AuthenticatedUserUtils.generateAdminJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        var responseBody = asList(OBJECT_MAPPER.readValue(response.body().asString(), TvShowDTO[].class));
        assertThat(responseBody).isEqualTo(List.of(mockedTvShow));

        verify(tvShowDetailsServiceMock).getAllShows(Pageable.ofSize(10));
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldGetAllTvShows_withAdmin_andPaging() throws Exception {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setId(1L);
        when(tvShowDetailsServiceMock.getAllShows(any())).thenReturn(List.of(mockedTvShow));

        var response = given()
            .basePath("/")
            .queryParam("page", 2)
            .queryParam("size", 22)
            .header(AuthenticatedUserUtils.generateAdminJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        var responseBody = asList(OBJECT_MAPPER.readValue(response.body().asString(), TvShowDTO[].class));
        assertThat(responseBody).isEqualTo(List.of(mockedTvShow));

        verify(tvShowDetailsServiceMock).getAllShows(PageRequest.of(2, 22));
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldReturnEmptyResponse_whenGetAllTvShows_withAdmin_andZeroShows() {
        when(tvShowDetailsServiceMock.getAllShows(any())).thenReturn(List.of());

        var response = given()
            .basePath("/")
            .header(AuthenticatedUserUtils.generateAdminJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        verify(tvShowDetailsServiceMock).getAllShows(Pageable.ofSize(10));
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldNotGetAllTvShows_andReturnForbidden_andUserRole() {
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setId(1L);
        when(tvShowDetailsServiceMock.getAllShows(any())).thenReturn(List.of(mockedTvShow));

        var response = given()
            .basePath("/")
            .header(AuthenticatedUserUtils.generateUserJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        verifyNoInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldGetTvShowDetailsByName() throws Exception {
        var showName = "The Sopranos";
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setName(showName);
        var mockedResponse = new TvShowSearchResponse();
        mockedResponse.setShow(mockedTvShow);
        mockedResponse.setScore(0.99);

        when(tvShowDetailsServiceMock.getShowDetailsByName(showName)).thenReturn(List.of(mockedResponse));

        var response = given()
            .basePath("/details")
            .queryParam("showName", showName)
            .header(AuthenticatedUserUtils.generateUserJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        var responseBody = asList(OBJECT_MAPPER.readValue(response.body().asString(), TvShowSearchResponse[].class));
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.getFirst().getScore()).isEqualTo(mockedResponse.getScore());
        assertThat(responseBody.getFirst().getShow()).isEqualTo(mockedResponse.getShow());
        assertThat(responseBody.getFirst().getLinks()).isNotEmpty();
        assertThat(responseBody.getFirst().getLinks().getLink("self")).isNotEmpty();
        assertThat(responseBody.getFirst().getLinks().getLink("self").get().getHref()).contains("/api/tv-shows/details/{id}");

        verify(tvShowDetailsServiceMock).getShowDetailsByName(showName);
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldReturnEmptyResponse_whenGetTvShowDetailsByName_withNonExistentShow() {
        var showName = "non existent";

        when(tvShowDetailsServiceMock.getShowDetailsByName(showName)).thenReturn(List.of());

        var response = given()
            .basePath("/details")
            .queryParam("showName", showName)
            .header(AuthenticatedUserUtils.generateUserJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        verify(tvShowDetailsServiceMock).getShowDetailsByName(showName);
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldGetTvShowDetailsById() throws Exception {
        final var showId = 1L;
        var mockedTvShow = new TvShowDTO();
        mockedTvShow.setId(showId);

        when(tvShowDetailsServiceMock.getShowDetailsById(showId)).thenReturn(mockedTvShow);

        var response = given()
            .basePath("/details/{id}")
            .pathParam("id", showId)
            .header(AuthenticatedUserUtils.generateUserJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        var responseBody = OBJECT_MAPPER.readValue(response.body().asString(), TvShowDTO.class);
        assertThat(responseBody).isEqualTo(mockedTvShow);

        verify(tvShowDetailsServiceMock).getShowDetailsById(showId);
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

    @Test
    void shouldReturnError_whenGetTvShowDetailsById_withMissingShow() {
        final var showId = 0L;

        doThrow(new InvalidShowDetailsActionException("test msg")).when(tvShowDetailsServiceMock).getShowDetailsById(showId);

        var response = given()
            .basePath("/details/{id}")
            .pathParam("id", showId)
            .header(AuthenticatedUserUtils.generateUserJwt(jwtService))
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        verify(tvShowDetailsServiceMock).getShowDetailsById(showId);
        verifyNoMoreInteractions(tvShowDetailsServiceMock);
    }

}
