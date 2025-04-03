package hr.java.web.javawebproject.listeners;

import hr.java.web.javawebproject.model.LoginRecord;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.repositories.LoginRecordRepository;
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
        if (principal instanceof User user) {
            LoginRecord record = LoginRecord.builder()
                    .user(user)
                    .loginTime(LocalDateTime.now())
                    .ipAddress(getClientIpAddress())
                    .success(true)
                    .userAgent(getUserAgent())
                    .build();
            loginRecordRepository.save(record);
        }
    }

    private void handleFailureEvent(AbstractAuthenticationFailureEvent event) {
        String attemptedUsername = event.getAuthentication().getName();

        // Create record WITHOUT requiring a user object
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
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest().getRemoteAddr();
        } catch (IllegalStateException ex) {
            return "unknown";
        }
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest().getHeader("User-Agent");
        } catch (IllegalStateException ex) {
            return "unknown";
        }
    }
}
