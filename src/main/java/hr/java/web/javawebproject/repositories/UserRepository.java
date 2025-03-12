    package hr.java.web.javawebproject.repositories;



    import org.springframework.data.jpa.repository.JpaRepository;
    import hr.java.web.javawebproject.model.User;

    import java.util.Optional;

    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);
    }
