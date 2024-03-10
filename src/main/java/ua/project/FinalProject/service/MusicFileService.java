package ua.project.FinalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
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
public class MusicFileService {
    @Autowired
    private MusicFileRepository musicFileRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionService subscriptionService;

    public List<MusicFileEntity> getAllMusicFilesSortedBySubscription(SubscriptionEnum subscriptionEnum) {//+
        SubscriptionEntity subscriptionEntity = subscriptionRepository.findBySubscriptionEnum(subscriptionEnum);

        List<MusicFileEntity> musicFiles = musicFileRepository.findBySubscriptionEntity(subscriptionEntity);

        return musicFiles;
    }
    @Transactional
    public void addMusicFile(MusicFileEntity musicFile) {
        musicFileRepository.save(musicFile);
    }

    public MusicFileEntity updateMusicFile(Long musicFileId, SubscriptionEntity newSubscription) throws ChangeSetPersister.NotFoundException {
        Optional<MusicFileEntity> musicFileOptional = musicFileRepository.findById(musicFileId);
        if (musicFileOptional.isPresent()) {
            MusicFileEntity musicFile = musicFileOptional.get();
            musicFile.setSubscriptionEntity(newSubscription);
            return musicFileRepository.save(musicFile);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    public void deleteMusicFileById(Long id) {
        musicFileRepository.deleteById(id);
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
        } else {
            throw new IllegalArgumentException("Music file with the provided id not found");
        }
    }
}
