package hr.java.web.javawebproject.service;

import hr.java.web.javawebproject.dto.RegisterRequest;
import hr.java.web.javawebproject.model.LoginRecord;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.repositories.LoginRecordRepository;
import hr.java.web.javawebproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LoginRecordRepository loginRecordRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(RegisterRequest req) {
        // check if email is in use
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        User user = User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("USER") // default role
                .build();
        return userRepo.save(user);
    }

    public User getByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Records a login with success = true and the user's email as attemptedUsername.
     */
    public void recordLogin(User user, String ipAddress) {
        LoginRecord lr = LoginRecord.builder()
                .user(user)
                .attemptedUsername(user.getEmail())
                .loginTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .success(true) // explicitly set success = true
                .build();

        loginRecordRepo.save(lr);
    }
}
