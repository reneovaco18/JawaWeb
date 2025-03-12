    package hr.java.web.javawebproject.model;









    import lombok.*;
    import jakarta.persistence.*;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.ArrayList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    @Table(name = "orders")
    public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Many orders to one user
        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id")
        private User user;

        private LocalDateTime orderDate;

        private String paymentMethod; // "COD" or "PAYPAL"

        private BigDecimal total;

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<OrderItem> items = new ArrayList<>();
    }

