package ua.project.FinalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        String phoneNumber = "80996325888";
        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(80996325888L)
                .bankCardNumber(1234567890L)
                .password("password")
                .build();
        userEntity.setSubscription(new SubscriptionEntity());

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(userEntity.getPhoneNumber()),
                userEntity.getPassword(),
                authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(userEntity);
        when(userService.isAdminSubscription(anyLong())).thenReturn(false);

        mockMvc.perform(get("/admin/{phoneNumber}", phoneNumber))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/pages/personal_office"));
    }

    @Test
    public void AdminControllerTest_GetAllUsers() throws Exception {

        List<UserEntity> users = new ArrayList<>();
        users.add(new UserEntity());
        users.add(new UserEntity());

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/{phoneNumber}/users", "80996325888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void AdminControllerTest_GetUserById_UserFound() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPhoneNumber(80996325888L);
        user.setBankCardNumber(1234567890123456L);
        user.setPassword("password");

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/admin/{phoneNumber}/users/{id}", user.getPhoneNumber(), userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    public void AdminControllerTest_GetUserById_UserNotFound() throws Exception {
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/admin/{phoneNumber}/users/{id}", "80996325888", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void AdminControllerTest_UpdatePhoneNumber() throws Exception {
        Long userId = 1L;
        Long newPhoneNumber = 80987654321L;
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("newPhoneNumber", newPhoneNumber);

        mockMvc.perform(put("/admin/{phoneNumber}/users/updatePhoneNumber/{id}", "80996325888", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestBody)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updatePhoneNumber(userId, newPhoneNumber);
    }

    @Test
    public void AdminControllerTest_GetAllSubscriptions() throws Exception {
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

        when(subscriptionService.getAllSubscriptions()).thenReturn(subscriptions);

        mockMvc.perform(get("/admin/{phoneNumber}/subscriptions", "80996325888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].subscriptionEnum", is("FREE")))
                .andExpect(jsonPath("$[0].price", is(0)))
                .andExpect(jsonPath("$[0].durationMinutes", is(30)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].subscriptionEnum", is("OPTIMAL")))
                .andExpect(jsonPath("$[1].price", is(10)))
                .andExpect(jsonPath("$[1].durationMinutes", is(60)));
    }

    @Test
    public void AdminControllerTest_GetSubscriptionById() throws Exception {
        Long id = 1L;
        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .id(id)
                .subscriptionEnum(SubscriptionEnum.FREE)
                .price(BigDecimal.valueOf(0))
                .durationMinutes(30)
                .build();

        when(subscriptionService.getSubscriptionById(id)).thenReturn(subscriptionEntity);

        mockMvc.perform(get("/admin/{phoneNumber}/subscriptions/{id}", "80996325888", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.subscriptionEnum", is("FREE")))
                .andExpect(jsonPath("$.price", is(0)))
                .andExpect(jsonPath("$.durationMinutes", is(30)));
    }

    @Test
    public void AdminControllerTest_GetAllMusicFiles() throws Exception {
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

        mockMvc.perform(get("/admin/{phoneNumber}/musicFiles", "80996325888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].musicFileName", is("first song")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].musicFileName", is("second song")));
    }

    @Test
    public void AdminControllerTest_GetMusicFileById() throws Exception {
        Long id = 1L;
        MusicFileEntity musicFileEntity = MusicFileEntity.builder()
                .id(id)
                .musicFileName("first song")
                .subscriptionEntity(SubscriptionEntity.builder().subscriptionEnum(SubscriptionEnum.MAXIMUM).build())
                .build();

        when(musicFileService.getMusicFileById(id)).thenReturn(Optional.ofNullable(musicFileEntity));

        mockMvc.perform(get("/admin/{phoneNumber}/musicFiles/{id}", "80996325888", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.musicFileName", is("first song")));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}