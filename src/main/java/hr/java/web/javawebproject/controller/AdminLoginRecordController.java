package hr.java.web.javawebproject.controller;


import hr.java.web.javawebproject.model.LoginRecord;
import hr.java.web.javawebproject.repositories.LoginRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logins")
@RequiredArgsConstructor
public class AdminLoginRecordController {

    private final LoginRecordRepository loginRepo;

    @GetMapping
    public List<LoginRecord> getLogs(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return loginRepo.findByUserId(userId);
        } else {
            return loginRepo.findAll();
        }
    }
}
