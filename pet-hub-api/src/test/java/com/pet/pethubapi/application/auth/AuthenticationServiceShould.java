package com.pet.pethubapi.application.auth;

import com.pet.pethubapi.PetIntegrationTest;
import com.pet.pethubapi.RestoreDatabaseCallback;
import com.pet.pethubsecurity.JWTResponse;
import com.pet.pethubsecurity.domain.role.Role;
import com.pet.pethubsecurity.domain.role.RoleEnum;
import com.pet.pethubsecurity.domain.user.UserRepository;
import com.pet.pethubsecurity.jwt.JwtService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@PetIntegrationTest
@ExtendWith(RestoreDatabaseCallback.class)
class AuthenticationServiceShould {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtServiceMock;

    @Test
    void registerNewUser() {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("Password1234.");
        input.setFirstName("First name");
        input.setLastName("Last name");

        authenticationService.registerNewUser(input);

        final var newUser = userRepository.findByEmail("test@test.com");
        assertThat(newUser).isPresent();
        assertThat(newUser.get().getPassword()).startsWith("{bcrypt}");
        assertThat(newUser.get().getFirstName()).isEqualTo("First name");
        assertThat(newUser.get().getLastName()).isEqualTo("Last name");
        assertThat(newUser.get().getCreatedBy()).isEqualTo("NEW_USER");
        assertThat(newUser.get().getRoles()).hasSize(1);
        assertThat(newUser.get().getRoles().iterator().next().getId()).isEqualTo(RoleEnum.USER.getId());
    }

    @Test
    void throwException_whenRegisterNewUser_andInputIsEmpty() {
        assertThatThrownBy(() -> authenticationService.registerNewUser(null))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("Input for new user registration is empty!");
    }

    @Test
    void throwException_whenRegisterNewUser_andEmailIsEmpty() {
        var input = new RegisterDTO();
        input.setPassword("Password1234.");
        input.setFirstName("First name");
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("One of the inputs for new user registration is empty!");
    }

    @Test
    void throwException_whenRegisterNewUser_andPasswordIsEmpty() {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("");
        input.setFirstName("First name");
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("One of the inputs for new user registration is empty!");
    }

    @Test
    void throwException_whenRegisterNewUser_andFirstNameIsEmpty() {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("Password1234.");
        input.setFirstName("     ");
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("One of the inputs for new user registration is empty!");
    }

    @Test
    void throwException_whenRegisterNewUser_andLastNameIsEmpty() {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("Password1234.");
        input.setFirstName("First name");
        input.setLastName(null);

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("One of the inputs for new user registration is empty!");
    }

    @Test
    void throwException_whenRegisterNewUser_andFirstNameIsTooLong() {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("Password1234.");
        input.setFirstName(RandomStringUtils.random(256));
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("First or last name should be shorter than 256 characters!");
    }

    @Test
    void throwException_whenRegisterNewUser_andLastNameIsTooLong() {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword("Password1234.");
        input.setFirstName("First name");
        input.setLastName(RandomStringUtils.random(256));

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("First or last name should be shorter than 256 characters!");
    }

    @ParameterizedTest
    @ValueSource(strings = {"@test.com", "test@test", "test.com", "test@test.c", "test@test.commm", "test@test.GOV", "a@a.a"})
    void throwException_whenRegisterNewUser_andEmailIsInvalid(String email) {
        var input = new RegisterDTO();
        input.setEmail(email);
        input.setPassword("Password1234.");
        input.setFirstName("First name");
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("Invalid email address format!");
    }

    @Test
    void throwException_whenRegisterNewUser_andUserAlreadyExists() {
        var input = new RegisterDTO();
        input.setEmail("admin@pethub.com");
        input.setPassword("Password1234.");
        input.setFirstName("First name");
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("User with given email already exists!");
    }

