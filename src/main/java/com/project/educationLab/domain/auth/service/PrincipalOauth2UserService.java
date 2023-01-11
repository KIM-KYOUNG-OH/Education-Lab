package com.project.educationLab.domain.auth.service;

import com.project.educationLab.domain.auth.dto.PrincipalDetails;
import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.repository.UserRepository;
import com.project.educationLab.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    // "/login/oauth2/code/{providerId}" 요청시 자동으로 동작
    // 소셜 로그인시 결과가 OAuth2User 타입 객체에 담김
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        UserJoinRequest userJoinRequest = parseToUserJoinRequest(userRequest, oAuth2User);

        User find = userService.findByUsername(userJoinRequest.getUsername()).orElseGet(() -> saveOAuthUser(userJoinRequest));

        return new PrincipalDetails(find, oAuth2User.getAttributes());
    }

    private UserJoinRequest parseToUserJoinRequest(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        log.info("getClientRegistration : {}", userRequest.getClientRegistration());
        log.info("getAccessToken : {}", userRequest.getAccessToken().getTokenValue());
        log.info("getAdditionalParameters : {}", userRequest.getAdditionalParameters());

        log.info("getAttributes : {}", oAuth2User.getAttributes());
        log.info("getAuthorities : {}", oAuth2User.getAuthorities());

        String providerId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> response = oAuth2User.getAttribute("response");
        String userId = (String) response.get("id");
        String username = providerId + "_" + userId;  // 중복 불허
        String password = "defaultPassword";
        String email = (String) response.get("email");
        String role = "USER";
        return UserJoinRequest.builder()
                .providerId(providerId)
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
    }

    private User saveOAuthUser(UserJoinRequest userJoinRequest) {
        log.info("최초 소셜 로그인이 감지되어 회원가입합니다.");
        return userService.saveUser(userJoinRequest);
    }
}
