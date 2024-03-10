package ua.project.FinalProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.project.FinalProject.Enum.AutoRenewStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "subscription")
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_number", unique = true, nullable = false)
    private long phoneNumber;
    @Column(name = "bank_card_number", unique = true, nullable = false)
    private long bankCardNumber;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "auto_renew")
    @Enumerated(EnumType.STRING)
    private AutoRenewStatus autoRenew;
    @ManyToOne
    @JoinColumn(name = "subscriptions_id")
    private SubscriptionEntity subscription;

    public void setSubscription(SubscriptionEntity subscriptionEntity) {
        this.subscription = subscriptionEntity;
    }

    public AutoRenewStatus getAutoRenew() {
        return this.autoRenew;
    }

    public void setAutoRenew(AutoRenewStatus autoRenew) {
        this.autoRenew = autoRenew;
    }
}