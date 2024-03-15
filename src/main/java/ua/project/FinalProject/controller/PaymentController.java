package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.service.UserService;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/personal_office/{phoneNumber}/payment")
public class PaymentController {
    private final UserService userService;

    @PostMapping()
    public String processPayment(@PathVariable long phoneNumber, @RequestParam String subscriptionName, Model model) {
        try {
            userService.subscriptionPayment(phoneNumber, subscriptionName);
            model.addAttribute("message", "Payment successful");
            log.info("Payment processed successfully for user with phone number {} for subscription {}", phoneNumber, subscriptionName);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Insufficient funds or invalid subscription");
            log.error("Payment failed for user with phone number {} due to: {}", phoneNumber, e.getMessage());
        } catch (Exception e) {
            model.addAttribute("message", "Internal server error");
            log.error("Internal server error occurred during payment processing for user with phone number {}: {}", phoneNumber, e.getMessage());
        }
        return "pages/personal_office";
    }
}