package ua.project.FinalProject.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionScheduler {

    @Autowired
    private SubscriptionService subscriptionService;

    @Scheduled(fixedRate = 60000)
    public void checkSubscriptionExpiration() {
        List<UserEntity> users = subscriptionService.getAllUsers();
        for (UserEntity user : users) {
            LocalDateTime endTime = user.getEndTime();
            if (endTime != null && endTime.isBefore(LocalDateTime.now())) {
                subscriptionService.handleExpiredSubscription(user);
                System.out.println("update");
            }
        }
    }
}
