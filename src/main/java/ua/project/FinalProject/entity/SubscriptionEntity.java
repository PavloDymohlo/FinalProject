package ua.project.FinalProject.entity;

import lombok.*;
import ua.project.FinalProject.Enum.SubscriptionEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"musicFiles", "users"})
@Table(name = "subscriptions")
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_name", unique = true, nullable = false)
    private SubscriptionEnum subscriptionEnum;
    @Column(name="price",nullable = false)
    private BigDecimal price;
    @Column(name = "duration_minutes")
    private int durationMinutes;

    @OneToMany(mappedBy = "subscriptionEntity", cascade = CascadeType.ALL)
    private List<MusicFileEntity> musicFiles;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<UserEntity> users;
}