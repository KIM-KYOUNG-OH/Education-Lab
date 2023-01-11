package com.project.educationLab.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class UserLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
