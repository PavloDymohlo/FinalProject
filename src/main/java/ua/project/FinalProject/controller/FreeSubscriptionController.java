package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.service.MusicFileService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/personal_office/{phoneNumber}")
public class FreeSubscriptionController {
    private final MusicFileService musicFileService;

    @GetMapping("/free_subscription")
    public String showFreeSubscriptionPage(Model model, @PathVariable String phoneNumber, Principal principal) {
        if (principal != null && principal.getName().equals(phoneNumber)) {
            model.addAttribute("musicFiles", musicFileService
                    .getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
            log.info("User with phone number {} accessed free subscription page.", phoneNumber);
            return "pages/free_subscription";
        } else {
            log.warn("Unauthorized access to free subscription page attempted with phone number: {}", phoneNumber);
            return "redirect:/host_page";
        }
    }
}