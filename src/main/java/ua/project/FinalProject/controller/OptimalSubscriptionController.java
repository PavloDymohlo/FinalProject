package ua.project.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.service.MusicFileService;
@Controller
public class OptimalSubscriptionController {
    @Autowired
    private MusicFileService musicFileService;


    @GetMapping("/optimal_subscription")
    public String showFreeSubscriptionPage(Model model) {
        model.addAttribute("freeMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
        model.addAttribute("optimalMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL));

        return "pages/optimal_subscription";
    }
}

