package ua.project.FinalProject.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.bankData.entity.BankAccountEntity;
import ua.project.FinalProject.bankData.repository.BankAccountRepository;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.SubscriptionRepository;
import ua.project.FinalProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity registerUser(long phoneNumber, long bankCardNumber, String password) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        String phoneRegex = "^80(?:50|66|95|99|67|68|96|97|98|63|73|93)\\d{7}$";
        if (!String.valueOf(phoneNumber).matches(phoneRegex)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (!bankAccountRepository.existsByBankCardNumber(bankCardNumber)) {
            throw new IllegalArgumentException("Bank card number does not exist");
        }
        if (userRepository.existsByBankCardNumber(bankCardNumber)) {
            throw new IllegalArgumentException("Bank card number already linked to another user");
        }
        SubscriptionEntity subscriptionEntity = subscriptionRepository
                .findBySubscriptionEnum(SubscriptionEnum.valueOf(String.valueOf(SubscriptionEnum.MAXIMUM)));
        if (subscriptionEntity == null) {
            throw new IllegalStateException("Maximum subscription not found");
        }
            UserEntity user = new UserEntity();
            user.setPhoneNumber(phoneNumber);
            user.setBankCardNumber(bankCardNumber);
            user.setPassword(passwordEncoder.encode(password));
            user.setSubscription(subscriptionEntity);
            user.setEndTime(LocalDateTime.now().plusMinutes(subscriptionEntity.getDurationMinutes()));
            return userRepository.save(user);
        }

    public String loginIn(long phoneNumber, String password) {
        String phoneRegex = "^80(?:50|66|95|99|67|68|96|97|98|63|73|93)\\d{7}$";
        if (!String.valueOf(phoneNumber).matches(phoneRegex)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new IllegalArgumentException("User with provided phone number does not exist");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        return "success";
    }
    public UserEntity getUserByPhoneNumber(long phoneNumber) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new IllegalArgumentException("User with provided phone number does not exist");
        }
        return user;
    }

    @Transactional
    public void subscriptionPayment(long phoneNumber, String subscriptionName) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new IllegalArgumentException("User with the provided phone number not found");
        }

        BankAccountEntity bankAccount = bankAccountRepository.findByBankCardNumber(user.getBankCardNumber());
        if (bankAccount == null) {
            throw new IllegalArgumentException("User's bank account not found");
        }

        SubscriptionEntity newSubscription = subscriptionRepository.findBySubscriptionEnum(SubscriptionEnum.valueOf(subscriptionName));
        if (newSubscription == null) {
            throw new IllegalArgumentException("Subscription not found");
        }

        BigDecimal subscriptionPrice = newSubscription.getPrice();
        BigDecimal userBalance = bankAccount.getBalance();

        if (userBalance.compareTo(subscriptionPrice) < 0) {
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
    }
    public void setAutoRenew(long phoneNumber, AutoRenewStatus status) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setAutoRenew(status);
        userRepository.save(user);
    }
    @Transactional
    public void updatePhoneNumber(long userId, long newPhoneNumber) {
        // Перевіряємо правильність формату нового номеру телефону
        String phoneRegex = "^80(?:50|66|95|99|67|68|96|97|98|63|73|93)\\d{7}$";
        if (!String.valueOf(newPhoneNumber).matches(phoneRegex)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        // Перевіряємо наявність нового номеру телефону в базі даних
        if (userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPhoneNumber(newPhoneNumber);
        userRepository.save(user);
    }

    @Transactional
    public void updateBankCardNumber(long userId, long newBankCardNumber) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Перевірка чи існує банківська картка в базі даних
        if (!bankAccountRepository.existsByBankCardNumber(newBankCardNumber)) {
            throw new IllegalArgumentException("Bank card number does not exist");
        }

        // Перевірка чи банківська картка вже пов'язана з іншим користувачем
        if (userRepository.existsByBankCardNumber(newBankCardNumber)) {
            throw new IllegalArgumentException("Bank card number already linked to another user");
        }

        user.setBankCardNumber(newBankCardNumber);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(long userId, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public UserEntity getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}