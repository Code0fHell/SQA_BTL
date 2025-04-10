package com.example.demo.repositories;

import com.example.demo.entities.Order;
import com.example.demo.entities.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Test cho OrderRepository.
 * Sử dụng @DataJpaTest để test các thao tác JPA Repository một cách nhẹ nhàng.
 */
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tạo một đối tượng User và lưu vào cơ sở dữ liệu.
     *
     * @param username tên người dùng
     * @return đối tượng User đã lưu
     */
    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(username + "@mail.com");
        user.setUserStatus(true);
        user.setProviderId("local");
        return userRepository.save(user);
    }

    /**
     * Tạo một Order cho user với tổng tiền cụ thể.
     *
     * @param user  người dùng đặt đơn
     * @param total tổng tiền đơn hàng
     */
    private void createOrderForUser(User user, long total) {
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setAddress("Address");
        order.setPhone("0123456789");
        order.setFirstName("First");
        order.setLastName("Last");
        orderRepository.save(order);
    }

    /**
     * R007 - Kiểm tra phương thức findAllByUserId() có trả về đúng danh sách đơn hàng của người dùng không.
     */
    @Test
    @DisplayName("findAllByUserId() trả về đúng đơn hàng của người dùng")
    void testFindAllByUserId() {
        // Tạo 2 người dùng
        User userA = createUser("userA");
        User userB = createUser("userB");

        // Tạo các đơn hàng tương ứng
        createOrderForUser(userA, 1000L);
        createOrderForUser(userA, 2000L);
        createOrderForUser(userB, 3000L);

        // Truy vấn danh sách đơn hàng của từng người
        List<Order> ordersUserA = orderRepository.findAllByUserId(userA.getId());
        List<Order> ordersUserB = orderRepository.findAllByUserId(userB.getId());

        // Kiểm tra số lượng và người sở hữu đơn hàng
        assertThat(ordersUserA).hasSize(2);
        assertThat(ordersUserB).hasSize(1);
        assertThat(ordersUserA).allMatch(order -> order.getUser().getId().equals(userA.getId()));
    }
}
