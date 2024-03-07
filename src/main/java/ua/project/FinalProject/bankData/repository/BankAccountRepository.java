package ua.project.FinalProject.bankData.repository;

import ua.project.FinalProject.bankData.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
    boolean existsByBankCardNumber(long bankCardNumber);
    BankAccountEntity findByBankCardNumber(long bankCardNumber);
}