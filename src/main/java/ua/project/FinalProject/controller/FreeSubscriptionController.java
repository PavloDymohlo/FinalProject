package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/personal_office/{phoneNumber}")
public class FreeSubscriptionController {
    private final MusicFileService musicFileService;

    @GetMapping("/free_subscription")
    public String showFreeSubscriptionPage(Model model, @PathVariable String phoneNumber, Principal principal) {
        if (principal != null && principal.getName().equals(phoneNumber)) {
            model.addAttribute("musicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
            return "pages/free_subscription";
        } else {
            return "redirect:/host_page";
        }
    }
}