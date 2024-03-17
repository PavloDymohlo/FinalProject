package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ua.project.FinalProject.service.JwtService;
import ua.project.FinalProject.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;

    @InjectMocks
    private PaymentController paymentController;
    @Test
    void PaymentControllerTest_SuccessfulPayment() throws Exception {
        // Arrange
        long phoneNumber = 80994485858L;
        String subscriptionName = "MAXIMUM";

        // Mock userService.subscriptionPayment to succeed
        doNothing().when(userService).subscriptionPayment(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/personal_office/{phoneNumber}/payment", phoneNumber)
                        .param("subscriptionName", subscriptionName))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/personal_office"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Payment successful"));

        // Verify that userService.subscriptionPayment is called with the correct arguments
        verify(userService, times(1)).subscriptionPayment(phoneNumber, subscriptionName);
    }

    @Test
    void PaymentControllerTest_PaymentNotSuccessful() throws Exception {
        // Arrange
        long phoneNumber = 123456789;
        String subscriptionName = "InvalidSubscription";

        // Mock userService.subscriptionPayment to throw IllegalArgumentException
        doThrow(new IllegalArgumentException("Invalid subscription")).when(userService).subscriptionPayment(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/personal_office/{phoneNumber}/payment", phoneNumber)
                        .param("subscriptionName", subscriptionName))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/personal_office"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Insufficient funds or invalid subscription"));

        // Verify that userService.subscriptionPayment is called with the correct arguments
        verify(userService, times(1)).subscriptionPayment(phoneNumber, subscriptionName);
    }

    @Test
    void processPayment_WithException_ReturnsPersonalOfficePageWithErrorMessage() throws Exception {
        // Arrange
        long phoneNumber = 123456789;
        String subscriptionName = "Premium";

        // Mock userService.subscriptionPayment to throw an Exception
        doThrow(new RuntimeException("Internal server error")).when(userService).subscriptionPayment(anyLong(), anyString());

        // Act & Assert
        mockMvc.perform(post("/personal_office/{phoneNumber}/payment", phoneNumber)
                        .param("subscriptionName", subscriptionName))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/personal_office"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Internal server error"));

        // Verify that userService.subscriptionPayment is called with the correct arguments
        verify(userService, times(1)).subscriptionPayment(phoneNumber, subscriptionName);
    }

}
