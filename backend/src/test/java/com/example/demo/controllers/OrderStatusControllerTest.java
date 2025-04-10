package com.example.demo.controllers;

import com.example.demo.entities.OrderStatus;
import com.example.demo.models.OrderStatusDTO;
import com.example.demo.services.OrderStatusService;
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

public class OrderStatusControllerTest {

    @InjectMocks
    private OrderStatusController orderStatusController;

    @Mock
    private OrderStatusService orderStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C029 - Test lấy tất cả các trạng thái đơn hàng.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách tất cả các trạng thái đơn
     * hàng.
     */
    @Test
    void testGetAllOrderStatuses_C029() {
        // Mock dữ liệu trả về từ service
        OrderStatus status1 = new OrderStatus();
        status1.setId(1L);
        status1.setName("Pending");

        OrderStatus status2 = new OrderStatus();
        status2.setId(2L);
        status2.setName("Shipped");

        List<OrderStatus> mockStatuses = Arrays.asList(status1, status2);
        // Mô phỏng hành vi của các phương thức trong OrderStatusService
        when(orderStatusService.findAll()).thenReturn(mockStatuses);

        // Gọi phương thức controller
        ResponseEntity<List<OrderStatus>> response = orderStatusController.getAllOrderStatus();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(orderStatusService, times(1)).findAll();
    }

    /**
     * C030 - Test lấy trạng thái đơn hàng theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu trạng thái đơn hàng theo ID hợp lệ.
     */
    @Test
    void testGetOrderStatusById_C030() {
        // Mock dữ liệu trả về từ service
        OrderStatus mockStatus = new OrderStatus();
        mockStatus.setId(1L);
        mockStatus.setName("Pending");

        when(orderStatusService.get(1L)).thenReturn(mockStatus);

        // Gọi phương thức controller
        ResponseEntity<OrderStatus> response = orderStatusController.getOrderStatus(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Pending", response.getBody().getName());
        verify(orderStatusService, times(1)).get(1L);
    }

    /**
     * C031 - Test tạo mới trạng thái đơn hàng.
     * Mô tả: Kiểm tra khi người dùng tạo mới một trạng thái đơn hàng.
     */
    @Test
    void testCreateOrderStatus_C031() {
        // Sử dụng setter để thiết lập dữ liệu cho OrderStatusDTO
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setName("Delivered");

        // Mock dữ liệu trả về từ service
        Long mockStatusId = 1L;
        when(orderStatusService.create(orderStatusDTO)).thenReturn(mockStatusId);

        // Gọi phương thức controller
        ResponseEntity<Long> response = orderStatusController.createOrderStatus(orderStatusDTO);

        // Kiểm tra kết quả trả về
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockStatusId, response.getBody());
        verify(orderStatusService, times(1)).create(orderStatusDTO);
    }

    /**
     * C032 - Test cập nhật trạng thái đơn hàng.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin trạng thái đơn hàng.
     */
    @Test
    void testUpdateOrderStatus_C032() {
        // Sử dụng setter để thiết lập dữ liệu cho OrderStatusDTO
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setName("Updated Status");

        // Gọi phương thức controller
        ResponseEntity<Void> response = orderStatusController.updateOrderStatus(1L, orderStatusDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        verify(orderStatusService, times(1)).update(1L, orderStatusDTO);
    }

    /**
     * C033 - Test xóa trạng thái đơn hàng.
     * Mô tả: Kiểm tra khi người dùng xóa một trạng thái đơn hàng theo ID.
     */
    @Test
    void testDeleteOrderStatus_C033() {
        // Gọi phương thức controller
        ResponseEntity<Void> response = orderStatusController.deleteOrderStatus(1L);

        // Kiểm tra kết quả trả về
        assertEquals(204, response.getStatusCodeValue());
        verify(orderStatusService, times(1)).delete(1L);
    }
}