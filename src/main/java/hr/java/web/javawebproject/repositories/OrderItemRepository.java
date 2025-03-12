    package hr.java.web.javawebproject.repositories;



    import org.springframework.data.jpa.repository.JpaRepository;
    import hr.java.web.javawebproject.model.OrderItem;

    public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    }
