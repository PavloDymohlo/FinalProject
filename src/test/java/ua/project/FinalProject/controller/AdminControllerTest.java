package ua.project.FinalProject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.service.MusicFileService;
import ua.project.FinalProject.service.SubscriptionService;
import ua.project.FinalProject.service.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private AdminController adminController;
    private List<UserEntity> mockUsers;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionEntity mockSubscription;
    @Mock
    private MusicFileService musicFileService;
    private List<SubscriptionEntity> mockSubscriptions;
    private List<MusicFileEntity> mockMusicFiles;

    @BeforeEach
    public void userInit() {
        mockUsers = new ArrayList<>();
        mockUsers.add(new UserEntity(1L, 80502587469L, 1234567890123456L, "password", null, null, null));
        mockUsers.add(new UserEntity(2L, 80987654321L, 9876543210987654L, "password", null, null, null));
    }

    @BeforeEach
    public void subscriptionInit() {
        mockSubscriptions = new ArrayList<>();
        mockSubscriptions.add(new SubscriptionEntity(1L, SubscriptionEnum.FREE, BigDecimal.valueOf(0), 30, null, null));
        mockSubscriptions.add(new SubscriptionEntity(2L, SubscriptionEnum.OPTIMAL, BigDecimal.valueOf(9.99), 60, null, null));
        mockSubscriptions.add(new SubscriptionEntity(3L, SubscriptionEnum.MAXIMUM, BigDecimal.valueOf(14.99), 90, null, null));
    }

    @BeforeEach
    public void musicFilesInit() {
        mockMusicFiles = new ArrayList<>();
        mockMusicFiles.add(new MusicFileEntity(1L, "song1.mp3", null));
        mockMusicFiles.add(new MusicFileEntity(2L, "song2.mp3", null));
    }

    @Test
    public void AdminControllerTest_GetAllUsers() {
        when(userService.getAllUsers()).thenReturn(mockUsers);
        ResponseEntity<List<UserEntity>> responseEntity = adminController.getAllUsers();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<UserEntity> returnedUsers = responseEntity.getBody();
        assertNotNull(returnedUsers);
        assertEquals(mockUsers.size(), returnedUsers.size());
        assertEquals(mockUsers, returnedUsers);
    }

    @Test
    public void AdminControllerTest_getUserById() {
        long userId = 1L;
        UserEntity mockUser = mockUsers.get(0);
        when(userService.getUserById(userId)).thenReturn(mockUser);
        ResponseEntity<UserEntity> response = adminController.getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    public void AdminControllerTest_deleteUser() {
        long userId = 1L;
        UserEntity mockUser = mockUsers.get(0);
        ResponseEntity<Void> response = adminController.deleteUser(userId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(userId);
    }

    @Test
    public void AdminControllerTest_updatePhoneNumber() {
        long userId = 1L;
        long newPhoneNumber = 80633256585L;
        Map<String, Long> requestBody = Map.of("newPhoneNumber", newPhoneNumber);
        UserEntity mockUser = mockUsers.get(0);
        ResponseEntity<Void> response = adminController.updatePhoneNumber(userId, requestBody);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).updatePhoneNumber(userId, newPhoneNumber);
    }

    @Test
    public void AdminControllerTest_getAllSubscriptions() {
        when(subscriptionService.getAllSubscriptions()).thenReturn(mockSubscriptions);
        ResponseEntity<List<SubscriptionEntity>> response = adminController.getAllSubscriptions();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSubscriptions.size(), response.getBody().size());
        verify(subscriptionService).getAllSubscriptions();
    }

    @Test
    public void AdminControllerTest_getSubscriptionById() {
        Long subscriptionId = 1L;
        when(subscriptionService.getSubscriptionById(subscriptionId)).thenReturn(mockSubscription);
        ResponseEntity<SubscriptionEntity> response = adminController.getSubscriptionById(subscriptionId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSubscription, response.getBody());
        verify(subscriptionService).getSubscriptionById(subscriptionId);
    }

    @Test
    public void AdminControllerTest_getAllMusicFiles() {
        when(musicFileService.getAllMusicFiles()).thenReturn(mockMusicFiles);
        ResponseEntity<List<MusicFileEntity>> response = adminController.getAllMusicFiles();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MusicFileEntity> returnedMusicGiles = response.getBody();
        assertNotNull(returnedMusicGiles);
        assertEquals(mockMusicFiles.size(), returnedMusicGiles.size());
        verify(musicFileService).getAllMusicFiles();
    }

    @Test
    public void AdminControllerTest_getMusicFileById() {
        long musicFileId = 1L;
        MusicFileEntity mockMusicFile = mockMusicFiles.get(0);
        when(musicFileService.getMusicFileById(musicFileId)).thenReturn(Optional.ofNullable(mockMusicFile));
        ResponseEntity<MusicFileEntity> response = adminController.getMusicFileById(musicFileId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockMusicFile, response.getBody());
    }
}