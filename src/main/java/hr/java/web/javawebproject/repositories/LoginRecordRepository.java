package hr.java.web.javawebproject.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import hr.java.web.javawebproject.model.LoginRecord;

import java.util.List;

public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {
    List<LoginRecord> findByUserId(Long userId);
}
