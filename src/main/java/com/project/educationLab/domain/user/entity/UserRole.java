package com.project.educationLab.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

public enum UserRole {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_MANAGER("ROLE_MANAGER");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @JsonCreator
    public static UserRole findByRole(String value) {
        return Stream.of(UserRole.values())
                .filter(v -> v.role.equals(value))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String getRole() {
        return role;
    }
}
