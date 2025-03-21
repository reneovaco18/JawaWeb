package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.dto.JwtResponse;
import hr.java.web.javawebproject.dto.LoginRequest;
import hr.java.web.javawebproject.dto.RegisterRequest;
import hr.java.web.javawebproject.service.AuthService;
import hr.java.web.javawebproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        var user = userService.registerNewUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody LoginRequest req,
            HttpServletRequest request
    ) {
        JwtResponse jwtResponse = authService.login(req, request);
        return ResponseEntity.ok(jwtResponse);
    }
}
