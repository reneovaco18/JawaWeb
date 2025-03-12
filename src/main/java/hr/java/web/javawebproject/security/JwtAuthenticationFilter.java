package hr.java.web.javawebproject.security;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Expecting 'Authorization: Bearer <token>'
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (StringUtils.hasText(token)) {
                try {
                    Claims claims = jwtUtil.validateTokenAndGetClaims(token);
                    String email = claims.getSubject();
                    String role = (String) claims.get("role");
                    Long userId = (Long) claims.get("userId");

                    // Build auth object
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null,
                                    Collections.singletonList(authority));

                    // store userId if needed. Or you can store user object if you load from DB
                    // We'll just store it as a detail
                    authToken.setDetails(userId);

                    // Set auth in context
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } catch (Exception ex) {
                    // Token invalid or expired
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
