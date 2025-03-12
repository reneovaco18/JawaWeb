package hr.java.web.javawebproject.model;







import lombok.*;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // e.g. email or username
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;  // stored encoded (BCrypt)

    @Column(nullable = false)
    private String role; // e.g. "USER" or "ADMIN"

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LoginRecord> loginRecords = new HashSet<>();
}
