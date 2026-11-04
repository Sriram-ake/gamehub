package com.gamehub.config;

import com.gamehub.security.CustomUserDetailsService;
import com.gamehub.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // Required for H2 Console iframe
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // H2 Console for local development
                .requestMatchers("/h2-console/**").permitAll()

                // Public Authentication Endpoints
                .requestMatchers("/api/v1/auth/**", "/api/auth/**").permitAll()

                // Public Games Catalog (supports /api/games and /api/v1/games)
                .requestMatchers(HttpMethod.GET, "/api/games/**", "/api/v1/games/**").permitAll()

                // Test REST endpoints
                .requestMatchers("/api/test", "/api/v1/test").permitAll()

                // Public Leaderboards (except /me)
                .requestMatchers(HttpMethod.GET, "/api/v1/leaderboards/global/me", "/api/v1/leaderboards/*/me", "/api/leaderboards/*/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/leaderboards/**", "/api/leaderboards/**").permitAll()

                // Public Achievements definitions
                .requestMatchers(HttpMethod.GET, "/api/v1/achievements", "/api/achievements").permitAll()

                // Public User lookup
                .requestMatchers(HttpMethod.GET, "/api/v1/users/{username}", "/api/users/{username}").permitAll()

                // OpenAPI / Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Static Frontend Resources
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/register.html",
                    "/profile.html",
                    "/leaderboard.html",
                    "/games/**",
                    "/css/**",
                    "/js/**",
                    "/assets/**",
                    "/favicon.ico"
                ).permitAll()

                // Protected Endpoints (scores submission, own profile, etc.)
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
