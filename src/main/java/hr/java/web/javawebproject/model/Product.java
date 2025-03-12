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
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    // In case you want to track stock
    private Integer stockQuantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
}
