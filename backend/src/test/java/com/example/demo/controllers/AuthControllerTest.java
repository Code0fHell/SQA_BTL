package com.example.demo.controllers;

import com.example.demo.entities.ERole;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.jwt.JwtTokenPovider;
import com.example.demo.models.UserDTO;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignUpRequest;
import com.example.demo.payload.response.JwtResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.security.AuthService;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    // @InjectMocks sẽ tự động tạo một đối tượng AuthController và tiêm các phụ
    // thuộc vào nó
    // @Mock sẽ tạo các đối tượng giả (mock) cho các phụ thuộc của AuthController
    @InjectMocks
    private AuthController authController;

    // Mock UserService - lớp dùng để thao tác với user (tìm kiếm, lưu user...)
    @Mock
    private UserService userService;

    // Mock RoleService - dùng để lấy role từ enum hoặc database
    @Mock
    private RoleService roleService;

    // Mock AuthService - có thể dùng trong đăng nhập, đăng ký, xác thực người dùng
    @Mock
    private AuthService authService;

    // Mock JwtTokenPovider - dùng để tạo hoặc xác minh JWT token
    @Mock
    private JwtTokenPovider jwtTokenProvider;

    // Mock AuthenticationManager - dùng để xác thực người dùng (Spring Security)
    @Mock
    private AuthenticationManager authenticationManager;

    // Mock PasswordEncoder - dùng để mã hóa mật khẩu
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C001 - Test đăng ký người dùng mới.
     * Mô tả: Kiểm tra khi người dùng đăng ký tài khoản mới với thông tin hợp lệ.
     */
    @Test
    void testRegisterUser_C001() {
        // Mock dữ liệu đầu vào
        SignUpRequest validRequest = new SignUpRequest();
        validRequest.setUsername("newUser");
        validRequest.setPassword("123456");
        validRequest.setEmail("new@example.com");
        validRequest.setPhone("0826892396");
        validRequest.setListRoles(Set.of("ROLE_USER"));

        // Tạo đối tượng Role
        Role role = new Role();
        role.setRolename(ERole.ROLE_USER);

        // Mock các phương thức của userService và roleService
        // Mô phỏng hành vi của các phương thức trong userService và roleService
        when(userService.existsByUsername("newUser")).thenReturn(false);
        when(userService.existsByEmail("new@example.com")).thenReturn(false);
        when(roleService.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        // Gọi phương thức controller
        ResponseEntity<?> response = authController.registerUser(validRequest);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", ((MessageResponse) response.getBody()).getMessage());
        verify(userService, times(1)).saveOrUpdate(any(User.class));
    }

    /**
     * C002 - Test đăng nhập người dùng.
     * Mô tả: Kiểm tra khi người dùng đăng nhập với thông tin hợp lệ.
     */
    @Test
    void testLoginUser_C002() {
        // Mock dữ liệu đầu vào
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user1");
        loginRequest.setPassword("password");

        // Giả lập authentication - Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock CustomUserDetails
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        CustomUserDetails customUserDetails = new CustomUserDetails(
                1L,
                "user1",
                "encodedPassword",
                "user1@example.com",
                "123456789",
                true,
                authorities);

        when(authentication.getPrincipal()).thenReturn(customUserDetails);

        // Mock JWT token
        when(jwtTokenProvider.generateToken(customUserDetails)).thenReturn("mock-jwt-token");

        // Gọi phương thức controller
        ResponseEntity<?> response = authController.loginUser(loginRequest);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("mock-jwt-token", jwtResponse.getToken());
        assertEquals("user1", jwtResponse.getUsername());
        assertEquals("user1@example.com", jwtResponse.getEmail());
        assertEquals("123456789", jwtResponse.getPhone());
        assertEquals(List.of("ROLE_USER"), jwtResponse.getListRoles()); // Sửa từ getRoles() thành getListRoles()

        // Kiểm tra các phương thức được gọi đúng cách
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(customUserDetails);
    }

    /**
     * C003 - Test cập nhật thông tin người dùng hiện tại.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin của chính họ.
     */
    @Test
    void testSaveOrUpdateUser_C003() {
        // Mock dữ liệu đầu vào
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("updateduser");
        userDTO.setEmail("updateduser@example.com");
        userDTO.setPhone("123456789");
        userDTO.setPassword("newpassword");

        // Mock dữ liệu người dùng hiện tại
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("currentuser");
        mockUser.setEmail("currentuser@example.com");
        mockUser.setPhone("987654321");

        when(authService.getCurrentUserId()).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(mockUser);
        when(userService.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userService.existsByEmail(userDTO.getEmail())).thenReturn(false);

        // Gọi phương thức controller
        ResponseEntity<?> response = authController.saveOrUpdateUser(userDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Update success!", ((MessageResponse) response.getBody()).getMessage());
        verify(userService, times(1)).saveOrUpdate(mockUser);
    }

}