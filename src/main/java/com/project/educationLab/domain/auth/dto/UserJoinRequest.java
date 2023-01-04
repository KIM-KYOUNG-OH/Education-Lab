package com.project.educationLab.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class UserJoinRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 8자 이상, 최소 하나 이상의 대문자, 소문자, 숫자, 특수 문자를 포함하세요.")
    private String password;
    @Email @NotBlank
    private String email;
    @NotBlank @Pattern(regexp = "USER|MANAGER|ADMIN")
    private String role;

    public UserJoinRequest() {
    }

    public UserJoinRequest(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
