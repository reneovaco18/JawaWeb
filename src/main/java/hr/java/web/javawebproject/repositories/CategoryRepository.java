package hr.java.web.javawebproject.repositories;




import org.springframework.data.jpa.repository.JpaRepository;
import hr.java.web.javawebproject.model.Category;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
