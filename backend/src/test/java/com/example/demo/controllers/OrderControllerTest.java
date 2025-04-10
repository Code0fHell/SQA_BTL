package com.example.demo.controllers;

import com.example.demo.entities.Order;
import com.example.demo.models.OrderDTO;
import com.example.demo.services.OrderService;
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

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C018 - Test lấy tất cả các đơn hàng.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách tất cả các đơn hàng.
     */
    @Test
    void testGetAllOrders_C018() {
        // Mock dữ liệu trả về từ service
        Order order1 = new Order();
        order1.setId(1L);

        Order order2 = new Order();
        order2.setId(2L);

        List<Order> mockOrders = Arrays.asList(order1, order2);
        when(orderService.findAll()).thenReturn(mockOrders);

        // Gọi phương thức controller
        ResponseEntity<List<Order>> response = orderController.getAll();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(orderService, times(1)).findAll();
    }

    /**
     * C019 - Test lấy tất cả các đơn hàng bởi admin.
     * Mô tả: Kiểm tra khi admin yêu cầu danh sách tất cả các đơn hàng.
     */
    @Test
    void testGetAllOrdersByAdmin_C019() {
        // Mock dữ liệu trả về từ service
        Order order1 = new Order();
        order1.setId(1L);

        Order order2 = new Order();
        order2.setId(2L);

        List<Order> mockOrders = Arrays.asList(order1, order2);
        when(orderService.findAllByAdmin()).thenReturn(mockOrders);

        // Gọi phương thức controller
        ResponseEntity<List<Order>> response = orderController.getAllByAdmin();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(orderService, times(1)).findAllByAdmin();
    }

    /**
     * C020 - Test lấy đơn hàng theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu đơn hàng theo ID hợp lệ.
     */
    @Test
    void testGetOrderById_C020() {
        // Mock dữ liệu trả về từ service
        Order mockOrder = new Order();
        mockOrder.setId(1L);

        when(orderService.get(1L)).thenReturn(mockOrder);

        // Gọi phương thức controller
        ResponseEntity<Order> response = orderController.getOrder(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(orderService, times(1)).get(1L);
    }

    /**
     * C021 - Test tạo mới đơn hàng từ giỏ hàng.
     * Mô tả: Kiểm tra khi người dùng tạo mới một đơn hàng từ giỏ hàng.
     */
    @Test
    void testCreateOrderFromCart_C021() {
        // Sử dụng setter để thiết lập dữ liệu cho OrderDTO
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setAddress("123 Test Street");
        orderDTO.setPhoneNumber("0123456789");
        orderDTO.setFirstName("John");
        orderDTO.setLastName("Doe");
        orderDTO.setStatus(1L);

        // Mock dữ liệu trả về từ service
        Long mockOrderId = 1L;
        when(orderService.createOrderFromCart(orderDTO)).thenReturn(mockOrderId);

        // Gọi phương thức controller
        ResponseEntity<Long> response = orderController.createOrderFromCart(orderDTO);

        // Kiểm tra kết quả trả về
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockOrderId, response.getBody());
        verify(orderService, times(1)).createOrderFromCart(orderDTO);
    }

    /**
     * C022 - Test cập nhật đơn hàng.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin đơn hàng.
     */
    @Test
    void testUpdateOrder_C022() {
        // Sử dụng setter để thiết lập dữ liệu cho OrderDTO
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setAddress("456 Updated Street");
        orderDTO.setPhoneNumber("0987654321");
        orderDTO.setFirstName("Jane");
        orderDTO.setLastName("Smith");
        orderDTO.setStatus(2L);

        // Gọi phương thức controller
        ResponseEntity<Void> response = orderController.updateOrder(1L, orderDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, times(1)).update(1L, orderDTO);
    }

    /**
     * C023 - Test xóa đơn hàng.
     * Mô tả: Kiểm tra khi người dùng xóa một đơn hàng theo ID.
     */
    @Test
    void testDeleteOrder_C023() {
        // Gọi phương thức controller
        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        // Kiểm tra kết quả trả về
        assertEquals(204, response.getStatusCodeValue());
        verify(orderService, times(1)).delete(1L);
    }
}