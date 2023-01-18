package com.project.educationLab.domain.auth.dto;

import com.project.educationLab.domain.user.entity.AuthProvider;
import com.project.educationLab.domain.user.entity.UserRole;
import com.project.educationLab.global.common.annotation.EnumPattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@ToString
public class UserJoinRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 8자 이상, 최소 하나 이상의 대문자, 소문자, 숫자, 특수 문자를 포함하세요.")
    private String password;

    @Email @NotBlank
    private String email;

    @EnumPattern(regexp = "ROLE_USER|ROLE_ADMIN|ROLE_MANAGER", message = "ROLE_USER, ROLE_ADMIN, ROLE_MANAGER만 입력 가능합니다.")
    private UserRole userRole;

    @EnumPattern(regexp = "NAVER", message = "NAVER만 입력 가능합니다.")
    private AuthProvider authProvider;

    public UserJoinRequest() {
    }

    public UserJoinRequest(String username, String password, String email, UserRole userRole, AuthProvider authProvider) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
        this.authProvider = authProvider;
    }
}
