package hr.java.web.javawebproject.service;

import hr.java.web.javawebproject.dto.LoginRequest;
import hr.java.web.javawebproject.dto.JwtResponse;
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
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userService.getByEmail(req.getEmail());
        String token = jwtUtil.generateToken(user);
        userService.recordLogin(user, request.getRemoteAddr());
        return new JwtResponse(token);
    }
}
