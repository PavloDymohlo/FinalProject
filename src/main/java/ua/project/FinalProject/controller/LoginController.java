package ua.project.FinalProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/login")
@Tag(name = "Login Controller", description = "Operations related to user login")
public class LoginController {
    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = "Show login form")
    @GetMapping
    public String loginUserShow(Model model) {
        try {
            log.info("User login redirected to personal office.");
            return "redirect:/personal_office/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.error("Error occurred during user login: {}", e.getMessage());
            return "pages/host_page";
        }
    }

    @Operation(summary = "Login user")
    @PostMapping
    public ResponseEntity<String> loginUser(@RequestBody UserEntity user) {
        try {
            userService.loginIn(user.getPhoneNumber(), user.getPassword());
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(user.getPhoneNumber()));
            String jwtToken = jwtService.generateToken(userDetails);
            log.info("Generated JWT token: {}", jwtToken);
            if (userService.isAdminSubscription(user.getPhoneNumber())) {
                log.info("User with phone number {} redirected to admin_office page", user.getPhoneNumber());
                return ResponseEntity.ok("redirect:/admin_office");
            } else {
                log.info("User with phone number {} redirected to personal_office page", user.getPhoneNumber());
                return ResponseEntity.ok("redirect:/personal_office");
            }
        } catch (IllegalArgumentException e) {
            log.error("Login attempt failed for user with phone number {}: {}", user.getPhoneNumber(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
        }
    }
}