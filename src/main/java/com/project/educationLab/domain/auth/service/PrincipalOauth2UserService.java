package com.project.educationLab.domain.auth.service;

import com.project.educationLab.domain.auth.dto.OAuth2UserInfo;
import com.project.educationLab.domain.auth.dto.PrincipalDetails;
import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.auth.util.OAuth2UserInfoFactory;
import com.project.educationLab.domain.user.entity.AuthProvider;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.entity.UserRole;
import com.project.educationLab.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        UserJoinRequest userJoinRequest = parseToUserJoinRequest(userRequest, oAuth2User);

        User find = userService.findByUsername(userJoinRequest.getUsername()).orElseGet(() -> saveOAuthUser(userJoinRequest));

        return new PrincipalDetails(find, oAuth2User.getAttributes());
    }

    private UserJoinRequest parseToUserJoinRequest(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        log.info("======global login======");
        log.info("getClientRegistration : {}", userRequest.getClientRegistration());
        log.info("getAccessToken : {}", userRequest.getAccessToken().getTokenValue());
        log.info("getAdditionalParameters : {}", userRequest.getAdditionalParameters());
        log.info("getAttributes : {}", oAuth2User.getAttributes());
        log.info("getAuthorities : {}", oAuth2User.getAuthorities());

        AuthProvider authProvider = AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, attributes);
        String username = String.format("%s_%s", authProvider.getProviderId(), oAuth2UserInfo.getId());
        String password = "defaultPassword";
        String email = oAuth2UserInfo.getEmail();
        return UserJoinRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .userRole(UserRole.ROLE_USER)
                .authProvider(authProvider)
                .build();
    }

    private User saveOAuthUser(UserJoinRequest userJoinRequest) {
        log.info("최초 소셜 로그인이 감지되어 회원가입합니다.");
        return userService.saveUser(userJoinRequest);
    }
}
