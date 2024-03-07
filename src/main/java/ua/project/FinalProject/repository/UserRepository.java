package ua.project.FinalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.project.FinalProject.entity.UserEntity;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(long phoneNumber);

    UserEntity findByPhoneNumber(long phoneNumber);

    boolean existsByBankCardNumber(long bankCardNumber);
}