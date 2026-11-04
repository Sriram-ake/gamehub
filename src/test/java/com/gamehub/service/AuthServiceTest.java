package com.gamehub.service;

import com.gamehub.dto.request.LoginRequest;
import com.gamehub.dto.request.RegisterRequest;
import com.gamehub.dto.response.AuthResponse;
import com.gamehub.exception.DuplicateResourceException;
import com.gamehub.model.Role;
import com.gamehub.model.User;
import com.gamehub.repository.UserRepository;
import com.gamehub.security.JwtService;
import com.gamehub.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("testplayer")
                .email("test@gamehub.local")
                .passwordHash("hashed_secret")
                .displayName("Test Player")
                .avatarUrl("/assets/images/default-avatar.svg")
                .role(Role.PLAYER)
                .totalXp(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Register with valid request should save user and return JWT auth response")
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testplayer")
                .email("test@gamehub.local")
                .password("password123")
                .displayName("Test Player")
                .build();

        when(userRepository.existsByUsername("testplayer")).thenReturn(false);
        when(userRepository.existsByEmail("test@gamehub.local")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_secret");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("mock-jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("testplayer", response.getUser().getUsername());
        assertEquals(0, response.getUser().getTotalXp());
        assertEquals(1, response.getUser().getLevel());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register with duplicate username should throw DuplicateResourceException")
    void register_DuplicateUsername_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testplayer")
                .email("unique@gamehub.local")
                .password("password123")
                .build();

        when(userRepository.existsByUsername("testplayer")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login with valid credentials should return JWT auth response")
    void login_Success() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("testplayer")
                .password("password123")
                .build();

        UserPrincipal principal = new UserPrincipal(sampleUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(principal)).thenReturn("valid-jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("valid-jwt-token", response.getToken());
        assertEquals("testplayer", response.getUser().getUsername());
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Login with wrong password should propagate BadCredentialsException")
    void login_BadCredentials_ThrowsException() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("testplayer")
                .password("wrongpassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
