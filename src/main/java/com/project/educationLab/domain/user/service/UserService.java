package com.project.educationLab.domain.user.service;

import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.exception.DuplicateEmailException;
import com.project.educationLab.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public User saveUser(UserJoinRequest userJoinRequest) {
        userRepository.findByEmail(userJoinRequest.getEmail()).ifPresent(a -> {
            throw new DuplicateEmailException();
        });

        String encPassword = bCryptPasswordEncoder.encode(userJoinRequest.getPassword());
        User user = User.builder()
                .username(userJoinRequest.getUsername())
                .email(userJoinRequest.getEmail())
                .password(encPassword)
                .role(userJoinRequest.getRole())
                .build();
        return userRepository.save(user);
    }
}
