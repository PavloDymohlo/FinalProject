package ua.project.FinalProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/register")
@Tag(name = "Registration Controller", description = "Operations related to user registration")
public class RegController {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @Operation(summary = "Register user")
    @PostMapping
    public ResponseEntity<String> registerUser(Model model, @RequestBody UserEntity user) {
        try {
            userService.registerUser(user.getPhoneNumber(),
                    user.getBankCardNumber(),
                    user.getPassword());
            userService.setAutoRenew(user.getPhoneNumber(), AutoRenewStatus.YES);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(user.getPhoneNumber()));
            String jwtToken = jwtService.generateToken(userDetails);
            log.info("User registered successfully with phone number {}", user.getPhoneNumber());
            log.info("Generated JWT token: {}", jwtToken);
            return ResponseEntity.ok(jwtToken);
        } catch (IllegalArgumentException e) {
            log.error("Registration failed for user with phone number {}: {}", user.getPhoneNumber(), e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}