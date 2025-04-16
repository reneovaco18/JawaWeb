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
    // New method: check if there's a recent success record for the same user
    List<LoginRecord> findByUserIdAndSuccessAndLoginTimeAfter(Long userId, boolean success, LocalDateTime after);
}
