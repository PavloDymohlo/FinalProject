package ua.project.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.service.MusicFileService;

import java.util.List;
@Controller
public class MaximumSubscriptionController {
    @Autowired
    private  MusicFileService musicFileService;

    @GetMapping("/maximum_subscription")
    public String showMaximumSubscriptionPage(Model model) {
        model.addAttribute("freeMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
        model.addAttribute("optimalMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL));
        model.addAttribute("maximumMusicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.MAXIMUM));
        return "pages/maximum_subscription";
    }
}
