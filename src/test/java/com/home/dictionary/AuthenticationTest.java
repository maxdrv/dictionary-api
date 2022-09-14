package com.home.dictionary;

import com.home.dictionary.model.user.Authority;
import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.openapi.model.AuthenticationResponse;
import com.home.dictionary.openapi.model.LoginRequest;
import com.home.dictionary.openapi.model.RegisterRequest;
import com.home.dictionary.repository.ApiUserRepository;
import com.home.dictionary.util.WithDataBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthenticationTest extends WithDataBase {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@gmail.com";

    @Autowired
    ApiUserRepository apiUserRepository;

    @Test
    public void userRegistrationIsOk() {
        var registerRequest = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL);

        caller.register(registerRequest)
                .andExpect(status().isOk())
                .andExpect(content().json("{'message': 'User Registration Successful'}"));

        var user = apiUserRepository.findByUsernameOrThrow(USERNAME);

        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPassword()).isNotBlank();
        assertThat(user.isEnabled()).isFalse();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getAuthorities().stream().map(Authority::getType)).containsExactly(AuthorityType.USER);
    }

    @Test
    public void userLoginIsOk() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var response = caller.login(loginRequest)
                .andExpect(status().isOk())
                .andReturnAs(AuthenticationResponse.class);

        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getExpiresAt()).isNotNull();
        assertThat(response.getAuthenticationToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();

        System.out.println(response.getAuthenticationToken());
        System.out.println(response.getRefreshToken());
    }

    @Test
    public void userLoginWrongPassword() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password("wrong_password");
        caller.login(loginRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("bad credentials"));
    }

    @Test
    public void userLoginWrongUsername() {
        registerUserAndEnable();
        var loginRequest = new LoginRequest().username("wrong_username").password("wrong_password");
        caller.login(loginRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("bad credentials"));
    }

    @Test
    public void requestAuthenticated() {
        registerUserAndEnable();
        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var response = caller.login(loginRequest)
                .andExpect(status().isOk())
                .andReturnAs(AuthenticationResponse.class);

        var bearerToken = response.getAuthenticationToken();


    }

    public void registerUserAndEnable() {
        var registerRequest = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL);

        caller.register(registerRequest)
                .andExpect(status().isOk());

        var user = apiUserRepository.findByUsernameOrThrow(USERNAME);
        user.enable();
        apiUserRepository.save(user);
    }

}
