package com.project.educationLab.domain.auth.controller;

import com.google.gson.Gson;
import com.project.educationLab.domain.auth.dto.UserJoinRequest;
import com.project.educationLab.domain.auth.service.PrincipalDetailsService;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void invalidUserJoinRequest() throws Exception {
        //given
        UserJoinRequest invalidUsernameRequest =  UserJoinRequest.builder()
                .username(null)
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("USER")
                .build();

        UserJoinRequest invalidPasswordRequest = UserJoinRequest.builder()
                .username("test")
                .password("1234")
                .email("sample@naver.com")
                .role("USER")
                .build();

        UserJoinRequest invalidEmailRequest = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("wrongEmail")
                .role("USER")
                .build();

        UserJoinRequest invalidRoleRequest = UserJoinRequest.builder()
                .username("tom")
                .password("Ttest1234@@")
                .email("sample@naver.com")
                .role("OWNER")
                .build();


        //when
        ResultActions resultActions1 = sendRequestToJoinAPI(invalidUsernameRequest);
        ResultActions resultActions2 = sendRequestToJoinAPI(invalidPasswordRequest);
        ResultActions resultActions3 = sendRequestToJoinAPI(invalidEmailRequest);
        ResultActions resultActions4 = sendRequestToJoinAPI(invalidRoleRequest);

        //then
        resultActions1.andExpect(status().is4xxClientError());
        resultActions2.andExpect(status().is4xxClientError());
        resultActions3.andExpect(status().is4xxClientError());
        resultActions4.andExpect(status().is4xxClientError());
    }

    private ResultActions sendRequestToJoinAPI(UserJoinRequest invalidUsernameRequest) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(invalidUsernameRequest))
        );
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