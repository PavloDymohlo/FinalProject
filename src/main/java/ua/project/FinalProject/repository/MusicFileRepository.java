package ua.project.FinalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;

import java.util.List;

@Repository
public interface MusicFileRepository extends JpaRepository<MusicFileEntity, Long> {
    List<MusicFileEntity> findBySubscriptionEntity(SubscriptionEntity subscriptionEntity);
}
