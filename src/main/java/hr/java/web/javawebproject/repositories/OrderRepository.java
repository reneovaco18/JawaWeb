package hr.java.web.javawebproject.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import hr.java.web.javawebproject.model.Order;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // For user order history
    List<Order> findByUserId(Long userId);

    // For admin filtering by date range
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // Combined filter by user & date range
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
