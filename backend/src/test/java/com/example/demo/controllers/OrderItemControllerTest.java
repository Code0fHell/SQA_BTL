package com.example.demo.controllers;

import com.example.demo.entities.OrderItem;
import com.example.demo.models.OrderItemDTO;
import com.example.demo.services.OrderItemService;
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

public class OrderItemControllerTest {

    @InjectMocks
    private OrderItemController orderItemController;

    @Mock
    private OrderItemService orderItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C024 - Test lấy tất cả các OrderItem.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách tất cả các OrderItem.
     */
    @Test
    void testGetAllOrderItems_C024() {
        // Mock dữ liệu trả về từ service
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);

        List<OrderItem> mockOrderItems = Arrays.asList(orderItem1, orderItem2);
        when(orderItemService.findAll()).thenReturn(mockOrderItems);

        // Gọi phương thức controller
        ResponseEntity<List<OrderItem>> response = orderItemController.getAllOrderItems();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(orderItemService, times(1)).findAll();
    }

    /**
     * C025 - Test lấy OrderItem theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu OrderItem theo ID hợp lệ.
     */
    @Test
    void testGetOrderItemById_C025() {
        // Mock dữ liệu trả về từ service
        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setId(1L);

        when(orderItemService.get(1L)).thenReturn(mockOrderItem);

        // Gọi phương thức controller
        ResponseEntity<OrderItem> response = orderItemController.getOrderItem(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(orderItemService, times(1)).get(1L);
    }

    /**
     * C026 - Test tạo mới OrderItem.
     * Mô tả: Kiểm tra khi người dùng tạo mới một OrderItem.
     */
    @Test
    void testCreateOrderItem_C026() {
        // Sử dụng setter để thiết lập dữ liệu cho OrderItemDTO
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setQuantity(2);
        orderItemDTO.setName("Test Product");
        orderItemDTO.setPrice(100L);
        orderItemDTO.setOrder(1L);
        orderItemDTO.setSize(1L);

        // Mock dữ liệu trả về từ service
        Long mockOrderItemId = 1L;
        when(orderItemService.create(orderItemDTO)).thenReturn(mockOrderItemId);

        // Gọi phương thức controller
        ResponseEntity<Long> response = orderItemController.createOrderItem(orderItemDTO);

        // Kiểm tra kết quả trả về
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockOrderItemId, response.getBody());
        verify(orderItemService, times(1)).create(orderItemDTO);
    }

    /**
     * C027 - Test cập nhật OrderItem.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin OrderItem.
     */
    @Test
    void testUpdateOrderItem_C027() {
        // Sử dụng setter để thiết lập dữ liệu cho OrderItemDTO
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setQuantity(3);
        orderItemDTO.setName("Updated Product");
        orderItemDTO.setPrice(150L);
        orderItemDTO.setOrder(1L);
        orderItemDTO.setSize(2L);

        // Gọi phương thức controller
        ResponseEntity<Void> response = orderItemController.updateOrderItem(1L, orderItemDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        verify(orderItemService, times(1)).update(1L, orderItemDTO);
    }

    /**
     * C028 - Test xóa OrderItem.
     * Mô tả: Kiểm tra khi người dùng xóa một OrderItem theo ID.
     */
    @Test
    void testDeleteOrderItem_C028() {
        // Gọi phương thức controller
        ResponseEntity<Void> response = orderItemController.deleteOrderItem(1L);

        // Kiểm tra kết quả trả về
        assertEquals(204, response.getStatusCodeValue());
        verify(orderItemService, times(1)).delete(1L);
    }
}