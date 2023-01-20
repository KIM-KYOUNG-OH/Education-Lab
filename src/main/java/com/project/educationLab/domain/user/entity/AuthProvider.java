package com.project.educationLab.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum AuthProvider {
    NAVER("NAVER");

    private final String providerId;

    AuthProvider(String providerId) {
        this.providerId = providerId;
    }

    @JsonCreator
    public static AuthProvider findById(String value) {
        return Stream.of(AuthProvider.values())
                .filter(v -> v.providerId.equals(value))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String getProviderId() {
        return providerId;
    }
}
