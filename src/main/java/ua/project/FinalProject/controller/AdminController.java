package ua.project.FinalProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
@Tag(name = "Admin Controller", description = "Operations related to administration")
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
                    log.debug("Searching for user with phone number: {}", phone);
                    UserEntity user = userService.getUserByPhoneNumber(phone);
                    if (user != null) {
                        model.addAttribute("user", user);
                        return "pages/admin_office";
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "User not found");
                        log.warn("User not found for phone number: {}", phone);
                        return "redirect:/admin";
                    }
                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid phone number");
                    log.error("Invalid phone number format: {}", phoneNumber);
                    return "redirect:/admin";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access this page");
                log.warn("Unauthorized access attempted by user: {}", userDetails.getUsername());
                return "redirect:/pages/personal_office";
            }
        } else {
            log.warn("Unauthorized access attempted. Redirecting to login page.");
            return "redirect:/login";
        }
    }
    @Operation(summary = "Get all users")
    @GetMapping("/{phoneNumber}/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        log.info("Retrieved {} users.", users.size());
        return ResponseEntity.ok(users);
    }
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{phoneNumber}/users/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        if (user != null) {
            log.info("Retrieved user with ID {}: {}", id, user);
            return ResponseEntity.ok(user);
        } else {
            log.warn("User not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/{phoneNumber}/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("User with ID {} has been successfully deleted.", id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Update user's phone number")
    @PutMapping("/{phoneNumber}/users/updatePhoneNumber/{id}")
    public ResponseEntity<Void> updatePhoneNumber(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) {
        Long newPhoneNumber = requestBody.get("newPhoneNumber");
        userService.updatePhoneNumber(id, newPhoneNumber);
        log.info("Phone number has been successfully updated for user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Get all subscriptions")
    @GetMapping("/{phoneNumber}/subscriptions")
    public ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions() {
        List<SubscriptionEntity> subscriptions = subscriptionService.getAllSubscriptions();
        log.info("Retrieved {} subscriptions.", subscriptions.size());
        return ResponseEntity.ok(subscriptions);
    }
    @Operation(summary = "Get subscription by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the subscription"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @GetMapping("/{phoneNumber}/subscriptions/{id}")
    public ResponseEntity<SubscriptionEntity> getSubscriptionById(@PathVariable Long id) {
        SubscriptionEntity subscription = subscriptionService.getSubscriptionById(id);
        if (subscription != null) {
            log.info("Retrieved subscription with ID {}: {}", id, subscription);
            return ResponseEntity.ok(subscription);
        } else {
            log.warn("Subscription not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Get all music files")
    @GetMapping("/{phoneNumber}/musicFiles")
    public ResponseEntity<List<MusicFileEntity>> getAllMusicFiles() {
        List<MusicFileEntity> musicFiles = musicFileService.getAllMusicFiles();
        log.info("Retrieved {} music files.", musicFiles.size());
        return ResponseEntity.ok(musicFiles);
    }
    @Operation(summary = "Get music file by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the music file"),
            @ApiResponse(responseCode = "404", description = "Music file not found")
    })
    @GetMapping("/{phoneNumber}/musicFiles/{id}")
    public ResponseEntity<MusicFileEntity> getMusicFileById(@PathVariable Long id) {
        MusicFileEntity musicFile = musicFileService.getMusicFileById(id).orElse(null);
        if (musicFile != null) {
            log.info("Retrieved music file with ID {}: {}", id, musicFile);
            return ResponseEntity.ok(musicFile);
        } else {
            log.warn("music file not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}