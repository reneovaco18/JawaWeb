package hr.java.web.javawebproject.repositories;

import hr.java.web.javawebproject.model.CartItem;
import hr.java.web.javawebproject.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<CartItem> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"product"})
    void deleteByUserAndProductId(User user, Long productId);
}
