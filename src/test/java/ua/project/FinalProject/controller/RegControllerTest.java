package ua.project.FinalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RegControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RegController regController;

    @Mock
    private Model model;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void RegControllerTest_RegisterUser_Success() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setPhoneNumber(80663256999L);
        userEntity.setBankCardNumber(1234567890123456L);
        userEntity.setPassword("password");

        when(jwtService.generateToken(any())).thenReturn("jwtToken");
        when(userService.registerUser(anyLong(), anyLong(), anyString())).thenReturn(userEntity);

        ResponseEntity<String> response = regController.registerUser(model, userEntity);

        verify(userService, times(1)).registerUser(80663256999L, 1234567890123456L, "password");
        verify(userService, times(1)).setAutoRenew(80663256999L, AutoRenewStatus.YES);
        verify(customUserDetailsService, times(1)).loadUserByUsername("80663256999");
        verify(jwtService, times(1)).generateToken(any());
        verifyNoMoreInteractions(userService, customUserDetailsService, jwtService);

        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals("jwtToken");
    }

    @Test
    public void testRegisterUser_Failure() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setPhoneNumber(80663256999L);
        userEntity.setBankCardNumber(1234567890123456L);
        userEntity.setPassword("password");

        when(userService.registerUser(anyLong(), anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid phone number"));

        ResponseEntity<String> response = regController.registerUser(model, userEntity);

        verify(userService, times(1)).registerUser(80663256999L, 1234567890123456L, "password");
        verifyNoMoreInteractions(userService, customUserDetailsService, jwtService);

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().equals("Invalid phone number");
    }
}