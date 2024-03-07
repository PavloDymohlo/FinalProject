package ua.project.FinalProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HostController {

    @GetMapping("/host_page")
    public String showHostPage() {
        return "pages/host_page";
    }
}