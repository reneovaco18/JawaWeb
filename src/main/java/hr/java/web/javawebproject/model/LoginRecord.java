package hr.java.web.javawebproject.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "login_records")
public class LoginRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)  // Change to true
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "attempted_username")
    private String attemptedUsername;

    private LocalDateTime loginTime;

    private String ipAddress;

    private boolean success;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    // Add proper record constructor with all fields
    public LoginRecord(User user, String attemptedUsername, LocalDateTime loginTime,
                       String ipAddress, boolean success, String failureReason,
                       String userAgent) {
        this.user = user;
        this.attemptedUsername = attemptedUsername;
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
        this.success = success;
        this.failureReason = failureReason;
        this.userAgent = userAgent;
    }
}
