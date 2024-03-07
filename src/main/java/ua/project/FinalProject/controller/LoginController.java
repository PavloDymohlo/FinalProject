package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.UserService;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping
    public String loginUser(Model model, @ModelAttribute("user") UserEntity user) {
        try {
            userService.loginIn(user.getPhoneNumber(), user.getPassword());
            return "redirect:/personal_office/" + user.getPhoneNumber();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/host_page";
        }
    }
}