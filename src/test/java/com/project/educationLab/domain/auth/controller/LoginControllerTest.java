package com.project.educationLab.domain.auth.controller;

import com.google.gson.Gson;
import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    public void wrongUsernameRequest() throws Exception {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username(null)
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("USER")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        //then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void wrongPasswordRequest() throws Exception {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username("test")
                .password("1234")
                .email("sample@naver.com")
                .role("USER")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        //then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void wrongEmailRequest() throws Exception {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("wrongEmail")
                .role("USER")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        //then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void wrongRoleRequest() throws Exception {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("OWNER")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        //then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void joinSuccess() throws Exception {
        //given
        UserJoinRequest request = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("USER")
                .build();

        User userResponse = User.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("USER")
                .build();

        doReturn(userResponse).when(userService)
                .saveUser(any(UserJoinRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        //then
        resultActions.andExpect(status().isOk());
        verify(userService, times(1)).saveUser(any(UserJoinRequest.class));
    }
}