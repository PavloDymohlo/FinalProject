package ua.project.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.service.MusicFileService;


@Controller
public class FreeSubscriptionController {
    @Autowired
    private  MusicFileService musicFileService;


    @GetMapping("/free_subscription")
    public String showFreeSubscriptionPage(Model model) {
        model.addAttribute("musicFiles", musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE));
        return "pages/free_subscription";
    }
}

