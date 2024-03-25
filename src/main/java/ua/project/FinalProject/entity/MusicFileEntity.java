package ua.project.FinalProject.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "subscriptionEntity")
@Table(name = "musicFiles")
public class MusicFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "music_file_name", unique = true, nullable = false)
    private String musicFileName;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subscriptions_id")
    private SubscriptionEntity subscriptionEntity;
}