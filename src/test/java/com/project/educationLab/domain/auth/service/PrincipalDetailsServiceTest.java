package com.project.educationLab.domain.auth.service;

import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.entity.UserRole;
import com.project.educationLab.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrincipalDetailsServiceTest {

    @InjectMocks
    private PrincipalDetailsService principalDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void usernameNotFound() {
        //given
        String username = "testUser";

        //when
        doReturn(Optional.empty()).when(userRepository)
                .findByUsername(any(String.class));

        //then
        assertThrows(UsernameNotFoundException.class, () -> principalDetailsService.loadUserByUsername(username));
    }

    @Test
    public void localLoginSuccess() {
        //given
        String username = "testUser";

        User userResponse = User.builder()
                .username("testUser")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .UserRole(UserRole.ROLE_USER)
                .build();

        //when
        doReturn(Optional.of(userResponse)).when(userRepository)
                .findByUsername(any(String.class));

        UserDetails userDetails = principalDetailsService.loadUserByUsername(username);

        //then
        assertThat(username).isEqualTo(userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(any(String.class));
    }
}