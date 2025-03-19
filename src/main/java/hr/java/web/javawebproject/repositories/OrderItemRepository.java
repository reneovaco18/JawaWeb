    package hr.java.web.javawebproject.repositories;


    import org.springframework.stereotype.Repository;
    import org.springframework.data.jpa.repository.JpaRepository;
    import hr.java.web.javawebproject.model.OrderItem;
    @Repository
    public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    }
