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
        // 1) Spring Security auth
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        // 2) user from DB
        User user = userService.getByEmail(req.getEmail());

        // 3) Generate JWT
        String token = jwtUtil.generateToken(user);

        // 4) Record login attempt
        //userService.recordLogin(user, request.getRemoteAddr());

        // 5) Mark in this request that we've already done a manual login record
        request.setAttribute("loginRecorded", Boolean.TRUE);

        // 6) Build response
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();

        return JwtResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }

}
