package com.project.educationLab.domain.auth.controller;

import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    /**
     * to do
     * jwt 토큰
     * inmemory db로 로그인 상태 정보 관리
     * OAuth 2.0 access token & refresh token 관리
     */
    @GetMapping("/login")
    @ResponseBody
    public String login() {
        return "login";
    }

    /**
     * to do
     * response 객체 만들기
     * exception handler 만들기
     */
    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<String> join(@Validated @RequestBody UserJoinRequest user) {
        userService.saveUser(user);
        return ResponseEntity.ok("success");
    }
}
