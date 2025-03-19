    package hr.java.web.javawebproject.repositories;



    import org.springframework.data.jpa.repository.JpaRepository;
    import hr.java.web.javawebproject.model.User;
    import org.springframework.stereotype.Repository;
    import java.util.Optional;
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);
    }
