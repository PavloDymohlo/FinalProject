package ua.project.FinalProject.repository;

import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    SubscriptionEntity findBySubscriptionEnum(SubscriptionEnum subscriptionEnum);
}