    package hr.java.web.javawebproject.repositories;



    import org.springframework.data.jpa.repository.JpaRepository;
    import hr.java.web.javawebproject.model.Product;
    import org.springframework.stereotype.Repository;
    import java.util.List;
    @Repository
    public interface ProductRepository extends JpaRepository<Product, Long> {
        List<Product> findByCategoryId(Long categoryId);
    }
