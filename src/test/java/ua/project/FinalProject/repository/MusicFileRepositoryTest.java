package ua.project.FinalProject.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MusicFileRepositoryTest {
    @Autowired
    private MusicFileRepository musicFileRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    private SubscriptionEnum subscriptionEnum;

    @Test
    public void MusicFileRepository_CreateMusicFile() {
        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song")
                .subscriptionEntity(new SubscriptionEntity())
                .build();
        MusicFileEntity saveMusicFile = musicFileRepository.save(musicFileEntity);

        Assertions.assertThat(saveMusicFile).isNotNull();
        Assertions.assertThat(saveMusicFile.getId()).isGreaterThan(0);
    }

    @Test
    public void MusicFileRepository_GetAll() {
        SubscriptionEntity firstSubscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedFirstSubscription = subscriptionRepository.save(firstSubscriptionEntity);

        SubscriptionEntity secondSubscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.MAXIMUM)
                .price(BigDecimal.valueOf(15))
                .durationMinutes(90)
                .build();
        SubscriptionEntity savedSecondSubscription = subscriptionRepository.save(secondSubscriptionEntity);

        MusicFileEntity firstMusicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song 1")
                .subscriptionEntity(savedFirstSubscription)
                .build();
        musicFileRepository.save(firstMusicFileEntity);

        MusicFileEntity secondMusicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song 2")
                .subscriptionEntity(savedSecondSubscription)
                .build();
        musicFileRepository.save(secondMusicFileEntity);

        List<MusicFileEntity> musicEntityList = musicFileRepository.findAll();

        Assertions.assertThat(musicEntityList).isNotNull();
        Assertions.assertThat(musicEntityList.size()).isEqualTo(2);
    }

    @Test
    public void MusicFileRepository_FindById() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song")
                .subscriptionEntity(savedSubscription)
                .build();
        musicFileRepository.save(musicFileEntity);

        MusicFileEntity findMusicEntity = musicFileRepository.getById(musicFileEntity.getId());

        Assertions.assertThat(findMusicEntity).isNotNull();
    }

    @Test
    public void MusicFileRepository_FindBySubscriptionEntity() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song")
                .subscriptionEntity(savedSubscription)
                .build();
        musicFileRepository.save(musicFileEntity);

        List<MusicFileEntity> musicFiles = musicFileRepository.findBySubscriptionEntity(savedSubscription);
        Assertions.assertThat(musicFiles).isNotNull();
        Assertions.assertThat(musicFiles).hasSizeGreaterThan(0);
    }

    @Test
    public void MusicFileRepository_UpdateMusicFile() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song")
                .subscriptionEntity(savedSubscription)
                .build();
        musicFileRepository.save(musicFileEntity);
        MusicFileEntity findMusicFile = musicFileRepository.findById(musicFileEntity.getId()).get();
        findMusicFile.setMusicFileName("very cool song");
        MusicFileEntity updateMusicFile = musicFileRepository.save(findMusicFile);
        Assertions.assertThat(updateMusicFile).isNotNull();
    }

    @Test
    public void MusicFileRepository_DeleteMusicFile() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .musicFileName("Cool song")
                .subscriptionEntity(savedSubscription)
                .build();
        musicFileRepository.save(musicFileEntity);
        musicFileRepository.deleteById(musicFileEntity.getId());
        Optional<MusicFileEntity> musicFileEntityReturn = musicFileRepository.findById(musicFileEntity.getId());
        Assertions.assertThat(musicFileEntityReturn).isEmpty();
    }
}