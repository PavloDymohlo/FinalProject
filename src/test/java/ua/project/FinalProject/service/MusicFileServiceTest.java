package ua.project.FinalProject.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.repository.MusicFileRepository;
import ua.project.FinalProject.repository.SubscriptionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MusicFileServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private MusicFileRepository musicFileRepository;

    @InjectMocks
    private MusicFileService musicFileService;


    @Test
    public void MusicFilesService_SortedBySubscription() {
        SubscriptionEnum subscriptionEnum = SubscriptionEnum.OPTIMAL;

        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .id(1L)
                .subscriptionEnum(subscriptionEnum)
                .price(BigDecimal.TEN)
                .durationMinutes(60)
                .musicFiles(new ArrayList<>())
                .users(new ArrayList<>())
                .build();

        List<MusicFileEntity> musicFiles = Arrays.asList(
                new MusicFileEntity(1L, "file1.mp3", subscriptionEntity),
                new MusicFileEntity(2L, "file2.mp3", subscriptionEntity)
        );

        when(subscriptionRepository.findBySubscriptionEnum(subscriptionEnum))
                .thenReturn(subscriptionEntity);

        when(musicFileRepository.findBySubscriptionEntity(subscriptionEntity))
                .thenReturn(musicFiles);

        List<MusicFileEntity> result = musicFileService.getAllMusicFilesSortedBySubscription(subscriptionEnum);

        assertEquals(2, result.size());
        assertEquals("file1.mp3", result.get(0).getMusicFileName());
        assertEquals("file2.mp3", result.get(1).getMusicFileName());
    }
    @Test
    public void MusicFilesService_GetMusicFileById() {
        Long id = 1L;
        MusicFileEntity musicFileEntity = new MusicFileEntity(id, "test.mp3", null);

        when(musicFileRepository.findById(id)).thenReturn(Optional.of(musicFileEntity));

        Optional<MusicFileEntity> result = musicFileService.getMusicFileById(id);

        assertEquals(Optional.of(musicFileEntity), result);
    }
    @Test
    public void MusicFilesService_GetAllMusicFiles() {
        MusicFileEntity musicFile1 = new MusicFileEntity(1L, "file1.mp3", null);
        MusicFileEntity musicFile2 = new MusicFileEntity(2L, "file2.mp3", null);
        List<MusicFileEntity> expectedMusicFiles = Arrays.asList(musicFile1, musicFile2);

        when(musicFileRepository.findAll()).thenReturn(expectedMusicFiles);

        List<MusicFileEntity> result = musicFileService.getAllMusicFiles();

        assertEquals(expectedMusicFiles.size(), result.size());
        assertEquals(expectedMusicFiles.get(0).getMusicFileName(), result.get(0).getMusicFileName());
        assertEquals(expectedMusicFiles.get(1).getMusicFileName(), result.get(1).getMusicFileName());
    }
}
