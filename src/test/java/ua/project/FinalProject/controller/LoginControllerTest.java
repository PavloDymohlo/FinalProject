package ua.project.FinalProject.controller;

import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testLoginUser_Success() throws Exception {
        UserEntity user = new UserEntity();
        user.setPhoneNumber(80501234569L);
        user.setPassword("password");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getPhoneNumber()),
                user.getPassword(),
                Collections.emptyList()
        );

        when(userService.loginIn(anyLong(), anyString())).thenReturn("success");
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generated_token");
        when(userService.isAdminSubscription(anyLong())).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"phoneNumber\": \"" + user.getPhoneNumber() + "\", \"password\": \"" + user.getPassword() + "\" }")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/personal_office"));
    }
}