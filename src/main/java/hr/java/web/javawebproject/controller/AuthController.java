package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.dto.LoginRequest;
import hr.java.web.javawebproject.dto.RegisterRequest;
import hr.java.web.javawebproject.dto.JwtResponse;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.security.JwtUtil;
import hr.java.web.javawebproject.service.UserService;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, UserService userService, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user = userService.registerNewUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userService.getByEmail(req.getEmail());
        String token = jwtUtil.generateToken(user);
        userService.recordLogin(user, request.getRemoteAddr());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
