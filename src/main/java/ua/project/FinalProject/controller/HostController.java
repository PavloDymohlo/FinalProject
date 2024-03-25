package ua.project.FinalProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@Tag(name = "Host Controller", description = "Operations related to the host page")
public class HostController {
    @Operation(summary = "Show host page")
    @GetMapping("/host_page")
    public String showHostPage() {
        log.info("Host page accessed.");
        return "pages/host_page";
    }
}