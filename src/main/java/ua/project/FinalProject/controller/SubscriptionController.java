//package ua.project.FinalProject.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import ua.project.FinalProject.Enum.AutoRenewStatus;
//import ua.project.FinalProject.entity.SubscriptionEntity;
//import ua.project.FinalProject.service.SubscriptionService;
//
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/subscription")
//public class SubscriptionController {
//
//    @Autowired
//    private SubscriptionService subscriptionService;
//
//    @PostMapping("/toggleAutoRenew")
//    @ResponseBody
//    public ResponseEntity<String> toggleAutoRenew(@RequestBody Map<String, String> requestParams) {
//        String phoneNumber = requestParams.get("phoneNumber");
//        String autoRenew = requestParams.get("autoRenew");
//
//        try {
//            subscriptionService.toggleAutoRenew(Long.parseLong(phoneNumber), AutoRenewStatus.valueOf(autoRenew));
//            return ResponseEntity.ok().body("Зміна збережена успішно");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка при збереженні зміни: " + e.getMessage());
//        }
//    }
//    @GetMapping("/all")
//    public ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions() {
//        List<SubscriptionEntity> subscriptions = subscriptionService.getAllSubscriptions();
//        return ResponseEntity.ok(subscriptions);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SubscriptionEntity> getSubscriptionById(@PathVariable Long id) {
//        SubscriptionEntity subscription = subscriptionService.getSubscriptionById(id);
//        return ResponseEntity.ok(subscription);
//    }
//}
