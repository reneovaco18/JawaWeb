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
import java.util.List;

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
        // If the "loginRecorded" attribute is set, do nothing
        if (alreadyRecordedInThisRequest()) {
            return;
        }

        Object principal = event.getAuthentication().getPrincipal();

        // 1) Check if it’s our domain User
        if (principal instanceof hr.java.web.javawebproject.model.User domainUser) {
            maybeSaveSuccessLogin(domainUser.getId(), domainUser.getEmail());

            // 2) Otherwise, it’s the Spring Security user, so do a lookup
        } else if (principal instanceof org.springframework.security.core.userdetails.User secUser) {
            String email = secUser.getUsername();
            User domainUser = userRepository.findByEmail(email).orElse(null);

            if (domainUser != null) {
                maybeSaveSuccessLogin(domainUser.getId(), domainUser.getEmail());
            } else {
                // No domain user found => record minimal info
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

    private boolean alreadyRecordedInThisRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            Object flag = attrs.getRequest().getAttribute("loginRecorded");
            return Boolean.TRUE.equals(flag);
        } catch (IllegalStateException ex) {
            // No current request
            return false;
        }
    }


    private void maybeSaveSuccessLogin(Long userId, String email) {
        // Check if we have already created a success record in the past 2 seconds
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(2);
        List<LoginRecord> recent = loginRecordRepository
                .findByUserIdAndSuccessAndLoginTimeAfter(userId, true, cutoff);

        // Only save if no recent success record yet
        if (recent.isEmpty()) {
            LoginRecord record = LoginRecord.builder()
                    .user(userRepository.findById(userId).orElse(null))
                    .attemptedUsername(email)
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
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest().getRemoteAddr();
        } catch (IllegalStateException ex) {
            return "unknown";
        }
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest().getHeader("User-Agent");
        } catch (IllegalStateException ex) {
            return "unknown";
        }
    }
}
