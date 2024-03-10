//package ua.project.FinalProject.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ua.project.FinalProject.entity.UserEntity;
//import ua.project.FinalProject.repository.UserRepository;
//import ua.project.FinalProject.service.UserService;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/users")
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public List<UserEntity> getAllUsers() {
//        return userService.getAllUsers();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
//        UserEntity user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PutMapping("/updatePhoneNumber/{id}")
//    public ResponseEntity<Void> updatePhoneNumber(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) {
//        Long newPhoneNumber = requestBody.get("newPhoneNumber");
//        userService.updatePhoneNumber(id, newPhoneNumber);
//        return ResponseEntity.noContent().build();
//    }
//
//
//    @PutMapping("/updateBankCardNumber/{id}")
//    public ResponseEntity<Void> updateBankCardNumber(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) {
//        Long newBankCardNumber = requestBody.get("newBankCardNumber");
//        userService.updateBankCardNumber(id, newBankCardNumber);
//        return ResponseEntity.noContent().build();
//    }
//
//
//    @PutMapping("/updatePassword/{id}")
//    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
//        String newPassword = requestBody.get("newPassword");
//        userService.updatePassword(id, newPassword);
//        return ResponseEntity.noContent().build();
//    }
//}