package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonalOfficeControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserEntity userEntity;

    @InjectMocks
    private PersonalOfficeController personalOfficeController;

    @Test
    public void PersonalOfficeControllerTest_ShowPersonalOffice_UserFound() {
        Model model = new ConcurrentModel();
        String phoneNumber = "80978520111";
        UserEntity user = new UserEntity();
        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(user);
        String viewName = personalOfficeController.showPersonalOffice(phoneNumber, model);
        assertEquals("pages/personal_office", viewName);
        assertEquals(user, model.getAttribute("user"));
        verify(userService, times(1)).getUserByPhoneNumber(Long.parseLong(phoneNumber));
    }

    @Test
    public void PersonalOfficeControllerTest_ShowPersonalOffice_UserNotFound() {
        Model model = new ConcurrentModel();
        String phoneNumber = "80978520111";
        when(userService.getUserByPhoneNumber(anyLong())).thenReturn(null);
        String viewName = personalOfficeController.showPersonalOffice(phoneNumber, model);
        assertEquals("pages/error", viewName);
        verify(userService, times(1)).getUserByPhoneNumber(Long.parseLong(phoneNumber));
    }
}