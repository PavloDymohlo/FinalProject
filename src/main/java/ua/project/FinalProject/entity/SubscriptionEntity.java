package ua.project.FinalProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.stereotype.Component;
import ua.project.FinalProject.Enum.SubscriptionEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Component
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
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "duration_minutes")
    private int durationMinutes;
    @JsonIgnore
    @OneToMany(mappedBy = "subscriptionEntity", cascade = CascadeType.ALL)
    private List<MusicFileEntity> musicFiles;
    @JsonIgnore
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<UserEntity> users;

}