package hr.java.web.javawebproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Add your frontend's URL (adjust the port as needed)
                .allowedOrigins("http://localhost:8080","http://localhost:8081","http://localhost:8085", "http://localhost:8086")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
