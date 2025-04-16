package hr.java.web.javawebproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    // Force EAGER so that user data is always fetched
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler", "loginRecords"})
    private User user; // <-- will be included in the JSON, minus ignored fields

    @Column(name = "attempted_username")
    private String attemptedUsername;

    private LocalDateTime loginTime;
    private String ipAddress;
    private boolean success;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    // Extra constructor for convenience
    public LoginRecord(
            User user,
            String attemptedUsername,
            LocalDateTime loginTime,
            String ipAddress,
            boolean success,
            String failureReason,
            String userAgent
    ) {
        this.user = user;
        this.attemptedUsername = attemptedUsername;
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
        this.success = success;
        this.failureReason = failureReason;
        this.userAgent = userAgent;
    }
}
