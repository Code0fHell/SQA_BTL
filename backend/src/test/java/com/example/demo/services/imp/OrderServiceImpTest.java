package com.example.demo.services.imp;

import com.example.demo.entities.*;
import com.example.demo.models.OrderDTO;
import com.example.demo.repositories.OrderItemRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.OrderStatusRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.AuthService;
import com.example.demo.services.SizeService;
import com.example.demo.services.Imp.CartItemServiceImp;
import com.example.demo.services.Imp.OrderServiceImp;
import com.example.demo.services.Imp.UserServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AuthService authService;

    @Mock
    private CartItemServiceImp cartItemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceImp userService;

    @Mock
    private SizeService sizeService;

    @Mock
    private ModelMapper modelMapper;

    private OrderServiceImp orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo đối tượng service chính cần test, truyền các dependency bắt buộc qua constructor
        orderService = new OrderServiceImp(orderRepository, orderStatusRepository, userRepository);

        // Gán các dependency còn lại bằng cách sử dụng Reflection do không dùng constructor
        ReflectionTestUtils.setField(orderService, "authService", authService);
        ReflectionTestUtils.setField(orderService, "orderItemRepository", orderItemRepository);
        ReflectionTestUtils.setField(orderService, "cartItemService", cartItemService);
        ReflectionTestUtils.setField(orderService, "userService", userService);
        ReflectionTestUtils.setField(orderService, "sizeService", sizeService);
        ReflectionTestUtils.setField(orderService, "modelMapper", modelMapper);

        // Mặc định giả lập người dùng đã đăng nhập
        User mockUser = new User();
        mockUser.setId(1L);
        when(authService.getCurrentUser()).thenReturn(mockUser);
    }

    // SI012: Kiểm tra tạo đơn hàng từ giỏ hàng thành công
    @Test
    void testCreateOrderFromCart() {
        // Kiểm tra khi tạo đơn hàng từ giỏ hàng thành công

        // Chuẩn bị dữ liệu order được gửi lên
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setPhoneNumber("0991234567");
        orderDTO.setFirstName("John");
        orderDTO.setLastName("Doe");
        orderDTO.setAddress("123 Test Street");
        orderDTO.setStatus(1L);

        // Tạo mock cho status đơn hàng
        OrderStatus mockOrderStatus = new OrderStatus();
        mockOrderStatus.setId(1L);

        // Tạo mock cho đơn hàng
        Order mockOrder = new Order();
        mockOrder.setId(1L);

        // Tạo 2 cart item (giả lập giỏ hàng có 2 sản phẩm)
        CartItem cartItem1 = new CartItem();
        cartItem1.setQuantity(2);
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(100L);
        cartItem1.setProduct(product1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setQuantity(1);
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(200L);
        cartItem2.setProduct(product2);

        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        // Tạo 2 order item tương ứng với cart item
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setName("Product 1");
        orderItem1.setPrice(100L);
        orderItem1.setQuantity(2);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setName("Product 2");
        orderItem2.setPrice(200L);
        orderItem2.setQuantity(1);

        // Giả lập gọi đến các repository
        when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(mockOrderStatus));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(cartItemService.findAllByUserId()).thenReturn(cartItems);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem1, orderItem2);

        // Gọi hàm thực hiện đặt hàng từ giỏ
        Long orderId = orderService.createOrderFromCart(orderDTO);

        // Kiểm tra kết quả trả về
        assertNotNull(orderId);
        assertEquals(1L, orderId);

        // Đảm bảo các phương thức cần thiết được gọi
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
        verify(cartItemService, times(1)).deleteAllByUserId();
    }

    // SI013: Kiểm tra tạo đơn hàng từ giỏ hàng không tồn tại
    @Test
    void testCreateOrderFromCart_CartDoesNotExist() {
        // Kiểm tra khi giỏ hàng rỗng thì không tạo được đơn hàng

        // Chuẩn bị thông tin order
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setPhoneNumber("123456789");
        orderDTO.setFirstName("John");
        orderDTO.setLastName("Doe");
        orderDTO.setAddress("123 Test Street");
        orderDTO.setStatus(1L);

        // Mock status đơn hàng
        OrderStatus mockOrderStatus = new OrderStatus();
        mockOrderStatus.setId(1L);

        // Giả lập gọi đến status repository thành công
        when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(mockOrderStatus));

        // Giả lập người dùng có giỏ hàng rỗng
        when(cartItemService.findAllByUserId()).thenReturn(List.of());

        // Thực hiện test và kiểm tra ngoại lệ
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart(orderDTO);
        });

        // So sánh nội dung exception
        assertEquals("Cart is empty. Cannot create order.", exception.getMessage());

        // Đảm bảo không có đơn hàng hay item nào được lưu
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(cartItemService, times(1)).findAllByUserId();
    }

    // SI014: Kiểm tra tạo đơn hàng từ giỏ hàng khi chưa đăng nhập
    @Test
    void testCreateOrderFromCart_UserNotLoggedIn() {
        // Kiểm tra khi chưa đăng nhập thì không tạo được đơn hàng

        // Tạo order DTO như bình thường
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setPhoneNumber("123456789");
        orderDTO.setFirstName("John");
        orderDTO.setLastName("Doe");
        orderDTO.setAddress("123 Test Street");
        orderDTO.setStatus(1L);

        // Giả lập khi gọi lấy user đang đăng nhập thì ném lỗi
        when(authService.getCurrentUser()).thenThrow(new RuntimeException("User is not logged in"));

        // Gọi hàm và kiểm tra ngoại lệ
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart(orderDTO);
        });

        // Kiểm tra thông báo lỗi chính xác
        assertEquals("User is not logged in", exception.getMessage());

        // Đảm bảo không có hành động lưu nào được thực hiện
        verify(authService, times(1)).getCurrentUser();
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(cartItemService, never()).findAllByUserId();
    }

}
