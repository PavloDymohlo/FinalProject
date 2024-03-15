package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RegController regController;

    @Test
    public void RegControllerTest_Success() {
        UserEntity user = new UserEntity();
        user.setPhoneNumber(80501256987L);
        user.setBankCardNumber(1234567890123456L);
        user.setPassword("password");
        when(userService.registerUser(user.getPhoneNumber(), user.getBankCardNumber(), user.getPassword())).thenReturn(user);
        when(customUserDetailsService.loadUserByUsername(String.valueOf(user.getPhoneNumber()))).thenReturn(null);
        when(jwtService.generateToken(any())).thenReturn("jwtToken");
        ResponseEntity<String> responseEntity = regController.registerUser(new ConcurrentModel(), user);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("jwtToken", responseEntity.getBody());
        verify(userService, times(1)).registerUser(user.getPhoneNumber(), user.getBankCardNumber(), user.getPassword());
        verify(userService, times(1)).setAutoRenew(user.getPhoneNumber(), AutoRenewStatus.YES);
        verify(customUserDetailsService, times(1)).loadUserByUsername(String.valueOf(user.getPhoneNumber()));
        verify(jwtService, times(1)).generateToken(any());
    }

    @Test
    public void RegControllerTest_DuplicatePhoneNumber() {
        UserEntity user = new UserEntity();
        user.setPhoneNumber(80501256987L);
        user.setBankCardNumber(1234567890123456L);
        user.setPassword("password");
        when(userService.registerUser(user.getPhoneNumber(), user.getBankCardNumber(), user.getPassword())).thenThrow(new IllegalArgumentException("Phone number already exists"));
        Model model = new ConcurrentModel();
        ResponseEntity<String> responseEntity = regController.registerUser(model, user);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Phone number already exists", responseEntity.getBody());
        verify(userService, times(1)).registerUser(user.getPhoneNumber(), user.getBankCardNumber(), user.getPassword());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(customUserDetailsService);
        verifyNoInteractions(jwtService);
    }

    @Test
    public void RegControllerTest_DuplicateBankCardNumber() {
        UserEntity user = new UserEntity();
        user.setPhoneNumber(80501256987L);
        user.setBankCardNumber(1234567890123456L);
        user.setPassword("password");
        when(userService.registerUser(user.getPhoneNumber(), user.getBankCardNumber(), user.getPassword())).thenThrow(new IllegalArgumentException("Bank card number already linked to another user"));
        Model model = new ConcurrentModel();
        ResponseEntity<String> responseEntity = regController.registerUser(model, user);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Bank card number already linked to another user", responseEntity.getBody());
        verify(userService, times(1)).registerUser(user.getPhoneNumber(), user.getBankCardNumber(), user.getPassword());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(customUserDetailsService);
        verifyNoInteractions(jwtService);
    }
}