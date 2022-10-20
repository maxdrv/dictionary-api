package com.home.dictionary;

import com.home.dictionary.model.configuration.ApiProperty;
import com.home.dictionary.model.user.Authority;
import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.repository.ApiUserRepository;
import com.home.dictionary.util.WithDataBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.home.dictionary.model.configuration.ApiPropertyKey.REGISTRATION_ALLOWED;
import static com.home.dictionary.util.Header.auth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO: исправить тесты
public class AuthenticationTest extends WithDataBase {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@gmail.com";

    @Autowired
    ApiUserRepository apiUserRepository;

    @BeforeEach
    void init() {
        apiPropertyRepository.save(new ApiProperty(REGISTRATION_ALLOWED.name(), "true"));
    }

    @Test
    void userRegistrationIsOk() {
        var registerRequest = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL);

        securedApiCaller.register(registerRequest)
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
    void userLoginIsOk() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var response = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andReturnAs(AuthenticationResponse.class);

        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getExpiresAt()).isNotNull();
//        assertThat(response.getAuthenticationToken()).isNotBlank();
//        assertThat(response.getRefreshToken()).isNotBlank();
//
//        System.out.println(response.getAuthenticationToken());
//        System.out.println(response.getRefreshToken());
    }

    @Test
    void userLoginWrongPassword() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password("wrong_password");
        securedApiCaller.login(loginRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("bad credentials"));
    }

    @Test
    void userLoginWrongUsername() {
        registerUserAndEnable();
        var loginRequest = new LoginRequest().username("wrong_username").password("wrong_password");
        securedApiCaller.login(loginRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("bad credentials"));
    }

    @Test
    void getRequestDoesNotNeedAuthentication() {
        securedApiCaller.getPageOfPhrase("?page=0&size=20")
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "size": 20,
                            "number": 0,
                            "totalElements": 0,
                            "totalPages": 0,
                            "content": []
                        }
                        """));
    }

    @Test
    void postRequest403_authHeaderMissing() {
        registerUserAndEnable();

        var createPhraseRequest = new CreatePhraseRequest()
                .source("1")
                .sourceLang(LangDto.FR)
                .transcription("1")
                .target("1")
                .targetLang(LangDto.RU);

        securedApiCaller.postPhrase(createPhraseRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    void postRequest403_authHeaderPresentButEmpty() {
        registerUserAndEnable();

        var createPhraseRequest = new CreatePhraseRequest()
                .source("1")
                .sourceLang(LangDto.FR)
                .transcription("1")
                .target("1")
                .targetLang(LangDto.RU);

        securedApiCaller.postPhrase(createPhraseRequest, auth(""))
                .andExpect(status().isForbidden());
    }

    @Test
    void postRequest403_authHeaderPresentButOnlyType() {
        registerUserAndEnable();

        var createPhraseRequest = new CreatePhraseRequest()
                .source("1")
                .sourceLang(LangDto.FR)
                .transcription("1")
                .target("1")
                .targetLang(LangDto.RU);

        securedApiCaller.postPhrase(createPhraseRequest, auth("Bearer "))
                .andExpect(status().isForbidden());
    }

    @Test
    void postRequest403_authHeaderPresentButInvalid() {
        registerUserAndEnable();

        var createPhraseRequest = new CreatePhraseRequest()
                .source("1")
                .sourceLang(LangDto.FR)
                .transcription("1")
                .target("1")
                .targetLang(LangDto.RU);

        securedApiCaller.postPhrase(createPhraseRequest, auth("Bearer Adfasdfasdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestAuthenticated() {
        registerUserAndEnable();
        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var response = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andReturnAs(AuthenticationResponse.class);

//        var bearerToken = response.getAuthenticationToken();

        var createPhraseRequest = new CreatePhraseRequest()
                .source("1")
                .sourceLang(LangDto.FR)
                .transcription("1")
                .target("1")
                .targetLang(LangDto.RU);

//        securedApiCaller.postPhrase(createPhraseRequest, auth("Bearer " + bearerToken))
//                .andExpect(status().isCreated());
//
//        securedApiCaller.postPhrase(createPhraseRequest, auth("Bearer " + bearerToken))
//                .andExpect(status().isCreated());
    }

    void registerUserAndEnable() {
        var registerRequest = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL);

        securedApiCaller.register(registerRequest)
                .andExpect(status().isOk());

        var user = apiUserRepository.findByUsernameOrThrow(USERNAME);
        user.enable();
        apiUserRepository.save(user);
    }

}
