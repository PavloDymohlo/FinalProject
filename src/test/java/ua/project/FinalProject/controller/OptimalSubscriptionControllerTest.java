package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.controller.OptimalSubscriptionController;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.UserService;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OptimalSubscriptionControllerTest {
    @Mock
    private MusicFileService musicFileService;

    @Mock
    private UserService userService;
    @Mock
    private UserEntity userEntity;

    @InjectMocks
    private OptimalSubscriptionController optimalSubscriptionController;

    @Test
    public void OptimalSubscriptionControllerTest_ShowOptimalSubscriptionPage() {
        Model model = new ConcurrentModel();
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password",
                Collections.singleton(() -> "ROLE_OPTIMAL"));
        UserEntity user = new UserEntity();
        user.setSubscription(new SubscriptionEntity(1L, SubscriptionEnum.OPTIMAL, BigDecimal.valueOf(10), 30, null, null));
        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(user);
        String viewName = optimalSubscriptionController.showOptimalSubscriptionPage("80502023658", model, authentication);
        assertEquals("pages/optimal_subscription", viewName);
        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE);
        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL);
    }
}