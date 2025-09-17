package com.pet.pethubapi.interfaces.auth;

import com.pet.pethubapi.PetIntegrationTest;
import com.pet.pethubsecurity.auth.AuthenticationService;
import com.pet.pethubsecurity.auth.InvalidAuthenticationException;
import com.pet.pethubsecurity.auth.LoginDTO;
import com.pet.pethubsecurity.auth.RegisterDTO;
import com.pet.pethubsecurity.jwt.JWTResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// TODO: migrate to security library
@PetIntegrationTest
class AuthenticationControllerShould {

    @LocalServerPort
    private int port;

    @MockBean
    private AuthenticationService authenticationServiceMock;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost/api/auth";
        RestAssured.port = port;
    }

    @Test
    void returnCreated_whenRegisterNewUser() {
        doNothing().when(authenticationServiceMock).registerNewUser(any());

        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("password");
        input.setFirstName("Test");
        input.setLastName("Testic");

        var response = given()
            .basePath("/register")
            .body(input).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getBody().asString()).isEmpty();
        verify(authenticationServiceMock).registerNewUser(input);
        verifyNoMoreInteractions(authenticationServiceMock);
    }

    @Test
    void returnJwt_whenSuccessfulAuthentication() {
        var input = new LoginDTO("email", "password");
        var mockResponse = new JWTResponse("accessToken", "refreshToken");

        when(authenticationServiceMock.authenticateUser(input)).thenReturn(mockResponse);

        var response = given()
            .basePath("/login")
            .body(input).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        var responseBody = response.as(JWTResponse.class);
        assertThat(responseBody).isEqualTo(mockResponse);

        verify(authenticationServiceMock).authenticateUser(input);
        verifyNoMoreInteractions(authenticationServiceMock);
    }

    @Test
    void returnBadRequest_whenAuthenticationFails() {
        var input = new LoginDTO("email", "password");

        when(authenticationServiceMock.authenticateUser(input)).thenThrow(new InvalidAuthenticationException("test msg"));

        var response = given()
            .basePath("/login")
            .body(input).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        verify(authenticationServiceMock).authenticateUser(input);
        verifyNoMoreInteractions(authenticationServiceMock);
    }

    @Test
    void returnJwt_whenRefreshingToken() {
        var input = "refreshToken";
        var mockResponse = new JWTResponse("accessToken", "refreshToken");

        when(authenticationServiceMock.refreshAuthToken(input)).thenReturn(mockResponse);

        var response = given()
            .basePath("/refresh-token")
            .header("X-Auth-Refresh", input)
            .when().get()
            .then().extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        var responseBody = response.as(JWTResponse.class);
        assertThat(responseBody).isEqualTo(mockResponse);

        verify(authenticationServiceMock).refreshAuthToken(input);
        verifyNoMoreInteractions(authenticationServiceMock);
    }

}
