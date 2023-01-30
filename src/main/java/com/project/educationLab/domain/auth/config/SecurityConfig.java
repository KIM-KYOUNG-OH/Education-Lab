package com.project.educationLab.domain.auth.config;

import com.project.educationLab.domain.auth.repository.CookieAuthorizationRequestRepository;
import com.project.educationLab.domain.auth.service.PrincipalOauth2UserService;
import com.project.educationLab.domain.auth.util.OAuth2AuthenticationFailureHandler;
import com.project.educationLab.domain.auth.util.OAuth2AuthenticationSuccessHandler;
import com.project.educationLab.domain.auth.util.JwtAuthenticationFilter;
import com.project.educationLab.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // 선택적으로 권한 처리시 @Secured나 @PreAuthorize 사용
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access(String.format("hasRole('%s') or hasRole('%s')", UserRole.ROLE_MANAGER.getRole(), UserRole.ROLE_ADMIN.getRole()))
                .antMatchers("/admin/**").access(String.format("hasRole('%s')", UserRole.ROLE_ADMIN.getRole()))
                .anyRequest().permitAll();

        http.formLogin();

        http.oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                .and()
                .userInfoEndpoint()
                    .userService(principalOauth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
