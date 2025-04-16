package hr.java.web.javawebproject.listeners;

import hr.java.web.javawebproject.model.LoginRecord;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.repositories.LoginRecordRepository;
import hr.java.web.javawebproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener implements ApplicationListener<ApplicationEvent> {

    private final LoginRecordRepository loginRecordRepository;
    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent successEvent) {
            handleSuccessEvent(successEvent);
        } else if (event instanceof AbstractAuthenticationFailureEvent failureEvent) {
            handleFailureEvent(failureEvent);
        }
    }

    private void handleSuccessEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();

        // 1) If the principal is our domain User
        if (principal instanceof hr.java.web.javawebproject.model.User domainUser) {
            createSuccessRecord(domainUser.getId(), domainUser.getEmail());

            // 2) If the principal is a Spring Security user => do a DB lookup by email
        } else if (principal instanceof org.springframework.security.core.userdetails.User secUser) {
            String email = secUser.getUsername();
            User domainUser = userRepository.findByEmail(email).orElse(null);

            if (domainUser != null) {
                createSuccessRecord(domainUser.getId(), domainUser.getEmail());
            } else {
                // No domain user found => still record success with minimal info
                LoginRecord record = LoginRecord.builder()
                        .attemptedUsername(email)
                        .loginTime(LocalDateTime.now())
                        .ipAddress(getClientIpAddress())
                        .success(true)
                        .userAgent(getUserAgent())
                        .build();
                loginRecordRepository.save(record);
            }
        }
    }

    private void createSuccessRecord(Long userId, String email) {
        User domainUser = userRepository.findById(userId).orElse(null);
        LoginRecord record = LoginRecord.builder()
                .user(domainUser)
                .attemptedUsername(email)
                .loginTime(LocalDateTime.now())
                .ipAddress(getClientIpAddress())
                .success(true)
                .userAgent(getUserAgent())
                .build();
        loginRecordRepository.save(record);
    }

    private void handleFailureEvent(AbstractAuthenticationFailureEvent event) {
        // 1) The user tried logging in with an incorrect password or non-existent user
        String attemptedUsername = event.getAuthentication().getName();

        // 2) Build a fail record
        LoginRecord record = LoginRecord.builder()
                .attemptedUsername(attemptedUsername)
                .loginTime(LocalDateTime.now())
                .ipAddress(getClientIpAddress())
                .success(false)
                .failureReason(event.getException().getMessage())
                .userAgent(getUserAgent())
                .build();

        loginRecordRepository.save(record);
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest().getRemoteAddr();
        } catch (IllegalStateException ex) {
            // No request context
            return "unknown";
        }
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest().getHeader("User-Agent");
        } catch (IllegalStateException ex) {
            return "unknown";
        }
    }
}
