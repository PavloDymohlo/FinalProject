package ua.project.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import ua.project.FinalProject.service.UserService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegController {
    @Autowired
    private UserService userService;

    @PostMapping
    public String registerUser(Model model, @ModelAttribute("user") UserEntity user) {
        try {
            userService.registerUser(user.getPhoneNumber(),
                    user.getBankCardNumber(),
                    user.getPassword());
            userService.setAutoRenew(user.getPhoneNumber(), AutoRenewStatus.YES);
            return "redirect:/personal_office/" + user.getPhoneNumber();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/register";
        }
    }
}