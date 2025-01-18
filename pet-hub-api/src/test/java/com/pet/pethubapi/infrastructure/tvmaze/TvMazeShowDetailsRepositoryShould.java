package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.PetIntegrationTest;
import com.pet.pethubapi.application.ObjectMapperUtils;
import com.pet.pethubapi.domain.ApiApplicationProperties;
import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import com.pet.pethubapi.infrastructure.tvmaze.audit.TvMazeRequestAuditor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.pet.pethubapi.infrastructure.tvmaze.TvMazeShowDetailsRepositoryImpl.SHOW_DETAILS_BY_NAME_CACHE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withRawStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@PetIntegrationTest
class TvMazeShowDetailsRepositoryShould {

    @MockBean
    private TvMazeRequestAuditor tvMazeRequestAuditorMock;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private RestClient.Builder defaultTvMazeRestClient;
    @Autowired
    private ApiApplicationProperties applicationProperties;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private TvShowDetailsRepository showDetailsRepository;

    private MockRestServiceServer mockServer;

    @BeforeAll
    void setup() {
        // done so we can replace actual implementation of RestClients with mocked ones (using real configuration - builder)
        // used setter injection instead of constructor creation, so we don't avoid proxies for Cacheable method
        mockServer = MockRestServiceServer.bindTo(defaultTvMazeRestClient).build();
        var showIndexRestClient = defaultTvMazeRestClient.baseUrl(applicationProperties.getTvMaze().getShowIndex().getBaseUrl()).build();
        var searchRestClient = defaultTvMazeRestClient.baseUrl(applicationProperties.getTvMaze().getSearch().getBaseUrl()).build();
        if (showDetailsRepository instanceof TvMazeShowDetailsRepositoryImpl impl) {
            // always true
            impl.setTvMazeShowIndexRestClient(showIndexRestClient);
            impl.setTvMazeSearchRestClient(searchRestClient);
        }
    }

    @AfterEach
    void clearTestInvocations() {
        mockServer.reset();
        Optional.ofNullable(cacheManager.getCache(SHOW_DETAILS_BY_NAME_CACHE)).ifPresent(Cache::clear);
    }

