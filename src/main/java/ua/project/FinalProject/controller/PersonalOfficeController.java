package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/personal_office")
public class PersonalOfficeController {
    private final UserService userService;

    @GetMapping("/{phoneNumber}")
    public String showPersonalOffice(@PathVariable("phoneNumber") String phoneNumber, Model model) {
        try {
            long phone = Long.parseLong(phoneNumber);
            UserEntity user = userService.getUserByPhoneNumber(phone);
            if (user != null) {
                model.addAttribute("user", user);
                return "pages/personal_office";
            } else {
                return "pages/error";
            }
        } catch (NumberFormatException e) {
            return "pages/error";
        }
    }
}