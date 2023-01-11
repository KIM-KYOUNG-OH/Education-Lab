package com.project.educationLab.domain.auth.service;

import com.project.educationLab.domain.auth.dto.PrincipalDetails;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;

    // "/login" 요청시 자동으로 동작
    // 로컬 로그인시 결과가 UserDetails 타입 객체에 담김
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User find = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("username is not found"));
        return new PrincipalDetails(find);
    }
}
