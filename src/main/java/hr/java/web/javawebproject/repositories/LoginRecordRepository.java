package hr.java.web.javawebproject.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import hr.java.web.javawebproject.model.LoginRecord;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {
    List<LoginRecord> findByUserId(Long userId);
}
