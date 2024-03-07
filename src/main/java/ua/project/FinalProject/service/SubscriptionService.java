package ua.project.FinalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.bankData.entity.BankAccountEntity;
import ua.project.FinalProject.bankData.repository.BankAccountRepository;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.SubscriptionRepository;

import java.math.BigDecimal;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;

import ua.project.FinalProject.repository.UserRepository;

@Service
public class SubscriptionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public void handleExpiredSubscription(UserEntity user) {
        if (user.getAutoRenew() == AutoRenewStatus.YES) {
            SubscriptionEntity currentSubscription = user.getSubscription();
            BigDecimal subscriptionPrice = currentSubscription.getPrice();
            BankAccountEntity bankAccount = bankAccountRepository.findByBankCardNumber(user.getBankCardNumber());

            if (bankAccount != null && bankAccount.getBalance().compareTo(subscriptionPrice) >= 0) {
                bankAccount.setBalance(bankAccount.getBalance().subtract(subscriptionPrice));
                bankAccountRepository.save(bankAccount);
                user.setEndTime(LocalDateTime.now().plusMinutes(currentSubscription.getDurationMinutes()));
                userRepository.save(user);
                System.out.println("Subscription continued.");
            } else {
                switchSubscriptionToFree(user);
                System.out.println("Insufficient funds, switched to free subscription.");
            }
        } else {
            switchSubscriptionToFree(user);
            System.out.println("Auto-renewal is disabled, switched to free subscription.");
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
    }


    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    public void toggleAutoRenew(long phoneNumber, AutoRenewStatus autoRenewStatus) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        AutoRenewStatus currentStatus = user.getAutoRenew();
        if (currentStatus == AutoRenewStatus.YES) {
            user.setAutoRenew(AutoRenewStatus.NO);
        } else {
            user.setAutoRenew(AutoRenewStatus.YES);
        }

        userRepository.save(user);
    }
    public SubscriptionEntity addSubscription(SubscriptionEntity subscription) {
        return subscriptionRepository.save(subscription);
    }

    // Оновлення назви підписки
    public SubscriptionEntity updateSubscriptionName(long subscriptionId, String newName) {
        Optional<SubscriptionEntity> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            SubscriptionEntity subscription = optionalSubscription.get();
            subscription.setSubscriptionEnum(SubscriptionEnum.valueOf(newName));
            return subscriptionRepository.save(subscription);
        } else {
            throw new IllegalArgumentException("Subscription not found");
        }
    }

    // Видалення підписки
    public void deleteSubscription(long subscriptionId) {
        Optional<SubscriptionEntity> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            subscriptionRepository.deleteById(subscriptionId);
        } else {
            throw new IllegalArgumentException("Subscription not found");
        }
    }

    // Отримання підписки по айді
    public SubscriptionEntity getSubscriptionById(long subscriptionId) {
        Optional<SubscriptionEntity> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            return optionalSubscription.get();
        } else {
            throw new IllegalArgumentException("Subscription not found");
        }
    }

    // Отримання всіх підписок
    public List<SubscriptionEntity> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
    public SubscriptionEntity getSubscriptionByName(String subscriptionName) {
        return subscriptionRepository.findBySubscriptionEnum(SubscriptionEnum.valueOf(subscriptionName));
    }
}
