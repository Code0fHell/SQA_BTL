package com.example.demo.repositories;

import com.example.demo.entities.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cho UserRepository – kiểm tra các truy vấn với user (username, email, providerId, keyword,...)
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Hàm tạo user để sử dụng trong các test case.
     */
    private User createUser(String username, String email, String password, String providerId) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreated(new Date());
        user.setPhone("0123456789");
        user.setProviderId(providerId);
        user.setUserStatus(true);
        return user;
    }

    /**
     * R018 - Kiểm tra tìm user theo username.
     */
    @Test
    @DisplayName("Tìm user theo username")
    void testFindByUsername() {
        User user = createUser("john_doe", "john@example.com", "password", "google");
        userRepository.save(user);

        User found = userRepository.findByUsername("john_doe");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("john@example.com");
    }

    /**
     * R019 - Kiểm tra phương thức existsByUsername.
     */
    @Test
    @DisplayName("Kiểm tra tồn tại username")
    void testExistsByUsername() {
        User user = createUser("jane_doe", "jane@example.com", "pass", "local");
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("jane_doe");

        assertThat(exists).isTrue();
    }

    /**
     * R020 - Kiểm tra phương thức existsByEmail.
     */
    @Test
    @DisplayName("Kiểm tra tồn tại email")
    void testExistsByEmail() {
        User user = createUser("userx", "userx@example.com", "pass", "local");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("userx@example.com");

        assertThat(exists).isTrue();
    }

    /**
     * R021 - Tìm user theo username, providerId, email – dùng trong xác thực OAuth + email.
     */
    @Test
    @DisplayName("Tìm theo username + providerId + email")
    void testFindByUsernameAndProviderIdAndEmail() {
        User user = createUser("multi_login", "multi@example.com", "1234", "google");
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsernameAndProviderIdAndEmail(
            "multi_login", "google", "multi@example.com"
        );

        assertThat(result).isPresent();
    }

    /**
     * R022 - Tìm user theo username và providerId – ví dụ với OAuth.
     */
    @Test
    @DisplayName("Tìm theo username + providerId")
    void testFindByUsernameAndProviderId() {
        User user = createUser("oauth_user", "oauth@example.com", "xyz", "google");
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsernameAndProviderId(
            "oauth_user", "google"
        );

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("oauth@example.com");
    }

    /**
     * R023 - Tìm user theo email và providerId – dùng khi login với email qua OAuth.
     */
    @Test
    @DisplayName("Tìm theo email + providerId")
    void testFindByEmailAndProviderId() {
        User user = createUser("provider_test", "provider@example.com", "123", "google");
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmailAndProviderId(
            "provider@example.com", "google"
        );

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("provider_test");
    }

    /**
     * R024 - Tìm kiếm user theo từ khóa chứa trong username.
     */
    @Test
    @DisplayName("Tìm user theo từ khóa khớp username")
    void testSearchUserByUsernameKeyword() {
        User user = createUser("awesome_user", "awesome@example.com", "pass", "local");
        userRepository.save(user);

        List<User> result = userRepository.searchUser("some");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).contains("some");
    }

    /**
     * R025 - Kiểm tra khi không có user nào khớp từ khóa tìm kiếm.
     */
    @Test
    @DisplayName("Không tìm thấy user với từ khóa không khớp")
    void testSearchUserNoMatch() {
        User user = createUser("hidden_user", "hidden@example.com", "hiddenpass", "local");
        userRepository.save(user);

        List<User> result = userRepository.searchUser("notfound");

        assertThat(result).isEmpty();
    }
}
