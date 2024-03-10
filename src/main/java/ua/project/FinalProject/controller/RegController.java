package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
@Slf4j
public class RegController {
    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<String> registerUser(Model model, @RequestBody UserEntity user) {
        try {
            userService.registerUser(user.getPhoneNumber(),
                    user.getBankCardNumber(),
                    user.getPassword());
            userService.setAutoRenew(user.getPhoneNumber(), AutoRenewStatus.YES);


            UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(user.getPhoneNumber()));


            String jwtToken = jwtService.generateToken(userDetails);
            log.info("Generated JWT token: {}", jwtToken);

            return ResponseEntity.ok(jwtToken);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}