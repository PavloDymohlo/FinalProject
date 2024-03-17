package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(PersonalOfficeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PersonalOfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;

    @InjectMocks
    private PersonalOfficeController personalOfficeController;

    @Test
    public void PersonalOfficeControllerTest_ValidPhoneNumber() throws Exception {
        long validPhoneNumber = 80978856999L;
        UserEntity user = new UserEntity();
        user.setPhoneNumber(validPhoneNumber);

        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/personal_office/{phoneNumber}", validPhoneNumber))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("pages/personal_office"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user));
    }
}