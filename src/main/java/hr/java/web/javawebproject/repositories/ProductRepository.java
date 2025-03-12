package hr.java.web.javawebproject.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import hr.java.web.javawebproject.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
}
