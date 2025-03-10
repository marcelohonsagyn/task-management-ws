package br.com.marcelohonsa.taskmanager.service;

import br.com.marcelohonsa.taskmanager.model.User;
import br.com.marcelohonsa.taskmanager.repository.UserRepository;
import br.com.marcelohonsa.taskmanager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("marcelo");
        user.setPassword("password123");
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = authService.register(user);

        assertEquals("User registered successfully!", result);
        assertEquals("encodedPassword", user.getPassword());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {

        when(userRepository.findByUsername("marcelo")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        when(jwtUtil.generateToken("marcelo")).thenReturn("mocked-jwt-token");

        String token = authService.authenticate("marcelo", "password123");

        assertEquals("mocked-jwt-token", token);
        verify(userRepository, times(1)).findByUsername("marcelo");
        verify(passwordEncoder, times(1)).matches("password123", "password123");
        verify(jwtUtil, times(1)).generateToken("marcelo");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("marcelo")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate("marcelo", "password123");
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("marcelo");
        verify(passwordEncoder, times(0)).matches(any(), any());
        verify(jwtUtil, times(0)).generateToken(any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {

        when(userRepository.findByUsername("marcelo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate("marcelo", "wrongpassword");
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("marcelo");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "password123");
        verify(jwtUtil, times(0)).generateToken(any());
    }
}