//package ua.project.FinalProject.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ua.project.FinalProject.entity.MusicFileEntity;
//import ua.project.FinalProject.entity.SubscriptionEntity;
//import ua.project.FinalProject.repository.SubscriptionRepository;
//import ua.project.FinalProject.service.MusicFileService;
//import ua.project.FinalProject.service.SubscriptionService;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/musicFiles")
//public class MusicFileController {
//    @Autowired
//    private MusicFileService musicFileService;
//    @Autowired
//    private SubscriptionService subscriptionService;
//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//
//    @GetMapping("/all")
//    public List<MusicFileEntity> getAllMusicFiles() {
//        return musicFileService.getAllMusicFiles();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<MusicFileEntity> getMusicFileById(@PathVariable Long id) {
//        Optional<MusicFileEntity> musicFileOptional = musicFileService.getMusicFileById(id);
//        if (musicFileOptional.isPresent()) {
//            return new ResponseEntity<>(musicFileOptional.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PostMapping("/add")
//    public ResponseEntity<String> addMusicFile(@RequestBody Map<String, String> request) {
//        try {
//            String musicFileName = request.get("musicFileName");
//            String subscriptionName = request.get("subscriptionName");
//            SubscriptionEntity subscription = musicFileService.getSubscriptionByName(subscriptionName);
//            if (subscription == null) {
//                throw new IllegalArgumentException("Subscription with the provided name not found");
//            }
//            MusicFileEntity musicFile = new MusicFileEntity();
//            musicFile.setMusicFileName(musicFileName);
//            musicFile.setSubscriptionEntity(subscription);
//            musicFileService.addMusicFile(musicFile);
//
//            return ResponseEntity.ok("Music file added successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Failed to add music file: " + e.getMessage());
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<String> updateMusicFile(@PathVariable Long id, @RequestBody Map<String, String> request) {
//        try {
//            String musicFileName = request.get("musicFileName");
//            String subscriptionName = request.get("subscriptionName");
//            SubscriptionEntity subscription = subscriptionService.getSubscriptionByName(subscriptionName);
//            if (subscription == null) {
//                throw new IllegalArgumentException("Subscription with the provided name not found");
//            }
//            musicFileService.updateMusicFile(id, musicFileName, subscription);
//
//            return ResponseEntity.ok("Music file updated successfully");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Failed to update music file: " + e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMusicFile(@PathVariable Long id) {
//        musicFileService.deleteMusicFileById(id);
//        return ResponseEntity.noContent().build();
//    }
//}