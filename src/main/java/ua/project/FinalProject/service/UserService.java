package ua.project.FinalProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.service.mock.BankAccountEntity;
import ua.project.FinalProject.service.mock.BankAccountRepository;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.SubscriptionRepository;
import ua.project.FinalProject.repository.UserRepository;
import org.springframework.stereotype.Service;
import ua.project.FinalProject.security.CustomUserDetailsService;
import ua.project.FinalProject.validation.UserValidation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final CustomUserDetailsService customUserDetailsService;

    public UserEntity registerUser(long phoneNumber, long bankCardNumber, String password) {
        if (userValidation.doesUserExistByPhoneNumber(phoneNumber)) {
            log.error("Phone number {} already exists", phoneNumber);
            throw new IllegalArgumentException("Phone number already exists");
        }
        if (!userValidation.isValidPhoneNumberFormat(phoneNumber)) {
            log.error("Invalid phone number format: {}", phoneNumber);
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (!userValidation.validateBankCardExists(bankCardNumber)) {
            log.error("Bank card number {} does not exist", bankCardNumber);
            throw new IllegalArgumentException("Bank card number does not exist");
        }
        ;
        if (userValidation.validateBankCardNotLinked(bankCardNumber)) {
            log.error("Bank card number {} already linked to another user", bankCardNumber);
            throw new IllegalArgumentException("Bank card number already linked to another user");
        }
        ;
        SubscriptionEntity subscriptionEntity = subscriptionRepository
                .findBySubscriptionEnum(SubscriptionEnum.MAXIMUM);
        userValidation.validateSubscriptionNotNull(subscriptionEntity);
        UserEntity user = new UserEntity();
        user.setPhoneNumber(phoneNumber);
        user.setBankCardNumber(bankCardNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setSubscription(subscriptionEntity);
        user.setEndTime(LocalDateTime.now().plusMinutes(subscriptionEntity.getDurationMinutes()));
        return userRepository.save(user);
    }

    public String loginIn(long phoneNumber, String password) {
        if (!userValidation.isValidPhoneNumberFormat(phoneNumber)) {
            log.error("Invalid phone number format: {}", phoneNumber);
            throw new IllegalArgumentException("Invalid phone number format");
        }
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        userValidation.validateUserNotNull(user);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Incorrect password for user with phone number: {}", phoneNumber);
            throw new IllegalArgumentException("Incorrect password");
        }
        log.info("User login successful: {}", phoneNumber);
        return "success";
    }

    public UserEntity getUserByPhoneNumber(long phoneNumber) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        userValidation.validateUserNotNull(user);
        log.info("User with phone number {} found: {}", phoneNumber, user);
        return user;
    }

    @Transactional
    public void subscriptionPayment(long phoneNumber, String subscriptionName) {
        userValidation.validateUserNotNull(userRepository.findByPhoneNumber(phoneNumber));
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        BankAccountEntity bankAccount = bankAccountRepository.findByBankCardNumber(user.getBankCardNumber());
        if (bankAccount == null) {
            log.error("User's bank account not found for user with phone number: {}", phoneNumber);
            throw new IllegalArgumentException("User's bank account not found");
        }
        SubscriptionEntity newSubscription = subscriptionRepository.findBySubscriptionEnum(SubscriptionEnum.valueOf(subscriptionName));
        if (newSubscription == null) {
            log.error("Subscription {} not found", subscriptionName);
            throw new IllegalArgumentException("Subscription not found");
        }
        BigDecimal subscriptionPrice = newSubscription.getPrice();
        BigDecimal userBalance = bankAccount.getBalance();
        if (userBalance.compareTo(subscriptionPrice) < 0) {
            log.error("Insufficient funds for the subscription for user with phone number: {}", phoneNumber);
            throw new IllegalArgumentException("Insufficient funds for the subscription");
        }
        SubscriptionEntity currentSubscription = user.getSubscription();
        LocalDateTime endTime;
        if (currentSubscription != null && currentSubscription.equals(newSubscription)) {
            endTime = user.getEndTime().plusMinutes(newSubscription.getDurationMinutes());
        } else {
            endTime = LocalDateTime.now().plusMinutes(newSubscription.getDurationMinutes());
            user.setSubscription(newSubscription);
        }
        BigDecimal newBalance = userBalance.subtract(subscriptionPrice);
        bankAccount.setBalance(newBalance);
        bankAccountRepository.save(bankAccount);
        user.setEndTime(endTime);
        userRepository.save(user);
        log.info("Subscription payment processed successfully for user with phone number: {}", phoneNumber);
    }

    public void setAutoRenew(long phoneNumber, AutoRenewStatus status) {
        userValidation.validateUserNotNull(userRepository.findByPhoneNumber(phoneNumber));
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        user.setAutoRenew(status);
        userRepository.save(user);
        log.info("Auto renew status set successfully for user with phone number: {}", phoneNumber);
    }

    @Transactional
    public void updatePhoneNumber(long userId, long newPhoneNumber) {
        if (!userValidation.isValidPhoneNumberFormat(newPhoneNumber)) {
            log.error("Invalid phone number format: {}", newPhoneNumber);
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (userValidation.doesUserExistByPhoneNumber(newPhoneNumber)) {
            log.error("Phone number {} already exists", newPhoneNumber);
            throw new IllegalArgumentException("Phone number already exists");
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found");
                });
        user.setPhoneNumber(newPhoneNumber);
        userRepository.save(user);
        log.info("Phone number updated successfully for user with ID: {}", userId);
    }
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public UserEntity getUserById(long userId) {
        return userValidation.getUserById(userId);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDetailsService userDetailsService() {
        return this::getUserByPhoneNumber;
    }

    private UserDetails getUserByPhoneNumber(String phoneNumber) {
        long phoneNumberValue = Long.parseLong(phoneNumber);
        return customUserDetailsService.loadUserByUsername(String.valueOf(phoneNumberValue));
    }

    public boolean isAdminSubscription(long phoneNumber) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            log.error("User not found with phone number: {}", phoneNumber);
            throw new IllegalArgumentException("User not found");
        }
        SubscriptionEntity subscription = user.getSubscription();
        if (subscription == null) {
            log.info("User with phone number {} does not have any subscription", phoneNumber);
            return false;
        }
        return subscription.getSubscriptionEnum() == SubscriptionEnum.ADMIN;
    }
}