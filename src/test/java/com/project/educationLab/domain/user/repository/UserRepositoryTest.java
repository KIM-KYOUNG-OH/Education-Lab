package com.project.educationLab.domain.user.repository;

import com.project.educationLab.domain.user.entity.AuthProvider;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
                .UserRole(UserRole.ROLE_USER)
                .authProvider(AuthProvider.NAVER)
                .build();
        userRepository.save(user);

        // when
        User find = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("username not found"));

        // then
        assertThat(find.getUsername()).isEqualTo(user.getUsername());
        assertThat(find.getPassword()).isEqualTo(user.getPassword());
        assertThat(find.getEmail()).isEqualTo(user.getEmail());
        assertThat(find.getUserRole()).isEqualTo(user.getUserRole());
        assertThat(find.getAuthProvider()).isEqualTo(user.getAuthProvider());
    }
}