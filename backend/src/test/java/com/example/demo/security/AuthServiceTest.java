package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;

/**
 * Unit test cho AuthService – kiểm tra các phương thức liên quan tới xác thực người dùng hiện tại.
 */
@ExtendWith(MockitoExtension.class)
@Import(AuthServiceTest.TestConfig.class)
public class AuthServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return new AuthService();
        }
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private CustomUserDetails testUserDetails;

    /**
     * Thiết lập trước mỗi test: mock người dùng hiện tại và security context.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");

        testUserDetails = CustomUserDetails.mapUserToUserDetails(testUser);
        
        // Gán SecurityContext cho SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
    }

    /**
     * S005 - Kiểm tra hàm getCurrentUserId() trả về đúng ID của user.
     */
    @Test
    void getCurrentUserId_ShouldReturnUserId() {
        // Act
        Long userId = authService.getCurrentUserId();

        // Assert
        assertEquals(1L, userId);
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }

    /**
     * S006 - Kiểm tra getCurrentUser() trả về đúng User khi tồn tại trong DB.
     */
    @Test
    void getCurrentUser_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User user = authService.getCurrentUser();

        // Assert
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        verify(userRepository).findById(1L);
    }

    /**
     * S007 - Kiểm tra getCurrentUser() khi user không tồn tại trong DB – phải ném lỗi 404.
     */
    @Test
    void getCurrentUser_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.getCurrentUser();
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepository).findById(1L);
    }
}
