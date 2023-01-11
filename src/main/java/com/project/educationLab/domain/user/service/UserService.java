package com.project.educationLab.domain.user.service;

import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.exception.DuplicateUsernameException;
import com.project.educationLab.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public User saveUser(UserJoinRequest userJoinRequest) {
        userRepository.findByUsername(userJoinRequest.getUsername()).ifPresent(a -> {
            throw new DuplicateUsernameException();
        });

        String encPassword = bCryptPasswordEncoder.encode(userJoinRequest.getPassword());
        User user = User.builder()
                .username(userJoinRequest.getUsername())
                .email(userJoinRequest.getEmail())
                .password(encPassword)
                .role(userJoinRequest.getRole())
                .providerId(userJoinRequest.getProviderId())
                .build();
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
