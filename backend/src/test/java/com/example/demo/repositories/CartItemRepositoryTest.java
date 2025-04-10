package com.example.demo.repositories;

import com.example.demo.entities.CartItem;
import com.example.demo.entities.Product;
import com.example.demo.entities.Size;
import com.example.demo.entities.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cho CartItemRepository.
 * Sử dụng @DataJpaTest để kiểm tra tầng Repository độc lập với Service/Controller.
 */
@DataJpaTest
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    /**
     * Hàm tạo User test và lưu vào database.
     */
    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(username + "@example.com");
        user.setUserStatus(true);
        user.setProviderId("local");
        return userRepository.save(user); // Lưu user vào DB
    }

    /**
     * Hàm tạo Product test và lưu vào database.
     */
    private Product createProduct(String name) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Test Product");
        product.setMaterials("Cotton");
        product.setInstruction("Use with care");
        product.setPrice(100L);
        return productRepository.save(product);
    }

    /**
     * Hàm tạo Size test và lưu vào database.
     */
    private Size createSize(String sizeName) {
        Size size = new Size();
        size.setName(sizeName);
        return sizeRepository.save(size);
    }

    /**
     * Hàm tạo CartItem test với các thông tin user, product, size và số lượng.
     */
    private CartItem createCartItem(User user, Product product, Size size, int quantity) {
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setSize(size);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    /**
     * R001-Test chức năng lấy tất cả cart item theo userId.
     * Kỳ vọng: trả về đúng số lượng item đã thêm.
     */
    @Test
    @DisplayName("Tìm tất cả CartItem theo userId")
    void testFindAllByUserId() {
        User user = createUser("cartuser");
        Product product = createProduct("Áo sơ mi");
        Size size = createSize("M");

        // Thêm 2 cart item cho cùng user
        createCartItem(user, product, size, 2);
        createCartItem(user, product, size, 3);

        // Thực hiện truy vấn
        List<CartItem> items = cartItemRepository.findAllByUserId(user.getId());

        // Kiểm tra kết quả trả về có đúng 2 item không
        assertThat(items).hasSize(2);
    }

    /**
     * R002- Test chức năng tìm cart item theo userId và productId.
     * Kỳ vọng: truy vấn đúng một cart item đã thêm.
     */
    @Test
    @DisplayName("Tìm CartItem theo userId và productId")
    void testFindByUserIdAndProductId() {
        User user = createUser("finduser");
        Product product = createProduct("Quần jeans");
        Size size = createSize("L");

        createCartItem(user, product, size, 1);

        // Truy vấn cart item theo user và product
        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());

        // Kiểm tra không null và đúng quantity
        assertThat(item).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(1);
    }

    /**
     * R003 - Test chức năng xóa tất cả cart item theo userId.
     * Kỳ vọng: sau khi xóa, danh sách item của user là rỗng.
     */
    @Test
    @DisplayName("Xóa tất cả CartItem theo userId")
    void testDeleteAllByUserId() {
        User user = createUser("deleteuser");
        Product product = createProduct("Giày thể thao");
        Size size = createSize("42");

        // Tạo 2 cart item cho user
        createCartItem(user, product, size, 2);
        createCartItem(user, product, size, 1);

        // Xóa toàn bộ cart item của user
        cartItemRepository.deleteAllByUserId(user.getId());

        // Truy vấn lại, kỳ vọng không còn cart item nào
        List<CartItem> items = cartItemRepository.findAllByUserId(user.getId());

        assertThat(items).isEmpty();
    }
}
