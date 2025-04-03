package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.model.LoginRecord;
import hr.java.web.javawebproject.repositories.LoginRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logins")
@RequiredArgsConstructor
public class AdminLoginLogController {

    private final LoginRecordRepository loginRepo;

    @GetMapping
    public List<LoginRecord> getLogs(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) Boolean success) {

        if (email != null) {
            return loginRepo.findByUserEmailContainingIgnoreCase(email);
        }
        if (start != null && end != null) {
            return loginRepo.findByLoginTimeBetween(start, end);
        }
        if (success != null) {
            return loginRepo.findBySuccess(success);
        }
        return loginRepo.findAll();
    }
}
