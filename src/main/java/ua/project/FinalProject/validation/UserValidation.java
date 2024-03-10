package ua.project.FinalProject.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.project.FinalProject.service.mock.BankAccountRepository;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.UserRepository;

@Component
public class UserValidation {

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionEntity subscriptionEntity;

    public boolean isValidPhoneNumberFormat(long phoneNumber) {
        String phoneRegex = "^80(?:50|66|95|99|67|68|96|97|98|63|73|93)\\d{7}$";
        return String.valueOf(phoneNumber).matches(phoneRegex);
    }

    public boolean isValidBankCardNumberFormat(long bankCardNumber) {
        String cardRegex = "^\\d{16}$"; // Перевірка на 16 цифр
        return String.valueOf(bankCardNumber).matches(cardRegex);
    }

    public boolean doesUserExistByPhoneNumber(long phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean doesUserExistByBankCardNumber(long bankCardNumber) {
        return userRepository.existsByBankCardNumber(bankCardNumber);
    }

    public boolean validateBankCardExists(long bankCardNumber) {
        return bankAccountRepository.existsByBankCardNumber(bankCardNumber);
    }

    public boolean validateBankCardNotLinked(long bankCardNumber) {
      return userRepository.existsByBankCardNumber(bankCardNumber);
    }

    public void validateSubscriptionNotNull(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null) {
            throw new IllegalStateException("Maximum subscription not found");
        }
    }
    public void validateUserNotNull(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("User with provided phone number does not exist");
        }
    }
    public UserEntity getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}