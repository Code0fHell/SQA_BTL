package com.example.demo.controllers;

import com.example.demo.entities.CartItem;
import com.example.demo.models.CartItemDTO;
import com.example.demo.services.CartItemService;
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

public class CartItemControllerTest {

    @InjectMocks
    private CartItemController cartItemController;

    @Mock
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C004 - Test lấy tất cả các CartItem của người dùng.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách CartItem và so sánh kết quả
     * trả về với dữ liệu mock.
     */
    @Test
    void testFindAllByUserId_C004() {
        // Mock dữ liệu trả về từ service
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQuantity(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setQuantity(1);

        List<CartItem> mockCartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartItemService.findAllByUserId()).thenReturn(mockCartItems);

        // Gọi phương thức controller
        ResponseEntity<List<CartItem>> response = cartItemController.findAllByUserId();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCartItems.size(), response.getBody().size());
        assertEquals(mockCartItems, response.getBody()); // So sánh toàn bộ danh sách
        verify(cartItemService, times(1)).findAllByUserId();
    }

    /**
     * C005 - Test lấy CartItem theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu CartItem theo ID hợp lệ và so sánh kết
     * quả trả về với dữ liệu mock.
     */
    @Test
    void testFindById_C005() {
        // Mock dữ liệu trả về từ service
        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setQuantity(2);

        when(cartItemService.get(1L)).thenReturn(mockCartItem);

        // Gọi phương thức controller
        ResponseEntity<CartItem> response = cartItemController.findById(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCartItem, response.getBody()); // So sánh toàn bộ đối tượng
        verify(cartItemService, times(1)).get(1L);
    }

    /**
     * C006 - Test tạo mới CartItem.
     * Mô tả: Kiểm tra khi người dùng tạo mới một CartItem và so sánh kết quả trả về
     * với dữ liệu mock.
     */
    @Test
    void testCreateCartItem_C006() {
        // Sử dụng setter để thiết lập dữ liệu cho CartItemDTO
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setQuantity(2);

        // Mock dữ liệu trả về từ service
        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setQuantity(2);

        when(cartItemService.create(cartItemDTO)).thenReturn(mockCartItem);

        // Gọi phương thức controller
        ResponseEntity<CartItem> response = cartItemController.create(cartItemDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCartItem, response.getBody()); // So sánh toàn bộ đối tượng
        verify(cartItemService, times(1)).create(cartItemDTO);
    }

    /**
     * C007 - Test cập nhật CartItem.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin CartItem và so sánh kết quả
     * trả về với dữ liệu mock.
     */
    @Test
    void testUpdateCartItem_C007() {
        // Sử dụng setter để thiết lập dữ liệu cho CartItemDTO
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setQuantity(3);

        // Mock dữ liệu trả về từ service
        CartItem mockUpdatedCartItem = new CartItem();
        mockUpdatedCartItem.setId(1L);
        mockUpdatedCartItem.setQuantity(3);

        when(cartItemService.update(1L, cartItemDTO)).thenReturn(mockUpdatedCartItem);

        // Gọi phương thức controller
        ResponseEntity<CartItem> response = cartItemController.update(1L, cartItemDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUpdatedCartItem, response.getBody()); // So sánh toàn bộ đối tượng
        verify(cartItemService, times(1)).update(1L, cartItemDTO);
    }

    /**
     * C008 - Test xóa CartItem.
     * Mô tả: Kiểm tra khi người dùng xóa một CartItem theo ID và xác minh hành vi
     * của service.
     */
    @Test
    void testDeleteCartItem_C008() {
        // Gọi phương thức controller
        ResponseEntity<Void> response = cartItemController.delete(1L);

        // Kiểm tra kết quả trả về
        assertEquals(204, response.getStatusCodeValue());
        verify(cartItemService, times(1)).delete(1L);
    }
}