package ua.project.FinalProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.MusicFileEntity;
import ua.project.FinalProject.entity.SubscriptionEntity;
import ua.project.FinalProject.repository.MusicFileRepository;
import ua.project.FinalProject.repository.SubscriptionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicFileService {
    private final MusicFileRepository musicFileRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<MusicFileEntity> getAllMusicFilesSortedBySubscription(SubscriptionEnum subscriptionEnum) {
        log.debug("Getting all music files sorted by subscription: {}", subscriptionEnum);
        SubscriptionEntity subscriptionEntity = subscriptionRepository.findBySubscriptionEnum(subscriptionEnum);

        List<MusicFileEntity> musicFiles = musicFileRepository.findBySubscriptionEntity(subscriptionEntity);
        log.debug("Retrieved {} music files for subscription: {}", musicFiles.size(), subscriptionEnum);

        return musicFiles;
    }

    @Transactional
    public void addMusicFile(MusicFileEntity musicFile) {
        musicFileRepository.save(musicFile);
        log.info("Music file added successfully.");
    }

    public MusicFileEntity updateMusicFile(Long musicFileId, SubscriptionEntity newSubscription) throws ChangeSetPersister.NotFoundException {
        Optional<MusicFileEntity> musicFileOptional = musicFileRepository.findById(musicFileId);
        if (musicFileOptional.isPresent()) {
            MusicFileEntity musicFile = musicFileOptional.get();
            musicFile.setSubscriptionEntity(newSubscription);
            log.info("Music file updated successfully.");
            return musicFileRepository.save(musicFile);
        } else {
            log.error("Music file with ID {} not found.", musicFileId);
            throw new ChangeSetPersister.NotFoundException();
        }
    }
    public Optional<MusicFileEntity> getMusicFileById(Long id) {
        return musicFileRepository.findById(id);
    }

    public List<MusicFileEntity> getAllMusicFiles() {
        return musicFileRepository.findAll();
    }

    public SubscriptionEntity getSubscriptionByName(String subscriptionName) {
        return subscriptionRepository.findBySubscriptionEnum(SubscriptionEnum.valueOf(subscriptionName));
    }

    @Transactional
    public void updateMusicFile(Long id, String musicFileName, SubscriptionEntity subscription) {
        Optional<MusicFileEntity> musicFileOptional = musicFileRepository.findById(id);
        if (musicFileOptional.isPresent()) {
            MusicFileEntity musicFile = musicFileOptional.get();
            musicFile.setMusicFileName(musicFileName);
            musicFile.setSubscriptionEntity(subscription);
            musicFileRepository.save(musicFile);
            log.info("Music file with ID {} updated successfully.", id);
        } else {
            log.error("Music file with ID {} not found.", id);
            throw new IllegalArgumentException("Music file with the provided id not found");
        }
    }
}