package ua.project.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.UserService;

@Controller
@RequestMapping("/personal_office")
public class PersonalOfficeController {

    @Autowired
    private UserService userService;

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

//    @GetMapping("/{phoneNumber}")
//    public String showPersonalOffice(@PathVariable("phoneNumber") String phoneNumber, Model model) {
//        try {
//            long phone = Long.parseLong(phoneNumber);
//            UserEntity user = userService.getUserByPhoneNumber(phone);
//            if (user != null) {
//                model.addAttribute("user", user);
//                return "pages/personal_office";
//            } else {
//                return "pages/error";
//            }
//        } catch (NumberFormatException e) {
//            return "pages/error";
//        }
//    }
