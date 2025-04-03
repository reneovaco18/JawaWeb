package hr.java.web.javawebproject.repositories;

import hr.java.web.javawebproject.model.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {
    List<LoginRecord> findByUserEmailContainingIgnoreCase(String email);

    List<LoginRecord> findByLoginTimeBetween(LocalDateTime start, LocalDateTime end);

    List<LoginRecord> findBySuccess(boolean success);
}
