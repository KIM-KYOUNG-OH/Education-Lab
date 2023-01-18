package com.project.educationLab.domain.user.service;

import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.entity.UserRole;
import com.project.educationLab.domain.user.exception.DuplicateUsernameException;
import com.project.educationLab.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void duplicateUserId() {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .userRole(UserRole.ROLE_USER)
                .build();
        User response = User.builder()
                        .username("tom")
                        .password(bCryptPasswordEncoder.encode("Ttest1234@@"))
                        .email("sample@naver.com")
                        .UserRole(UserRole.ROLE_USER)
                        .build();
        doReturn(Optional.of(response)).when(userRepository)
                .findByUsername(any(String.class));

        //when & Then
        assertThrows(DuplicateUsernameException.class, () -> userService.saveUser(request));
    }

    @Test
    public void saveSuccess() {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .userRole(UserRole.ROLE_USER)
                .build();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encPassword = encoder.encode(request.getPassword());
        doReturn(User.builder()
                .username(request.getUsername())
                .password(encPassword)
                .email(request.getEmail())
                .UserRole(request.getUserRole())
                .build()).when(userRepository)
                .save(any(User.class));

        //when
        User user = userService.saveUser(request);

        //then
        assertThat(user.getUsername()).isEqualTo(request.getUsername());
        assertThat(user.getPassword()).isEqualTo(encPassword);
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getUserRole()).isEqualTo(request.getUserRole());

        verify(userRepository, times(1)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(1)).encode(any(String.class));
    }
}