    @Test
    void shouldGetAllShows() {
        var mockedResponseBody = new TvShowDTO();
        mockedResponseBody.setId(1L);
        mockedResponseBody.setName("show name");

        mockServer.expect(requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "?page=2"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess(ObjectMapperUtils.toJsonString(List.of(mockedResponseBody)), MediaType.APPLICATION_JSON));

        var result = showDetailsRepository.getAllShows(2);

        mockServer.verify();
        assertThat(result).isEqualTo(List.of(mockedResponseBody));

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditGetAllShows();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldGetAllShows_withEmptyResponse() {
        mockServer.expect(requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "?page=1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess(ObjectMapperUtils.toJsonString(List.of()), MediaType.APPLICATION_JSON));

        var result = showDetailsRepository.getAllShows(1);

        mockServer.verify();
        assertThat(result).isEqualTo(List.of());

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditGetAllShows();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldThrowException_whenGetAllShows_withClientError() {
        mockServer.expect(requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "?page=1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withException(new IOException("Client error!")));

        assertThatThrownBy(() -> showDetailsRepository.getAllShows(1))
            .isInstanceOf(TvMazeIntegrationException.class)
            .hasMessage("Unexpected error while calling TVMaze: 'I/O error on GET request for \"https://api.tvmaze.com/shows\": Client error!'!");

        mockServer.verify();

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditGetAllShows();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldThrowException_whenGetAllShows_withBadRequestFromClient() {
        var tvMazeErrorResponse = new TvMazeRestClientErrorHandler.TvMazeErrorResponse(400, "Client error!");

        mockServer.expect(requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "?page=1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withRawStatus(400).body(ObjectMapperUtils.toJsonString(tvMazeErrorResponse)));

        assertThatThrownBy(() -> showDetailsRepository.getAllShows(1))
            .isInstanceOf(TvMazeIntegrationException.class)
            .hasMessage("Error when calling TvMaze API with HTTP status code: 400 and message: 'Client error!'!");

        mockServer.verify();

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditGetAllShows();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldGetShowDetailsByName() {
        var mockedShow = new TvShowDTO();
        mockedShow.setId(1L);
        mockedShow.setName("show name");
        var mockedResponseBody = new TvShowSearchResponse();
        mockedResponseBody.setShow(mockedShow);
        mockedResponseBody.setScore(0.9);

        mockServer.expect(requestTo(applicationProperties.getTvMaze().getSearch().getBaseUrl() + "?q=Sopranos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess(ObjectMapperUtils.toJsonString(List.of(mockedResponseBody)), MediaType.APPLICATION_JSON));

        var result = showDetailsRepository.getShowDetailsByName("Sopranos");

        mockServer.verify();
        assertThat(result).isEqualTo(List.of(mockedResponseBody));

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowByName();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldGetShowDetailsByName_andNotTriggerSequentialCall_dueToCaching() {
        var mockedShow = new TvShowDTO();
        mockedShow.setId(1L);
        mockedShow.setName("show name");
        var mockedResponseBody = new TvShowSearchResponse();
        mockedResponseBody.setShow(mockedShow);
        mockedResponseBody.setScore(0.9);

        mockServer.expect(requestTo(applicationProperties.getTvMaze().getSearch().getBaseUrl() + "?q=Sopranos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess(ObjectMapperUtils.toJsonString(List.of(mockedResponseBody)), MediaType.APPLICATION_JSON));

        assertThat(cacheManager.getCache(SHOW_DETAILS_BY_NAME_CACHE).get("Sopranos")).isNull();

        var result = showDetailsRepository.getShowDetailsByName("Sopranos");
        var cachedResult = (List<TvShowSearchResponse>) cacheManager.getCache(SHOW_DETAILS_BY_NAME_CACHE).get("Sopranos").get();

        mockServer.verify();
        assertThat(result)
            .isEqualTo(List.of(mockedResponseBody))
            .isEqualTo(cachedResult);

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowByName();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldGetShowDetailsByName_withEmptyResponse() {
        mockServer.expect(requestTo(applicationProperties.getTvMaze().getSearch().getBaseUrl() + "?q=Sopranos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess(ObjectMapperUtils.toJsonString(List.of()), MediaType.APPLICATION_JSON));

        var result = showDetailsRepository.getShowDetailsByName("Sopranos");

        mockServer.verify();
        assertThat(result).isEqualTo(List.of());

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowByName();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsByName_withClientError() {
        mockServer.expect(requestTo(applicationProperties.getTvMaze().getSearch().getBaseUrl() + "?q=Sopranos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withException(new IOException("Client error!")));

        assertThatThrownBy(() -> showDetailsRepository.getShowDetailsByName("Sopranos"))
            .isInstanceOf(TvMazeIntegrationException.class)
            .hasMessage(
                "Unexpected error while calling TVMaze: 'I/O error on GET request for \"https://api.tvmaze.com/search/shows\": Client error!'!");

        mockServer.verify();

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowByName();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsByName_withBadRequestFromClient() {
        var tvMazeErrorResponse = new TvMazeRestClientErrorHandler.TvMazeErrorResponse(400, "Client error!");

        mockServer.expect(requestTo(applicationProperties.getTvMaze().getSearch().getBaseUrl() + "?q=Sopranos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withRawStatus(400).body(ObjectMapperUtils.toJsonString(tvMazeErrorResponse)));

        assertThatThrownBy(() -> showDetailsRepository.getShowDetailsByName("Sopranos"))
            .isInstanceOf(TvMazeIntegrationException.class)
            .hasMessage("Error when calling TvMaze API with HTTP status code: 400 and message: 'Client error!'!");

        mockServer.verify();

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowByName();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldGetShowDetailsById() {
        var mockedResponseBody = new TvShowDTO();
        mockedResponseBody.setId(1L);
        mockedResponseBody.setName("show name");

        mockServer.expect(requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "/1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess(ObjectMapperUtils.toJsonString(mockedResponseBody), MediaType.APPLICATION_JSON));

        var result = showDetailsRepository.getShowDetailsById(1L);

        mockServer.verify();
        assertThat(result).isPresent().get().isEqualTo(mockedResponseBody);

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowById();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldReturnEmptyResponse_whenGetShowDetailsById_withMissingShow() {
        mockServer.expect(requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "/0"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        var result = showDetailsRepository.getShowDetailsById(0L);

        mockServer.verify();
        assertThat(result).isEmpty();

        verify(tvMazeRequestAuditorMock).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock).auditShowById();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

    @Test
    void shouldThrowException_whenGetShowDetailsById_withBadRequestFromClient_exhaustingAllRetries() {
        mockServer.expect(ExpectedCount.times(4), requestTo(applicationProperties.getTvMaze().getShowIndex().getBaseUrl() + "/0"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue()))
            .andExpect(header(HttpHeaders.FROM, appName))
            .andRespond(withException(new IOException("Client error!")));

        assertThatThrownBy(() -> showDetailsRepository.getShowDetailsById(0L))
            .isInstanceOf(ResourceAccessException.class)
            .hasMessage("I/O error on GET request for \"https://api.tvmaze.com/shows/0\": Client error!");

        mockServer.verify();

        verify(tvMazeRequestAuditorMock, times(4)).validateRequestOverflow();
        verify(tvMazeRequestAuditorMock, times(4)).auditShowById();
        verifyNoMoreInteractions(tvMazeRequestAuditorMock);
    }

}
