package ua.project.FinalProject.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping
    public String loginUserShow(Model model) {
        try {
            return "redirect:/personal_office/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/host_page";
        }
    }

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


//@Controller
//@RequestMapping("/login")
//@RequiredArgsConstructor
//@Slf4j
//public class LoginController {
//
//    private final UserService userService;
//
//    private final JwtService jwtService;;
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @GetMapping
//    public String loginUserShow(Model model) {
//        try {
//            return "redirect:/personal_office/";
//        } catch (IllegalArgumentException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "pages/host_page";
//        }
//    }
//
//    @PostMapping
//    public ResponseEntity<String> loginUser(@RequestBody UserEntity user) {
//        try {
//            userService.loginIn(user.getPhoneNumber(), user.getPassword());
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(user.getPhoneNumber()));
//            String jwtToken = jwtService.generateToken(userDetails);
//            log.info("Generated JWT token: {}", jwtToken);
//            return ResponseEntity.ok(jwtToken);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
//        }
//    }
//}
//
