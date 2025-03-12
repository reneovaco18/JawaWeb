package hr.java.web.javawebproject.model;



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
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many order items to one order
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    // Many order items to one product
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    // Price per product at time of purchase
    private BigDecimal price;
}
