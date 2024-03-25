package ua.project.FinalProject.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    private SubscriptionEnum subscriptionEnum;

    @Test
    public void UserRepository_CreateUser() {
        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(new SubscriptionEntity())
                .build();
        UserEntity saveUser = userRepository.save(userEntity);

        Assertions.assertThat(saveUser).isNotNull();
        Assertions.assertThat(saveUser.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_GetAll() {
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

        UserEntity firstUserEntity = UserEntity.builder()
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(savedFirstSubscription)
                .build();

        UserEntity secondUserEntity = UserEntity.builder()
                .phoneNumber(80661234599L)
                .bankCardNumber(1234567895523456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(savedSecondSubscription)
                .build();

        userRepository.save(firstUserEntity);
        userRepository.save(secondUserEntity);
        List<UserEntity> userEntityList = userRepository.findAll();

        Assertions.assertThat(userEntityList).isNotNull();
        Assertions.assertThat(userEntityList.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_FindById() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(savedSubscription)
                .build();

        userRepository.save(userEntity);
        UserEntity findUserEntity = userRepository.findById(userEntity.getId()).get();

        Assertions.assertThat(findUserEntity).isNotNull();
    }

    @Test
    public void UserRepository_FindByPhoneNumber() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(savedSubscription)
                .build();

        userRepository.save(userEntity);
        UserEntity findUserEntity = userRepository.findByPhoneNumber(userEntity.getPhoneNumber());

        Assertions.assertThat(findUserEntity).isNotNull();
    }

    @Test
    public void UserRepository_UpdateUser() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(savedSubscription)
                .build();

        userRepository.save(userEntity);
        UserEntity findUserEntity = userRepository.findById(userEntity.getId()).get();
        findUserEntity.setPhoneNumber(80505055050L);
        findUserEntity.setBankCardNumber(8975658301221452L);
        findUserEntity.setPassword("1234");

        UserEntity updateUser = userRepository.save(findUserEntity);

        Assertions.assertThat(updateUser.getPhoneNumber()).isNotNull();
        Assertions.assertThat(updateUser.getBankCardNumber()).isNotNull();
        Assertions.assertThat(updateUser.getPassword()).isNotNull();
    }

    @Test
    public void UserRepository_DeleteUser() {
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscriptionEntity);

        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(savedSubscription)
                .build();

        userRepository.save(userEntity);
        userRepository.deleteById(userEntity.getId());
        Optional<UserEntity> userEntityReturn = userRepository.findById(userEntity.getId());
        Assertions.assertThat(userEntityReturn).isEmpty();
    }
}