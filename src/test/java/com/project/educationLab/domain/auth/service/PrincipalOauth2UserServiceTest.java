package com.project.educationLab.domain.auth.service;

import com.google.gson.Gson;
import com.project.educationLab.domain.auth.dto.PrincipalDetails;
import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.repository.UserRepository;
import com.project.educationLab.domain.user.service.UserService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrincipalOauth2UserServiceTest {

    @InjectMocks
    private PrincipalOauth2UserService principalOauth2UserService;

    @Mock
    private UserService userService;

    private ClientRegistration testClientRegistration;

    private OAuth2AccessToken testAccessToken;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        String userInfoResponse = "{\n" +
                "\"resultcode\": \"00\"\n" +
                ", \"message\": \"success\"\n" +
                ", \"response\": {\n" +
                "\"id\": \"jO_DkMi63xbgX32ZiY84sdLl7-NEmUsuemtSBamZFIQ\"\n" +
                ", \"email\": \"kdkg1234@naver.com\"\n" +
                ", \"name\": \"김경오\"}\n" +
                "}";
        this.mockWebServer.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.mockWebServer.url("/user").toString();

        this.testClientRegistration = ClientRegistration.withRegistrationId("test_registration_id")
                .clientId("client_id")
                .clientSecret("client_secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://client.com")
                .scope(new LinkedHashSet<>(Arrays.asList("scope1", "scope2")))
                .authorizationUri("https://provider.com/oauth2/authorization")
                .tokenUri("https://provider.com/oauth2/token")
                .userInfoUri(userInfoUri)
                .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)
                .userNameAttributeName("response")
                .clientName("client_name")
                .build();

        this.testAccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token-1234", Instant.now(),
                Instant.now().plusSeconds(60), new LinkedHashSet<>(Arrays.asList("scope1", "scope2")));
    }

    private MockResponse jsonResponse(String json) {
        return new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE).setBody(json);
    }

    @AfterEach
    public void cleanUp() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    public void socialLoginAlreadyJoinedUserSuccess() {
        //given
        String username = "testUser";
        User userResponse = User.builder()
                .username(username)
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("USER")
                .providerId("test")
                .build();

        //when
        doReturn(Optional.of(userResponse)).when(userService)
                .findByUsername(any(String.class));

        PrincipalDetails response = (PrincipalDetails) principalOauth2UserService.loadUser(new OAuth2UserRequest(testClientRegistration, testAccessToken));

        //then
        assertThat(response.getUsername()).isEqualTo(userResponse.getUsername());
        assertThat(response.getPassword()).isEqualTo(userResponse.getPassword());
        verify(userService, times(1)).findByUsername(any(String.class));
        verify(userService, times(0)).saveUser(any(UserJoinRequest.class));
    }

    @Test
    public void socialLoginAndSaveSuccess() {
        //given
        String username = "testDiff";
        User userResponse = User.builder()
                .username(username)
                .password("testDiff")
                .email("testDiff@test.com")
                .role("USER")
                .providerId("test")
                .build();
        doReturn(Optional.empty()).when(userService)
                .findByUsername(any(String.class));
        doReturn(userResponse).when(userService)
                .saveUser(any(UserJoinRequest.class));

        //when
        PrincipalDetails response = (PrincipalDetails) principalOauth2UserService.loadUser(new OAuth2UserRequest(testClientRegistration, testAccessToken));

        //then
        verify(userService, times(1)).findByUsername(any(String.class));
        verify(userService, times(1)).saveUser(any(UserJoinRequest.class));
    }
}