package com.home.dictionary;

import com.home.dictionary.model.configuration.ApiProperty;
import com.home.dictionary.model.user.Authority;
import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.repository.ApiUserRepository;
import com.home.dictionary.util.Header;
import com.home.dictionary.util.WithDataBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.home.dictionary.model.configuration.ApiPropertyKey.REGISTRATION_ALLOWED;
import static com.home.dictionary.util.Header.auth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(status().isCreated())
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
        var sevenDays = 7 * 24 * 60 * 60;

        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var resultAction = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().httpOnly("jwt", true))
                .andExpect(cookie().maxAge("jwt", sevenDays))
                .andExpect(cookie().secure("jwt", true));

        var jwtCookie = resultAction.andReturn().getResponse().getCookie("jwt");
        var response = resultAction.andReturnAs(AuthenticationResponse.class);

        assertThat(jwtCookie.getValue()).isNotBlank();
        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getAccessToken()).isNotBlank();

        var savedUser = apiUserRepository.findByUsernameOrThrow(USERNAME);
        assertThat(savedUser.getRefreshToken()).isNotBlank();
        assertThat(savedUser.getRefreshToken()).isEqualTo(jwtCookie.getValue());
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
    void getRequestAuthenticated() {
        registerUserAndEnable();
        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var response = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andReturnAs(AuthenticationResponse.class);

        var bearerToken = response.getAccessToken();

        securedApiCaller.getPageOfPhrase("?page=0&size=20", auth("Bearer " + bearerToken))
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
    void postRequestAuthenticated() {
        registerUserAndEnable();
        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var response = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andReturnAs(AuthenticationResponse.class);

        var bearerToken = response.getAccessToken();

        var createPhraseRequest = new CreatePhraseRequest()
                .source("1")
                .sourceLang(LangDto.FR)
                .transcription("1")
                .target("1")
                .targetLang(LangDto.RU);

        securedApiCaller.postPhrase(createPhraseRequest, auth("Bearer " + bearerToken))
                .andExpect(status().isCreated());

        // same access token works several times
        securedApiCaller.postPhrase(createPhraseRequest, auth("Bearer " + bearerToken))
                .andExpect(status().isCreated());
    }

    @Test
    void usingRefreshTokenToGetNewAccessToken() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var resultAction = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"));

        var jwtCookie = resultAction.andReturn().getResponse().getCookie("jwt");
        var response = resultAction.andReturnAs(AuthenticationResponse.class);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(jwtCookie.getValue()).isNotBlank();


        var refreshResponse = securedApiCaller.refresh(Header.cookie("jwt", jwtCookie.getValue()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("jwt"))
                .andReturnAs(AuthenticationResponse.class);

        assertThat(refreshResponse.getUsername()).isEqualTo(USERNAME);
        assertThat(refreshResponse.getAccessToken()).isNotBlank();

        var savedUser = apiUserRepository.findByUsernameOrThrow(USERNAME);
        assertThat(savedUser.getRefreshToken()).isNotBlank();
        assertThat(savedUser.getRefreshToken()).isEqualTo(jwtCookie.getValue());
    }

    @Test
    void logoutInvalidatesCookieAndDeletesRefreshToken() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var resultAction = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"));

        var jwtCookie = resultAction.andReturn().getResponse().getCookie("jwt");
        var response = resultAction.andReturnAs(AuthenticationResponse.class);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(jwtCookie.getValue()).isNotBlank();

        var resultAfterLogout = securedApiCaller.logout(Header.cookie("jwt", jwtCookie.getValue()))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().httpOnly("jwt", true))
                .andExpect(cookie().maxAge("jwt", -1))
                .andExpect(cookie().secure("jwt", true));

        var jwtCookieAfterLogout = resultAfterLogout.andReturn().getResponse().getCookie("jwt");
        assertThat(jwtCookieAfterLogout.getValue()).isBlank();

        var savedUser = apiUserRepository.findByUsernameOrThrow(USERNAME);
        assertThat(savedUser.getRefreshToken()).isNull();
    }

    @Test
    void refreshDoesNotProvideNewAccessTokenAfterLogout() {
        registerUserAndEnable();

        var loginRequest = new LoginRequest().username(USERNAME).password(PASSWORD);
        var resultAction = securedApiCaller.login(loginRequest)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"));

        var jwtCookie = resultAction.andReturn().getResponse().getCookie("jwt");
        var response = resultAction.andReturnAs(AuthenticationResponse.class);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(jwtCookie.getValue()).isNotBlank();

        var resultAfterLogout = securedApiCaller.logout(Header.cookie("jwt", jwtCookie.getValue()))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("jwt", -1));

        securedApiCaller.refresh(Header.cookie("jwt", jwtCookie.getValue()))
                .andExpect(status().isForbidden());
    }


    void registerUserAndEnable() {
        var registerRequest = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL);

        securedApiCaller.register(registerRequest)
                .andExpect(status().isCreated());

        var user = apiUserRepository.findByUsernameOrThrow(USERNAME);
        user.enable();
        apiUserRepository.save(user);
    }

}
