package hr.java.web.javawebproject.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import hr.java.web.javawebproject.model.CartItem;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
}
