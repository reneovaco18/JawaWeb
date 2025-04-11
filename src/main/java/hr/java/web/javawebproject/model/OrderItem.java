package hr.java.web.javawebproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many order items to one order
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({"items"})  // Avoid circular reference
    private Order order;

    // Many order items to one product
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Product product;

    private int quantity;

    private BigDecimal price;
}
