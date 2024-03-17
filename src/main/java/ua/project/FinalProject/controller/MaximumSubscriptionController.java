package ua.project.FinalProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/personal_office/{phoneNumber}")
@Tag(name = "Maximum Subscription Controller", description = "Operations related to maximum subscriptions")
public class MaximumSubscriptionController {
    private final MusicFileService musicFileService;
    private final UserService userService;

    @Operation(summary = "Show maximum subscription page")
    @GetMapping("/maximum_subscription")
    public String showMaximumSubscriptionPage(@PathVariable String phoneNumber, Model model, Authentication authentication) {
        UserEntity user = userService.getUserByPhoneNumber(Long.parseLong(phoneNumber));
        if (user != null && user.getSubscription() != null &&
            (user.getSubscription().getSubscriptionEnum() == SubscriptionEnum.MAXIMUM ||
             user.getSubscription().getSubscriptionEnum() == SubscriptionEnum.ADMIN ||
             authentication.getAuthorities().stream().anyMatch(grantedAuthority ->
                     grantedAuthority.getAuthority().equals("ROLE_MAXIMUM") ||
                     grantedAuthority.getAuthority().equals("ROLE_ADMIN")))) {
            model.addAttribute("freeMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
            model.addAttribute("optimalMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL));
            model.addAttribute("maximumMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.MAXIMUM));
            log.info("User with phone number {} accessed maximum subscription page.", phoneNumber);
            return "pages/maximum_subscription";
        } else {
            log.warn("Unauthorized access to maximum subscription page attempted with phone number: {}", phoneNumber);
            return "redirect:/host_page";
        }
    }
}