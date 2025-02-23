package com.pet.pethubapi.infrastructure.jsonplaceholder;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.pet.pethubapi.PetIntegrationTest;
import com.pet.pethubapi.domain.jsonplaceholder.JsonPlaceholderTodoDTO;
import com.pet.pethubapi.domain.jsonplaceholder.JsonPlaceholderUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.forbidden;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// TODO: failOnUnmatchedRequest(true) to disable HttpClientErrorException$NotFound 404 (which can be handled by application)

/**
 * WireMock has strict stubbing enabled dy default, so no need for verifying if request has been made.
 * It would only make sense if method under test has external API request, which is void (no need for stubbing).
 */
@PetIntegrationTest
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "pet.json-placeholder.base-url"))
class JsonPlaceholderClientShould {

    @InjectWireMock
    private WireMockServer mockServer;

    @Autowired
    private JsonPlaceholderClient jsonPlaceholderClient;

    @Test
    void findAllUsers() {
        var mockedResponseBody = List.of(JsonPlaceholderUserDTO.builder().id(1).name("name").website("website").email("email").build());

        mockServer.stubFor(get("/users")
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(jsonResponse(mockedResponseBody, HttpStatus.OK.value())));

        var result = jsonPlaceholderClient.findAllUsers();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockedResponseBody);
    }

    @Test
    void throwException_whenConnectionIsReset() {
        mockServer.stubFor(get("/users")
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> jsonPlaceholderClient.findAllUsers())
            .isInstanceOf(ResourceAccessException.class)
            .hasMessageContaining("Connection reset");
    }

    @Test
    void throwException_whenClientFailsToRespond() {
        mockServer.stubFor(get("/users")
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

        assertThatThrownBy(() -> jsonPlaceholderClient.findAllUsers())
            .isInstanceOf(ResourceAccessException.class)
            .hasMessageContaining("failed to respond");
    }

    @Test
    void throwException_whenClientIsUnavailable() {
        mockServer.stubFor(get("/users")
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(serviceUnavailable()));

        assertThatThrownBy(() -> jsonPlaceholderClient.findAllUsers())
            .isInstanceOf(HttpServerErrorException.ServiceUnavailable.class);
    }

    @Test
    void throwException_whenClientRespondsWithForbidden() {
        mockServer.stubFor(get("/users")
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(forbidden()));

        assertThatThrownBy(() -> jsonPlaceholderClient.findAllUsers())
            .isInstanceOf(HttpClientErrorException.Forbidden.class);
    }

    @Test
    void findUserById() {
        var mockedResponseBody = JsonPlaceholderUserDTO.builder().id(1).name("name").website("website").email("email").build();

        mockServer.stubFor(get(urlPathTemplate("/users/{id}"))
            .withPathParam("id", equalTo("1"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(jsonResponse(mockedResponseBody, HttpStatus.OK.value())));

        var result = jsonPlaceholderClient.findUserById(1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockedResponseBody);
    }

    @Test
    void createUser() {
        var mockedResponseBody = JsonPlaceholderUserDTO.builder().id(1).name("name").website("website").email("email").build();

        mockServer.stubFor(post("/users")
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(jsonResponse(mockedResponseBody, HttpStatus.CREATED.value())));

        var requestBody = JsonPlaceholderUserDTO.builder().name("name").website("website").email("email").build();
        var result = jsonPlaceholderClient.createUser(requestBody);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(mockedResponseBody);
    }

    @Test
    void updateUser() {
        var mockedResponseBody = JsonPlaceholderUserDTO.builder().id(1).name("updated name").website("website").email("email").build();

        mockServer.stubFor(put(urlPathTemplate("/users/{id}"))
            .withPathParam("id", equalTo("1"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(jsonResponse(mockedResponseBody, HttpStatus.OK.value())));

        var requestBody = JsonPlaceholderUserDTO.builder().id(1).name("name").website("website").email("email").build();
        var result = jsonPlaceholderClient.updateUser(requestBody.getId(), requestBody);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockedResponseBody);
    }

    @Test
    void patchUser() {
        var mockedResponseBody = JsonPlaceholderUserDTO.builder().id(1).name("updated name").website("website").email("email").build();

        mockServer.stubFor(patch(urlPathTemplate("/users/{id}"))
            .withPathParam("id", equalTo("1"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(jsonResponse(mockedResponseBody, HttpStatus.OK.value())));

        var requestBody = JsonPlaceholderUserDTO.builder().name("updated name").build();
        var result = jsonPlaceholderClient.patchUser(1, requestBody);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockedResponseBody);
    }

    @Test
    void deleteUserByIdById() {
        mockServer.stubFor(delete(urlPathTemplate("/users/{id}"))
            .withPathParam("id", equalTo("1"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(noContent()));

        var result = jsonPlaceholderClient.deleteUserById(1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
    }

    @Test
    void findAllTodosByUserId() {
        var mockedResponseBody = List.of(JsonPlaceholderTodoDTO.builder().id(1).userId(99).title("title").isCompleted(true).build());

        mockServer.stubFor(get(urlPathEqualTo("/todos"))
            .withQueryParam("userId", equalTo("99"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(jsonResponse(mockedResponseBody, HttpStatus.OK.value())));

        var result = jsonPlaceholderClient.findAllTodosByUserId(99);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockedResponseBody);
    }

}
