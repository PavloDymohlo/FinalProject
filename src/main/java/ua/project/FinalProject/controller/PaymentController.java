package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.service.UserService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/personal_office/{phoneNumber}/payment")
public class PaymentController {
    private final UserService userService;

    @PostMapping()
    public String processPayment(@PathVariable long phoneNumber, @RequestParam String subscriptionName, Model model) {
        try {
            userService.subscriptionPayment(phoneNumber, subscriptionName);
            model.addAttribute("message", "Payment successful");
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Insufficient funds or invalid subscription");
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
        }
        return "pages/personal_office";
    }
}