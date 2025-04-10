package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.models.UserDTO;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.security.AuthService;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    // BeforeEach chạy trước mỗi test case - setup lại môi trường cho mỗi test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C045 - Test lấy tất cả người dùng.
     * Mô tả: Kiểm tra khi admin yêu cầu danh sách tất cả người dùng.
     */
    @Test
    void testGetAllUsers_C045() {
        // Mock dữ liệu trả về từ service
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("user1");

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("user2");

        List<UserDTO> mockUsers = Arrays.asList(user1, user2);
        when(userService.findAllUser()).thenReturn(mockUsers);

        // Gọi phương thức controller
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).findAllUser();
    }

    /**
     * C046 - Test lấy người dùng theo ID.
     * Mô tả: Kiểm tra khi admin yêu cầu thông tin người dùng theo ID hợp lệ.
     */
    @Test
    void testGetUserById_C046() {
        // Mock dữ liệu trả về từ service
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("user1");

        when(userService.get(1L)).thenReturn(mockUser);

        // Gọi phương thức controller
        ResponseEntity<User> response = userController.getUser(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user1", response.getBody().getUsername());
        verify(userService, times(1)).get(1L);
    }

    /**
     * C047 - Test lấy thông tin người dùng hiện tại.
     * Mô tả: Kiểm tra khi người dùng yêu cầu thông tin của chính họ.
     */
    @Test
    void testMe_C047() {
        // Mock dữ liệu trả về từ AuthService
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("currentuser");

        when(authService.getCurrentUser()).thenReturn(mockUser);

        // Gọi phương thức controller
        ResponseEntity<User> response = userController.me();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("currentuser", response.getBody().getUsername());
        verify(authService, times(1)).getCurrentUser();
    }

    /**
     * C048 - Test tạo mới người dùng.
     * Mô tả: Kiểm tra khi admin tạo mới một người dùng.
     */
    @Test
    void testCreateUser_C048() {
        // Sử dụng setter để thiết lập dữ liệu cho UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setEmail("newuser@example.com");

        // Mock kiểm tra tồn tại username và email
        when(userService.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userService.existsByEmail(userDTO.getEmail())).thenReturn(false);

        // Mock dữ liệu trả về từ service
        Long mockUserId = 1L;
        when(userService.create(userDTO)).thenReturn(mockUserId);

        // Gọi phương thức controller
        ResponseEntity<?> response = userController.createUser(userDTO);

        // Kiểm tra kết quả trả về
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockUserId, response.getBody());
        verify(userService, times(1)).create(userDTO);
    }

    /**
     * C049 - Test tìm kiếm người dùng.
     * Mô tả: Kiểm tra khi admin tìm kiếm người dùng theo từ khóa.
     */
    @Test
    void testSearchUser_C049() {
        // Mock dữ liệu trả về từ service
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<User> mockUsers = Arrays.asList(user1, user2);
        when(userService.search("user")).thenReturn(mockUsers);

        // Gọi phương thức controller
        ResponseEntity<List<User>> response = userController.searchUser("user");

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).search("user");
    }

    /**
     * C050 - Test xóa người dùng.
     * Mô tả: Kiểm tra khi admin xóa một người dùng theo ID.
     */
    @Test
    void testDeleteUser_C050() {
        // Gọi phương thức controller
        ResponseEntity<?> response = userController.deleteUser(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Delete success!", response.getBody());
        verify(userService, times(1)).delete(1L);
    }

    /**
     * C051 - Test cập nhật người dùng.
     * Mô tả: Kiểm tra khi admin cập nhật thông tin người dùng.
     */
    @Test
    void testUpdateUser_C051() {
        // Sử dụng setter để thiết lập dữ liệu cho UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("updateduser");
        userDTO.setEmail("updateduser@example.com");
        userDTO.setPhone("123456789");

        // Mock dữ liệu trả về từ service
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("updateduser");
        mockUser.setEmail("updateduser@example.com");
        mockUser.setPhone("123456789");

        when(userService.findById(1L)).thenReturn(mockUser);
        when(userService.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userService.existsByEmail(userDTO.getEmail())).thenReturn(false);

        // Gọi phương thức controller
        ResponseEntity<?> response = userController.saveOrUpdateUser(1L, userDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Update success!", ((MessageResponse) response.getBody()).getMessage());
        verify(userService, times(1)).saveOrUpdate(mockUser);
    }
}