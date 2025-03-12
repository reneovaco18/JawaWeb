package hr.java.web.javawebproject.model;



import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owning side of One-to-One with User
    @OneToOne
    @JoinColumn(name = "user_id")  // each cart is linked to a user
    private User user;

    // Many-to-Many with Product
    @ManyToMany
    @JoinTable(name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new HashSet<>();

    // Constructors, getters, setters...
    public Cart() { }
    public Cart(User user) {
        this.user = user;
    }
    // ... getters and setters ...
}
