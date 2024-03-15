package ua.project.FinalProject.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.security.crypto.password.PasswordEncoder;
import ua.project.FinalProject.Enum.AutoRenewStatus;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.SubscriptionRepository;
import ua.project.FinalProject.repository.UserRepository;
import ua.project.FinalProject.validation.UserValidation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserValidation userValidation;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SubscriptionEntity subscriptionEntity;

    @Test
    public void UserService_RegisterUser() {
        long phoneNumber = 80633256897L;
        long bankCardNumber = 789523010025L;
        String password = "password";

        UserEntity user = new UserEntity();
        user.setPhoneNumber(phoneNumber);
        user.setBankCardNumber(bankCardNumber);
        user.setPassword(password);

        when(userValidation.doesUserExistByPhoneNumber(phoneNumber)).thenReturn(false);
        when(userValidation.isValidPhoneNumberFormat(phoneNumber)).thenReturn(true);
        when(userValidation.validateBankCardExists(bankCardNumber)).thenReturn(true);
        when(userValidation.validateBankCardNotLinked(bankCardNumber)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(password);

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .durationMinutes(0).build();
        when(subscriptionRepository.findBySubscriptionEnum(SubscriptionEnum.MAXIMUM)).thenReturn(subscriptionEntity);
        doNothing().when(userValidation).validateSubscriptionNotNull(any());

        when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(user);

        UserEntity savedUser = userService.registerUser(phoneNumber, bankCardNumber, password);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getPhoneNumber()).isEqualTo(phoneNumber);
        Assertions.assertThat(savedUser.getBankCardNumber()).isEqualTo(bankCardNumber);
        Assertions.assertThat(savedUser.getPassword()).isEqualTo(password);
        Assertions.assertThat(savedUser.getSubscription()).isNull();
        Assertions.assertThat(savedUser.getEndTime()).isNull();
        Assertions.assertThat(subscription.getDurationMinutes()).isEqualTo(0);
    }

    @Test
    public void UserService_GetUserByPhoneNumber() {

        long phoneNumber = 80996665532L;
        UserEntity expectedUser = new UserEntity();
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(expectedUser);

        UserEntity actualUser = userService.getUserByPhoneNumber(phoneNumber);

        assertEquals(expectedUser, actualUser);
        verify(userValidation, times(1)).validateUserNotNull(expectedUser);
    }

    @Test
    public void UserService_UpdatePhoneNumber() {
        long userId = 1L;
        long newPhoneNumber = 80505055050L;

        when(userValidation.isValidPhoneNumberFormat(newPhoneNumber)).thenReturn(true);
        when(userValidation.doesUserExistByPhoneNumber(newPhoneNumber)).thenReturn(false);

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(new SubscriptionEntity())
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updatePhoneNumber(userId, newPhoneNumber);

        verify(userValidation, times(1)).isValidPhoneNumberFormat(newPhoneNumber);
        verify(userValidation, times(1)).doesUserExistByPhoneNumber(newPhoneNumber);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userEntity);
        assertEquals(newPhoneNumber, userEntity.getPhoneNumber());
    }

    @Test
    public void UserService_DeleteUser() {
        long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void UserService_GetUserById() {
        long userId = 1L;
        UserEntity sampleUser = UserEntity.builder()
                .id(userId)
                .phoneNumber(80661234567L)
                .bankCardNumber(1234567890123456L)
                .password("test")
                .endTime(LocalDateTime.now().plusYears(1))
                .autoRenew(AutoRenewStatus.NO)
                .subscription(new SubscriptionEntity())
                .build();
        when(userValidation.getUserById(userId)).thenReturn(sampleUser);

        UserEntity resultUser = userService.getUserById(userId);

        Assertions.assertThat(resultUser).isNotNull();
        Assertions.assertThat(resultUser.getId()).isEqualTo(sampleUser.getId());
        Assertions.assertThat(resultUser.getPhoneNumber()).isEqualTo(sampleUser.getPhoneNumber());
        Assertions.assertThat(resultUser.getBankCardNumber()).isEqualTo(sampleUser.getBankCardNumber());
        Assertions.assertThat(resultUser.getPassword()).isEqualTo(sampleUser.getPassword());
        Assertions.assertThat(resultUser.getEndTime()).isEqualTo(sampleUser.getEndTime());
        Assertions.assertThat(resultUser.getAutoRenew()).isEqualTo(sampleUser.getAutoRenew());
        Assertions.assertThat(resultUser.getSubscription()).isEqualTo(sampleUser.getSubscription());
    }

    @Test
    public void UserService_GetAllUsers() {
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

        List<UserEntity> userList = List.of(firstUserEntity, secondUserEntity);
        when(userRepository.findAll()).thenReturn(userList);

        List<UserEntity> result = userService.getAllUsers();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).containsExactlyInAnyOrder(firstUserEntity, secondUserEntity);
    }
}