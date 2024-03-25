package ua.project.FinalProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/personal_office")
@Tag(name = "Personal Office Controller", description = "Operations related to personal office management")
public class PersonalOfficeController {
    private final UserService userService;
    @Operation(summary = "Show personal office")
    @GetMapping("/{phoneNumber}")
    public String showPersonalOffice(@PathVariable("phoneNumber") String phoneNumber, Model model) {
        try {
            long phone = Long.parseLong(phoneNumber);
            UserEntity user = userService.getUserByPhoneNumber(phone);
            if (user != null) {
                model.addAttribute("user", user);
                log.info("Personal office accessed successfully for user with phone number {}", phoneNumber);
                return "pages/personal_office";
            } else {
                log.warn("User with phone number {} not found", phoneNumber);
                return "pages/error";
            }
        } catch (NumberFormatException e) {
            log.error("Invalid phone number format: {}", phoneNumber);
            return "pages/error";
        }
    }
}