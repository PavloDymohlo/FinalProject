package ua.project.FinalProject.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.UserService;

import java.util.Collections;

import static org.mockito.Mockito.when;


@WebMvcTest(controllers = FreeSubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class FreeSubscriptionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MusicFileService musicFileService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;

    @Test
    public void testShowFreeSubscriptionPage_WithAuthorizedUser() throws Exception {
        String phoneNumber = "80633214588";
        when(musicFileService.getAllMusicFilesSortedBySubscription(SubscriptionEnum.FREE))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/personal_office/" + phoneNumber + "/free_subscription")
                        .principal(() -> phoneNumber))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("pages/free_subscription"));
    }


    @Test
    public void FreeSubscriptionControllerTest_ShowFreeSubscriptionPage_WithUnauthorizedUser() throws Exception {
        String phoneNumber = "80502365877";
        mockMvc.perform(MockMvcRequestBuilders.get("/personal_office/" + phoneNumber + "/free_subscription"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/host_page"));
    }
}