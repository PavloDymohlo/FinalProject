package ua.project.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerUser(@RequestParam long phoneNumber, @RequestParam long bankCardNumber, @RequestParam String password) {
        UserEntity newUser = userService.registerUser(phoneNumber, bankCardNumber, password);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam long phoneNumber, @RequestParam String password) {
        String result = userService.loginIn(phoneNumber, password);
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/updatePhoneNumber")
    public ResponseEntity<Void> updatePhoneNumber(@PathVariable Long id, @RequestParam long newPhoneNumber) {
        userService.updatePhoneNumber(id, newPhoneNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/updateBankCardNumber")
    public ResponseEntity<Void> updateBankCardNumber(@PathVariable Long id, @RequestParam long newBankCardNumber) {
        userService.updateBankCardNumber(id, newBankCardNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/updatePassword")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }
}