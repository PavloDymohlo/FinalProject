package ua.project.FinalProject.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.service.MusicFileService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FreeSubscriptionControllerTest {
    @Mock
    private MusicFileService musicFileService;
    @InjectMocks
    private FreeSubscriptionController freeSubscriptionController;

    @Test
    public void FreeSubscriptionControllerTest_ShowFreeSubscriptionPage() {
        Model model = new ConcurrentModel();
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("80999874563");
        List<MusicFileEntity> musicFiles = Arrays.asList();
        Mockito.when(musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE)).thenReturn(musicFiles);
        String viewName = freeSubscriptionController.showFreeSubscriptionPage(model, "80999874563", principal);
        Assert.assertEquals("pages/free_subscription", viewName);
        Assert.assertTrue(model.containsAttribute("musicFiles"));
    }
}