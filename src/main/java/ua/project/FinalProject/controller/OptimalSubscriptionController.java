package ua.project.FinalProject.controller;

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
public class OptimalSubscriptionController {
    private final MusicFileService musicFileService;
    private final UserService userService;

    @GetMapping("/optimal_subscription")
    public String showOptimalSubscriptionPage(@PathVariable String phoneNumber, Model model, Authentication authentication) {
        UserEntity user = userService.getUserByPhoneNumber(Long.parseLong(phoneNumber));
        if (user != null && user.getSubscription() != null &&
            (user.getSubscription().getSubscriptionEnum() == SubscriptionEnum.OPTIMAL ||
             user.getSubscription().getSubscriptionEnum() == SubscriptionEnum.MAXIMUM ||
             user.getSubscription().getSubscriptionEnum() == SubscriptionEnum.ADMIN ||
             authentication.getAuthorities().stream().anyMatch(grantedAuthority ->
                     grantedAuthority.getAuthority().equals("ROLE_OPTIMAL") ||
                     grantedAuthority.getAuthority().equals("ROLE_MAXIMUM") ||
                     grantedAuthority.getAuthority().equals("ROLE_ADMIN")))) {
            model.addAttribute("freeMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
            model.addAttribute("optimalMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL));
            log.info("User with phone number {} accessed optimal subscription page.", phoneNumber);
            return "pages/optimal_subscription";
        } else {
            log.warn("Unauthorized access to optimal subscription page attempted with phone number: {}", phoneNumber);
            return "redirect:/host_page";
        }
    }
}