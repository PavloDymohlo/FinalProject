package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.UserService;

import java.util.Collections;

import static org.mockito.Mockito.*;

@WebMvcTest(OptimalSubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class OptimalSubscriptionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicFileService musicFileService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Test
    void OptimalSubscriptionControllerTest_ReturnsOptimalSubscriptionPage() throws Exception {
        String phoneNumber = "80665559899";
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password",
                Collections.singletonList(() -> "ROLE_OPTIMAL"));

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .subscriptionEnum(SubscriptionEnum.OPTIMAL)
                .build();

        UserEntity user = UserEntity.builder()
                .phoneNumber(80665559899L)
                .subscription(subscription)
                .build();

        when(userService.getUserByPhoneNumber(any(Long.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/personal_office/{phoneNumber}/optimal_subscription", phoneNumber)
                        .principal(authentication))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("pages/optimal_subscription"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("freeMusicFiles"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("optimalMusicFiles"));

        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE);
        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL);
    }

    @Test
    void OptimalSubscriptionControllerTest_ReturnsRedirectToHostPage() throws Exception {
        String phoneNumber = "80665559899";
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password",
                Collections.singletonList(() -> "ROLE_FREE"));
        when(userService.getUserByPhoneNumber(any(Long.class)))
                .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/personal_office/{phoneNumber}/optimal_subscription", phoneNumber)
                        .principal(authentication))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/host_page"));

        verify(musicFileService, never()).getAllMusicFilesSortedBySubscription(any(SubscriptionEnum.class));
    }
}