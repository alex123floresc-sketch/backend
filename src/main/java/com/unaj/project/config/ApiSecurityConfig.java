package com.unaj.project.config;

import com.unaj.project.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Cadena de seguridad SOLO para la API REST (/api/**) que consume Angular.
 * Es stateless (sin sesión) y se autentica con JWT en cada petición.
 *
 * Tiene @Order(1) para evaluarse ANTES que la cadena de Thymeleaf (SecurityConfig, @Order(2)),
 * de modo que el resto de la app server-rendered sigue funcionando igual con login por formulario.
 */
@Configuration
public class ApiSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    public ApiSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Sin esto, un 401/403 por defecto (Http403ForbiddenEntryPoint) termina en un
                // forward interno a "/error", que no matchea "/api/**" y cae en la cadena de
                // Thymeleaf (SecurityConfig), la cual redirige a "/login" en vez de devolver JSON.
                // Escribiendo la respuesta acá evitamos ese forward por completo.
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json");
                            response.getWriter().write("{\"mensaje\":\"No autenticado\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json");
                            response.getWriter().write("{\"mensaje\":\"No tiene permisos para esta acción\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos de la API
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        // Escrituras restringidas por rol (mismo criterio que el backend Thymeleaf)
                        .requestMatchers(HttpMethod.POST, "/api/cursos/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cursos/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cursos/**").hasAnyRole("ADMIN")
                        // El resto de la API requiere estar autenticado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
