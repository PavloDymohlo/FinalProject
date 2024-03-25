package ua.project.FinalProject.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.service.mock.BankAccountEntity;
import ua.project.FinalProject.service.mock.BankAccountRepository;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.SubscriptionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import ua.project.FinalProject.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final SubscriptionRepository subscriptionRepository;

    public void handleExpiredSubscription(UserEntity user) {
        if (user.getAutoRenew() == AutoRenewStatus.YES) {
            SubscriptionEntity currentSubscription = user.getSubscription();
            BigDecimal subscriptionPrice = currentSubscription.getPrice();
            BankAccountEntity bankAccount = bankAccountRepository.findByBankCardNumber(user.getBankCardNumber());
            if (bankAccount != null && bankAccount.getBalance().compareTo(subscriptionPrice) >= 0) {
                log.info("Auto-renewal is enabled, sufficient funds found for subscription renewal.");
                bankAccount.setBalance(bankAccount.getBalance().subtract(subscriptionPrice));
                bankAccountRepository.save(bankAccount);
                user.setEndTime(LocalDateTime.now().plusMinutes(currentSubscription.getDurationMinutes()));
                userRepository.save(user);
                log.info("Subscription continued");
            } else {
                log.warn("Insufficient funds for subscription renewal, switching to free subscription.");
                switchSubscriptionToFree(user);
            }
        } else {
            log.info("Auto-renewal is disabled, switching to free subscription.");
            switchSubscriptionToFree(user);
        }
    }

    private void switchSubscriptionToFree(UserEntity user) {
        SubscriptionEntity freeSubscription = subscriptionRepository.findBySubscriptionEnum(SubscriptionEnum.FREE);
        if (freeSubscription == null) {
            throw new IllegalStateException("Free subscription not found");
        }
        user.setSubscription(freeSubscription);
        user.setEndTime(null);
        user.getSubscription().setId(freeSubscription.getId());
        userRepository.save(user);
        log.info("Subscription switched to free successfully.");
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public SubscriptionEntity getSubscriptionById(long subscriptionId) {
        Optional<SubscriptionEntity> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            return optionalSubscription.get();
        } else {
            log.error("Subscription with ID {} not found", subscriptionId);
            throw new IllegalArgumentException("Subscription not found");
        }
    }

    public List<SubscriptionEntity> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

}