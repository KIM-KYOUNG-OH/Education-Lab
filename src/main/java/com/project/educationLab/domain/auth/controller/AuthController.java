package com.project.educationLab.domain.auth.controller;

import com.project.educationLab.domain.auth.dto.PrincipalDetails;
import com.project.educationLab.domain.auth.dto.UserLoginRequest;
import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.auth.service.PrincipalDetailsService;
import com.project.educationLab.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final PrincipalDetailsService principalDetailsService;

    /*
     * to do
     * jwt 토큰
     * inmemory db로 로그인 상태 정보 관리
     * OAuth 2.0 access token & refresh token 관리
     * response 객체 만들기
     * exception handler 만들기
     */

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication) {
        log.info("/test/login==========");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("authentication : {}", principalDetails.getUser());
        return "세션 정보 확인하기";
    }

    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<String> join(@Validated @RequestBody UserJoinRequest user) {
        userService.saveUser(user);
        return ResponseEntity.ok("success");
    }
}
