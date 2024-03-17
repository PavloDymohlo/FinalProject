package ua.project.FinalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.SubscriptionService;
import ua.project.FinalProject.service.UserService;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.hamcrest.Matchers.is;




@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private MusicFileService musicFileService;
    @MockBean
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void AdminControllerTest_ShowAdminOffice_Success() throws Exception {
        String phoneNumber = "80996325888";
        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80996325888L)
                .bankCardNumber(1234567890L)
                .password("password")
                .build();
        userEntity.setSubscription(new SubscriptionEntity());

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(userEntity.getPhoneNumber()),
                userEntity.getPassword(),
                authorities
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(userEntity);
        when(userService.isAdminSubscription(anyLong())).thenReturn(true);

        mockMvc.perform(get("/admin/{phoneNumber}", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/admin_office"));
    }

    @Test
    public void AdminControllerTest_ShowAdminOffice_AccessDenied() throws Exception {
        // Arrange
        String phoneNumber = "80996325888";
        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80996325888L)
                .bankCardNumber(1234567890L)
                .password("password")
                .build();
        userEntity.setSubscription(new SubscriptionEntity());

        // Створіть об'єкт SimpleGrantedAuthority для ролі "ROLE_USER" замість "ROLE_ADMIN"
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(userEntity.getPhoneNumber()),
                userEntity.getPassword(),
                authorities
        );

        // Створіть об'єкт Authentication з роллю "ROLE_USER" замість "ROLE_ADMIN"
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Встановити автентифікацію у контексті безпеки
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(userEntity);
        when(userService.isAdminSubscription(anyLong())).thenReturn(false); // Змінено на false, оскільки користувач не є адміном

        // Act and Assert
        mockMvc.perform(get("/admin/{phoneNumber}", phoneNumber))
                .andExpect(status().isFound()) // Очікується статус "302 Found" для перенаправлення
                .andExpect(redirectedUrl("/pages/personal_office")); // Очікується перенаправлення на сторінку особистого кабінету
    }
    @Test
    public void AdminControllerTest_GetAllUsers() throws Exception {
        // Arrange
        List<UserEntity> users = new ArrayList<>();
        users.add(new UserEntity());
        users.add(new UserEntity());

        when(userService.getAllUsers()).thenReturn(users);

        // Act and Assert
        mockMvc.perform(get("/admin/{phoneNumber}/users", "80996325888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // Очікується, що повернеться список з двома елементами
    }
    @Test
    public void AdminControllerTest_GetUserById_UserFound() throws Exception {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPhoneNumber(80996325888L);
        user.setBankCardNumber(1234567890123456L);
        user.setPassword("password");

        when(userService.getUserById(userId)).thenReturn(user);

        // Act and Assert
        mockMvc.perform(get("/admin/{phoneNumber}/users/{id}", "80996325888", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    public void AdminControllerTest_GetUserById_UserNotFound() throws Exception {
        // Arrange
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/admin/{phoneNumber}/users/{id}", "80996325888", userId))
                .andExpect(status().isNotFound());
    }
    @Test
    public void AdminControllerTest_UpdatePhoneNumber() throws Exception {
        // Arrange
        Long userId = 1L;
        Long newPhoneNumber = 80987654321L;
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("newPhoneNumber", newPhoneNumber);

        // Act and Assert
        mockMvc.perform(put("/admin/{phoneNumber}/users/updatePhoneNumber/{id}", "80996325888", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestBody)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updatePhoneNumber(userId, newPhoneNumber);
    }
    @Test
    public void AdminControllerTest_GetAllSubscriptions() throws Exception {
        // Arrange
        SubscriptionEntity subscription1 = SubscriptionEntity.builder()
                .id(1L)
                .subscriptionEnum(SubscriptionEnum.FREE)
                .price(BigDecimal.valueOf(0))
                .durationMinutes(30)
                .build();

        SubscriptionEntity subscription2 = SubscriptionEntity.builder()
                .id(2L)
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .price(BigDecimal.valueOf(10))
                .durationMinutes(60)
                .build();

        List<SubscriptionEntity> subscriptions = Arrays.asList(subscription1, subscription2);

        // Mock сервісу підписок, щоб повертав список підписок при виклику методу getAllSubscriptions()
        when(subscriptionService.getAllSubscriptions()).thenReturn(subscriptions);

        // Act and Assert
        mockMvc.perform(get("/admin/{phoneNumber}/subscriptions", "80996325888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Очікується, що відповідь міститиме масив з двома елементами
                .andExpect(jsonPath("$[0].id", is(1))) // Очікується, що перший елемент має id рівний 1
                .andExpect(jsonPath("$[0].subscriptionEnum", is("FREE"))) // Очікується, що перший елемент має підписку "FREE"
                .andExpect(jsonPath("$[0].price", is(0))) // Очікується, що перший елемент має ціну 0
                .andExpect(jsonPath("$[0].durationMinutes", is(30))) // Очікується, що перший елемент має тривалість 30 хвилин
                .andExpect(jsonPath("$[1].id", is(2))) // Очікується, що другий елемент має id рівний 2
                .andExpect(jsonPath("$[1].subscriptionEnum", is("OPTIMAL"))) // Очікується, що другий елемент має підписку "OPTIMAL"
                .andExpect(jsonPath("$[1].price", is(10))) // Очікується, що другий елемент має ціну 10
                .andExpect(jsonPath("$[1].durationMinutes", is(60))); // Очікується, що другий елемент має тривалість 60 хвилин
    }
    @Test
    public void AdminControllerTest_GetSubscriptionById() throws Exception {
        // Arrange
        Long id = 1L;
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .id(id)
                .subscriptionEnum(SubscriptionEnum.FREE)
                .price(BigDecimal.valueOf(0))
                .durationMinutes(30)
                .build();

        // Моделюємо повернення підписки з сервісу підписок
        when(subscriptionService.getSubscriptionById(id)).thenReturn(subscriptionEntity);

        // Act & Assert
        mockMvc.perform(get("/admin/{phoneNumber}/subscriptions/{id}", "80996325888", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1))) // Очікується, що підписка має id рівний 1
                .andExpect(jsonPath("$.subscriptionEnum", is("FREE"))) // Очікується, що підписка має тип "FREE"
                .andExpect(jsonPath("$.price", is(0))) // Очікується, що ціна підписки дорівнює 0
                .andExpect(jsonPath("$.durationMinutes", is(30))); // Очікується, що тривалість підписки дорівнює 30 хвилин
    }
    @Test
    public void AdminControllerTest_GetAllMusicFiles() throws Exception {
        // Arrange
        MusicFileEntity musicFileEntity1 = MusicFileEntity.builder()
                .id(1L)
                .musicFileName("first song")
                .subscriptionEntity(SubscriptionEntity.builder().subscriptionEnum(SubscriptionEnum.FREE).build())
                .build();

        MusicFileEntity musicFileEntity2 = MusicFileEntity.builder()
                .id(2L)
                .musicFileName("second song")
                .subscriptionEntity(SubscriptionEntity.builder().subscriptionEnum(SubscriptionEnum.OPTIMAL).build())
                .build();


        List<MusicFileEntity> musicFileList = Arrays.asList(musicFileEntity1, musicFileEntity2);

        when(musicFileService.getAllMusicFiles()).thenReturn(musicFileList);

        // Act and Assert
        mockMvc.perform(get("/admin/{phoneNumber}/musicFiles", "80996325888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Очікується, що відповідь міститиме масив з двома елементами
                .andExpect(jsonPath("$[0].id", is(1))) // Очікується, що перший елемент має id рівний 1
                .andExpect(jsonPath("$[0].musicFileName", is("first song"))) // Очікується, що перший елемент має ім'я "first song"
                .andExpect(jsonPath("$[1].id", is(2))) // Очікується, що другий елемент має id рівний 2
                .andExpect(jsonPath("$[1].musicFileName", is("second song"))); // Очікується, що другий елемент має ім'я "second song"
    }
    @Test
    public void AdminControllerTest_GetMusicFileById() throws Exception {
        // Arrange
        Long id = 1L;
        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .id(id)
                .musicFileName("first song")
                .subscriptionEntity(SubscriptionEntity.builder().subscriptionEnum(SubscriptionEnum.MAXIMUM).build())
                .build();

        when(musicFileService.getMusicFileById(id)).thenReturn(Optional.ofNullable(musicFileEntity));

        // Act & Assert
        mockMvc.perform(get("/admin/{phoneNumber}/musicFiles/{id}", "80996325888", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.musicFileName", is("first song"))); // Змінено на коректний JSON шлях для поля musicFileName
    }



    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
