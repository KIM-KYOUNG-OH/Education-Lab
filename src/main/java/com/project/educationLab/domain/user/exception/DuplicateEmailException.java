package com.project.educationLab.domain.user.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
    }

    public DuplicateEmailException(String message) {
        super(message);
    }
}
