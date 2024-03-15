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
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.UserService;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaximumSubscriptionControllerTest {
    @Mock
    private MusicFileService musicFileService;

    @Mock
    private UserService userService;
    @Mock
    private UserEntity userEntity;

    @InjectMocks
    private MaximumSubscriptionController maximumSubscriptionController;

    @Test
    public void MaximumSubscriptionControllerTest_ShowMaximumSubscriptionPage() {
        Model model = new ConcurrentModel();
        Authentication authentication = new UsernamePasswordAuthenticationToken("userEntity", "password",
                Collections.singleton(() -> "ROLE_MAXIMUM"));
        UserEntity userEntity = new UserEntity();
        userEntity.setSubscription(new SubscriptionEntity(1L, SubscriptionEnum.MAXIMUM, BigDecimal.valueOf(10), 30, null, null));
        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(userEntity);
        String viewName = maximumSubscriptionController.showMaximumSubscriptionPage("80635553325", model, authentication);
        assertEquals("pages/maximum_subscription", viewName);
        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE);
        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.OPTIMAL);
        verify(musicFileService, times(1)).getAllMusicFilesSortedBySubscription(SubscriptionEnum.MAXIMUM);
    }
}