package ua.project.FinalProject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ua.project.FinalProject.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    public void PaymentControllerTest_SuccessfulPayment() {
        Model model = new ConcurrentModel();
        long phoneNumber = 80988523021L;
        String subscriptionName = "MAXIMUM";
        doNothing().when(userService).subscriptionPayment(phoneNumber, subscriptionName);
        String viewName = paymentController.processPayment(phoneNumber, subscriptionName, model);
        assertEquals("pages/personal_office", viewName);
        assertEquals("Payment successful", model.getAttribute("message"));
        verify(userService, times(1)).subscriptionPayment(phoneNumber, subscriptionName);
    }

    @Test
    public void PaymentControllerTest_InsufficientFunds() {
        Model model = new ConcurrentModel();
        long phoneNumber = 80988523021L;
        String subscriptionName = "MAXIMUM";
        String errorMessage = "Insufficient funds";
        doThrow(new IllegalArgumentException(errorMessage)).when(userService).subscriptionPayment(phoneNumber, subscriptionName);
        String viewName = paymentController.processPayment(phoneNumber, subscriptionName, model);
        assertEquals("pages/personal_office", viewName);
        assertEquals("Insufficient funds or invalid subscription", model.getAttribute("message"));
        verify(userService, times(1)).subscriptionPayment(phoneNumber, subscriptionName);
    }
}
