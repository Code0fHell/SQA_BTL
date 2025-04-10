package com.example.demo.services.imp;

import com.example.demo.entities.CartItem;
import com.example.demo.entities.Product;
import com.example.demo.entities.Size;
import com.example.demo.entities.User;
import com.example.demo.models.CartItemDTO;
import com.example.demo.repositories.CartItemRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.SizeRepository;
import com.example.demo.security.AuthService;
import com.example.demo.services.Imp.CartItemServiceImp;
import com.example.demo.services.Imp.ProductServiceImp;
import com.example.demo.services.Imp.SizeServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartItemServiceImpTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SizeRepository sizeRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ProductServiceImp productService;

    @Mock
    private SizeServiceImp sizeService;

    @Mock
    private ModelMapper modelMapper;

    private CartItemServiceImp cartItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo service cần test, truyền các dependency bắt buộc qua constructor
        cartItemService = new CartItemServiceImp(cartItemRepository, userRepository, productRepository, sizeRepository);

        // Tiêm các dependency còn lại vào service thông qua Reflection (không dùng constructor)
        ReflectionTestUtils.setField(cartItemService, "authService", authService);
        ReflectionTestUtils.setField(cartItemService, "productService", productService);
        ReflectionTestUtils.setField(cartItemService, "sizeService", sizeService);
        ReflectionTestUtils.setField(cartItemService, "modelMapper", modelMapper);
    }

    // SI001: Kiểm tra việc tạo mới một mục giỏ hàng thành công - Add to Cart
    @Test
    void testCreateCartItem() {
        // Kiểm tra việc tạo mới một mục giỏ hàng thành công

        // Tạo DTO mô phỏng dữ liệu gửi lên từ client
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(1L);
        cartItemDTO.setSize(1L);
        cartItemDTO.setQuantity(2);

        // Tạo user, product, size và cartItem mock để sử dụng trong test
        User mockUser = new User();
        mockUser.setId(1L);

        Product mockProduct = new Product();
        mockProduct.setId(1L);

        Size mockSize = new Size();
        mockSize.setSizeId(1L);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setQuantity(2);
        mockCartItem.setUser(mockUser);
        mockCartItem.setProduct(mockProduct);
        mockCartItem.setSize(mockSize);

        // Giả lập hành vi lấy user đang đăng nhập
        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(authService.getCurrentUserId()).thenReturn(1L);

        // Giả lập lấy product và size thành công
        when(productService.get(1L)).thenReturn(mockProduct);
        when(sizeService.get(1L)).thenReturn(mockSize);

        // Giả lập việc lưu cart item vào repository
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(mockCartItem);

        // Gọi phương thức cần test
        CartItem createdCartItem = cartItemService.create(cartItemDTO);

        // Kiểm tra kết quả trả về
        assertNotNull(createdCartItem);
        assertEquals(1L, createdCartItem.getId());
        assertEquals(2, createdCartItem.getQuantity());

        // Kiểm tra phương thức save đã được gọi đúng 1 lần
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    // SI002: Kiểm tra việc tạo mới một mục giỏ hàng với sản phẩm không tồn tại
    @Test
    void testCreateCartItem_ProductDoesNotExist() {
        // Kiểm tra khi product không tồn tại sẽ ném ra exception

        // Tạo DTO với productId không tồn tại
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(999L); // ID không tồn tại
        cartItemDTO.setSize(1L);
        cartItemDTO.setQuantity(2);

        // Tạo user mock
        User mockUser = new User();
        mockUser.setId(1L);

        // Giả lập lấy user đang đăng nhập thành công
        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(authService.getCurrentUserId()).thenReturn(1L);

        // Giả lập việc product không tìm thấy, ném RuntimeException
        when(productService.get(999L)).thenThrow(new RuntimeException("Cannot find product with id: 999"));

        // Gọi phương thức create và kiểm tra exception được ném ra đúng
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartItemService.create(cartItemDTO);
        });

        // Kiểm tra nội dung exception
        assertEquals("Cannot find product with id: 999", exception.getMessage());

        // Đảm bảo phương thức get của productService đã được gọi đúng
        verify(productService, times(1)).get(999L);

        // Đảm bảo không có cart item nào được lưu
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    // SI003: Kiểm tra việc tạo mới một mục giỏ hàng khi người dùng chưa đăng nhập
    @Test
    void testCreateCartItem_UserNotLoggedIn() {
        // Kiểm tra khi chưa đăng nhập thì không tạo được mục giỏ hàng

        // Tạo DTO như bình thường
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(1L);
        cartItemDTO.setSize(1L);
        cartItemDTO.setQuantity(2);

        // Giả lập trường hợp người dùng chưa đăng nhập -> ném exception
        when(authService.getCurrentUser()).thenThrow(new RuntimeException("User is not logged in"));

        // Gọi phương thức create và mong đợi exception xảy ra
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartItemService.create(cartItemDTO);
        });

        // Kiểm tra nội dung thông báo lỗi
        assertEquals("User is not logged in", exception.getMessage());

        // Đảm bảo phương thức lấy user được gọi đúng 1 lần
        verify(authService, times(1)).getCurrentUser();

        // Đảm bảo không lưu gì vào repository
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }
}