    @ParameterizedTest
    @ValueSource(strings = {"password", "Password", "Password123", "ThisIsTooLongPasswordToHandle1234.!", "ALLUPPERCASE1.", "AlmostCorrect."})
    void throwException_whenRegisterNewUser_andPasswordIsInvalid(String password) {
        var input = new RegisterDTO();
        input.setEmail("test@test.com");
        input.setPassword(password);
        input.setFirstName("First name");
        input.setLastName("Last name");

        assertThatThrownBy(() -> authenticationService.registerNewUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(
                "Invalid password format! Password must contain at least 8 characters and at most 20 characters, with at least one uppercase and one lowercase letter and at least one digit and special character!");
    }

    @Test
    void authenticateUser_andReturnTokens() {
        var registrationInput = new RegisterDTO();
        registrationInput.setEmail("test2@test.com");
        registrationInput.setPassword("Password1234.");
        registrationInput.setFirstName("First name");
        registrationInput.setLastName("Last name");
        authenticationService.registerNewUser(registrationInput);
        var loginInput = new LoginDTO(registrationInput.getEmail(), registrationInput.getPassword());

        var mockJwtResponse = new JWTResponse("accessToken", "refreshToken");
        when(jwtServiceMock.generateTokens(eq(loginInput.email()), anySet())).thenReturn(mockJwtResponse);

        var response = authenticationService.authenticateUser(loginInput);

        assertThat(response).isEqualTo(mockJwtResponse);

        ArgumentCaptor<Set<Role>> roleCaptor = ArgumentCaptor.forClass((Class) Set.class);
        verify(jwtServiceMock).generateTokens(eq(loginInput.email()), roleCaptor.capture());
        assertThat(roleCaptor.getValue()).hasSize(1);
        assertThat(roleCaptor.getValue().iterator().next().getId()).isEqualTo(RoleEnum.USER.getId()); // because new user is registered as USER by default
        verifyNoMoreInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenAuthenticateUser_andInputIsEmpty() {
        assertThatThrownBy(() -> authenticationService.authenticateUser(null))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("Input for user login is empty!");

        verifyNoInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenAuthenticateUser_andEmailIsEmpty() {
        var input = new LoginDTO("", "password");

        assertThatThrownBy(() -> authenticationService.authenticateUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("One of the inputs for user login is empty!");

        verifyNoInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenAuthenticateUser_andPasswordIsEmpty() {
        var input = new LoginDTO("email", null);

        assertThatThrownBy(() -> authenticationService.authenticateUser(input))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("One of the inputs for user login is empty!");

        verifyNoInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenAuthenticateUser_andUserWithEmailDoesNotExist() {
        var input = new LoginDTO("email@email.com", "password");

        assertThatThrownBy(() -> authenticationService.authenticateUser(input))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("Invalid email or password!");

        verifyNoInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenAuthenticateUser_withIncorrectPassword() {
        var input = new LoginDTO("admin@pethub.com", "password");

        assertThatThrownBy(() -> authenticationService.authenticateUser(input))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("Invalid email or password!");

        verifyNoInteractions(jwtServiceMock);
    }

    @Test
    void refreshAuthToken_andReturnTokens() {
        var input = "refreshToken";

        when(jwtServiceMock.hasTokenExpired(input)).thenReturn(false);
        when(jwtServiceMock.isRefreshToken(input)).thenReturn(true);
        var mockUser = "user@pethub.com";
        when(jwtServiceMock.getUsernameFromToken(input)).thenReturn(mockUser);
        var mockJwtResponse = new JWTResponse("accessToken", "refreshToken2");
        when(jwtServiceMock.generateTokens(eq(mockUser), anySet())).thenReturn(mockJwtResponse);

        var response = authenticationService.refreshAuthToken(input);

        assertThat(response).isEqualTo(mockJwtResponse);

        verify(jwtServiceMock).hasTokenExpired(input);
        verify(jwtServiceMock).isRefreshToken(input);
        verify(jwtServiceMock).getUsernameFromToken(input);
        ArgumentCaptor<Set<Role>> roleCaptor = ArgumentCaptor.forClass((Class) Set.class);
        verify(jwtServiceMock).generateTokens(eq(mockUser), roleCaptor.capture());
        assertThat(roleCaptor.getValue()).hasSize(1);
        assertThat(roleCaptor.getValue().iterator().next().getId()).isEqualTo(RoleEnum.USER.getId());
        verifyNoMoreInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenRefreshAuthToken_whenRefreshTokenIsEmpty() {
        assertThatThrownBy(() -> authenticationService.refreshAuthToken(null))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage("Refresh token is empty!");

        verifyNoInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenRefreshAuthToken_whenRefreshTokenHasExpired() {
        var input = "refreshToken";

        when(jwtServiceMock.hasTokenExpired(input)).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.refreshAuthToken(input))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("Refresh token has expired or is invalid!");

        verify(jwtServiceMock).hasTokenExpired(input);
        verifyNoMoreInteractions(jwtServiceMock);
    }

    @Test
    void throwException_whenRefreshAuthToken_whenRefreshTokenIsNotRefreshToken() {
        var input = "refreshToken";

        when(jwtServiceMock.hasTokenExpired(input)).thenReturn(false);
        when(jwtServiceMock.isRefreshToken(input)).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.refreshAuthToken(input))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("Refresh token has expired or is invalid!");

        verify(jwtServiceMock).hasTokenExpired(input);
        verify(jwtServiceMock).isRefreshToken(input);
        verifyNoMoreInteractions(jwtServiceMock);
    }

}
