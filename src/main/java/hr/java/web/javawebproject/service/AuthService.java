package hr.java.web.javawebproject.service;

import hr.java.web.javawebproject.dto.JwtResponse;
import hr.java.web.javawebproject.dto.LoginRequest;
import hr.java.web.javawebproject.dto.UserDto;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authManager, UserService userService, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public JwtResponse login(LoginRequest req, HttpServletRequest request) {
        // 1) If the credentials are bad, this throws an AuthenticationException
        //    => AbstractAuthenticationFailureEvent is fired, so the listener logs a fail record.
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // 2) If we get here, the user is authenticated => AuthenticationSuccessEvent is fired
        User user = userService.getByEmail(req.getEmail());

        // 3) Generate JWT
        String token = jwtUtil.generateToken(user);

        // 4) Build the user DTO
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();

        // 5) Return a JwtResponse containing the token and user
        return JwtResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }
}
