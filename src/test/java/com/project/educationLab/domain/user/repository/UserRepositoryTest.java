package com.project.educationLab.domain.user.repository;

import com.project.educationLab.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void findByUsername() {
        // given
        String username = "test";
        User user = User.builder()
                .username(username)
                .password("test")
                .email("test@test.com")
                .role("USER")
                .providerId("test")
                .build();
        userRepository.save(user);

        // when
        User find = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("username not found"));

        // then
        assertThat(find.getUsername()).isEqualTo(user.getUsername());
        assertThat(find.getPassword()).isEqualTo(user.getPassword());
        assertThat(find.getEmail()).isEqualTo(user.getEmail());
        assertThat(find.getRole()).isEqualTo(user.getRole());
        assertThat(find.getProviderId()).isEqualTo(user.getProviderId());
    }
}