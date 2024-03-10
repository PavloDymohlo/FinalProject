package ua.project.FinalProject.service.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankAccountRepository bankAccountRepository;

    public boolean checkBankCard(long bankCardNumber, BigDecimal amount) {
        return bankAccountRepository.existsByBankCardNumber(bankCardNumber);
    }

    @Transactional(transactionManager = "bankTransactionManager")
    public boolean withdrawalOfMoney(String bankCardNumber, BigDecimal amount) {
        long cardNumber = Long.parseLong(bankCardNumber);
        if (bankAccountRepository.existsByBankCardNumber(cardNumber)) {
            BankAccountEntity bankAccount = bankAccountRepository.findByBankCardNumber(cardNumber);

            BigDecimal currentBalance = bankAccount.getBalance();
            if (currentBalance.compareTo(amount) >= 0) {
                BigDecimal newBalance = currentBalance.subtract(amount);
                bankAccount.setBalance(newBalance);
                bankAccountRepository.save(bankAccount);
                return true;
            } else {
                throw new RuntimeException("Insufficient funds");
            }
        } else {
            throw new RuntimeException("Bank card not found");
        }
    }
}