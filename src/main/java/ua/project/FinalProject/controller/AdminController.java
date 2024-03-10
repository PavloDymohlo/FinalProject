package ua.project.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.SubscriptionService;
import ua.project.FinalProject.service.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {


    private final UserService userService;


    private final SubscriptionService subscriptionService;


    private final MusicFileService musicFileService;

    @GetMapping("/{phoneNumber}")
    public String showAdminOffice(@PathVariable("phoneNumber") String phoneNumber, Model model, RedirectAttributes redirectAttributes) {
        log.info("Received request for admin office with phone number: {}", phoneNumber);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity currentUser = userService.getUserByPhoneNumber(Long.parseLong(userDetails.getUsername()));
            if (currentUser != null && userService.isAdminSubscription(currentUser.getPhoneNumber())) {
                try {
                    long phone = Long.parseLong(phoneNumber);
                    UserEntity user = userService.getUserByPhoneNumber(phone);
                    if (user != null) {
                        model.addAttribute("user", user);
                        return "pages/admin_office";
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "User not found");
                        return "redirect:/admin";
                    }
                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid phone number");
                    return "redirect:/admin";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access this page");
                return "redirect:/pages/personal_office";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/{phoneNumber}/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{phoneNumber}/users/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{phoneNumber}/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{phoneNumber}/users/updatePhoneNumber/{id}")
    public ResponseEntity<Void> updatePhoneNumber(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) {
        Long newPhoneNumber = requestBody.get("newPhoneNumber");
        userService.updatePhoneNumber(id, newPhoneNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{phoneNumber}/users/updatePassword/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("newPassword");
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{phoneNumber}/subscriptions")
    public ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions() {
        List<SubscriptionEntity> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/{phoneNumber}/subscriptions/{id}")
    public ResponseEntity<SubscriptionEntity> getSubscriptionById(@PathVariable Long id) {
        SubscriptionEntity subscription = subscriptionService.getSubscriptionById(id);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{phoneNumber}/musicFiles")
    public ResponseEntity<List<MusicFileEntity>> getAllMusicFiles() {
        List<MusicFileEntity> musicFiles = musicFileService.getAllMusicFiles();
        return ResponseEntity.ok(musicFiles);
    }

    @GetMapping("/{phoneNumber}/musicFiles/{id}")
    public ResponseEntity<MusicFileEntity> getMusicFileById(@PathVariable Long id) {
        MusicFileEntity musicFile = musicFileService.getMusicFileById(id).orElse(null);
        if (musicFile != null) {
            return ResponseEntity.ok(musicFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
