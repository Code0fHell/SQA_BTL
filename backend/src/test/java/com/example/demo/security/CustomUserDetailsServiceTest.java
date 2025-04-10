package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;

/**
 * Unit test cho CustomUserDetailsService – xử lý việc load thông tin người dùng cho Spring Security.
 */
@ExtendWith(MockitoExtension.class)
@Import(CustomUserDetailsServiceTest.TestConfig.class)
public class CustomUserDetailsServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return new CustomUserDetailsService();
        }
    }

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
    }

    /**
     * S001 - Kiểm tra khi username tồn tại trong hệ thống.
     */
    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    /**
     * S002 Kiểm tra khi username không tồn tại – ném ra UsernameNotFoundException.
     */
    @Test
    void loadUserByUsername_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }
}
