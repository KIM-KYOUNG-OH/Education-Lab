package com.project.educationLab.domain.auth.util;

import com.project.educationLab.domain.auth.dto.NaverOAuth2UserInfo;
import com.project.educationLab.domain.auth.dto.OAuth2UserInfo;
import com.project.educationLab.domain.user.entity.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        if (authProvider == AuthProvider.NAVER) {
            return new NaverOAuth2UserInfo(attributes);
        }
        throw new IllegalArgumentException("Invalid Provider Type");
    }
}
