package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.entities.Role;
import com.example.demo.entities.ERole;
import com.example.demo.entities.User;

public class CustomUserDetailsTest {

    private User testUser;
    private List<Role> roles;

    @BeforeEach
    void setUp() {
        // Khởi tạo user giả lập
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setUserStatus(true);

        // Gán role USER
        roles = new ArrayList<>();
        Role role = new Role();
        role.setRolename(ERole.ROLE_USER);
        roles.add(role);
        testUser.setListRoles(new HashSet<>(roles));
    }

    /* S003 */
    @Test
    void mapUserToUserDetails_ShouldMapCorrectly() {
        // Act
        CustomUserDetails userDetails = CustomUserDetails.mapUserToUserDetails(testUser);

        // Assert cơ bản các field
        assertNotNull(userDetails);
        assertEquals(1L, userDetails.getUserId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("1234567890", userDetails.getPhone());
        assertTrue(userDetails.isUserStatus());

        // Assert authority (vai trò người dùng)
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.get(0).getAuthority());
    }

    /* S004 */
    @Test
    void userDetailsImplementation_ShouldHaveExpectedValues() {
        // Arrange: Tạo userDetails thủ công
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        CustomUserDetails userDetails = new CustomUserDetails(
            1L, "testuser", "password", "test@example.com",
            "1234567890", true, authorities
        );

        // Assert các method bắt buộc của Spring Security
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
