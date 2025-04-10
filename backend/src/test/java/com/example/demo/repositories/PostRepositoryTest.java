package com.example.demo.repositories;

import com.example.demo.entities.Post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Test cho PostRepository – kiểm thử các thao tác truy xuất dữ liệu với bảng Post.
 */
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    /**
     * Hàm tạo một bài viết giả lập để sử dụng trong các test case.
     *
     * @param title    tiêu đề bài viết
     * @param body     nội dung bài viết
     * @param imageUrl đường dẫn ảnh (có thể null)
     * @return đối tượng Post đã khởi tạo
     */
    private Post createPost(String title, String body, String imageUrl) {
        Post post = new Post();
        post.setTitle(title);
        post.setBody(body);
        post.setImageUrl(imageUrl);
        post.setCreateDate(new Date());
        post.setModifyDate(new Date());
        return post;
    }

    /**
     * R008 - Kiểm tra phương thức findAll() có trả về đúng số lượng bài viết đã lưu hay không.
     */
    @Test
    @DisplayName("Lấy tất cả bài viết")
    void testFindAll() {
        Post post1 = createPost("Spring Boot là gì?", "Chi tiết về Spring Boot", null);
        Post post2 = createPost("Microservices là gì?", "Hướng dẫn về kiến trúc Microservices", null);

        postRepository.save(post1);
        postRepository.save(post2);

        List<Post> posts = postRepository.findAll();

        assertThat(posts).hasSize(2);
    }

    /**
     * R009 - Kiểm tra khả năng truy xuất bài viết theo ID.
     */
    @Test
    @DisplayName("Tìm bài viết theo ID")
    void testFindById() {
        Post post = createPost("Bài viết đặc biệt", "Nội dung đặc biệt", null);
        Post saved = postRepository.save(post);

        Optional<Post> result = postRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Bài viết đặc biệt");
    }

    /**
     * R010 - Kiểm tra phương thức tìm kiếm bài viết theo từ khóa trong tiêu đề (case-insensitive).
     */
    @Test
    @DisplayName("Tìm bài viết theo từ khóa trong tiêu đề")
    void testSearchPostsByTitle() {
        Post post = createPost("Giới thiệu Django", "Django là framework Python mạnh mẽ", null);
        postRepository.save(post);

        List<Post> results = postRepository.searchPosts("django");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle().toLowerCase()).contains("django");
    }

    /**
     * R011 - Kiểm tra khi tìm kiếm với từ khóa không tồn tại, kết quả trả về rỗng.
     */
    @Test
    @DisplayName("Không tìm thấy bài viết nếu từ khóa không khớp")
    void testSearchPostsNoMatch() {
        Post post = createPost("Khóa học Spring Boot nâng cao", "Chi tiết nâng cao", null);
        postRepository.save(post);

        List<Post> results = postRepository.searchPosts("laravel");

        assertThat(results).isEmpty();
    }

    /**
     * R012 - Kiểm tra kết quả tìm kiếm khi có nhiều bài viết cùng chứa từ khóa trong tiêu đề.
     */
    @Test
    @DisplayName("Tìm nhiều bài viết với cùng từ khóa")
    void testSearchPostsMultipleResults() {
        Post post1 = createPost("Học Django cơ bản", "Giới thiệu Django", null);
        Post post2 = createPost("Tạo blog với Django", "Ứng dụng Django thực tế", null);
        postRepository.save(post1);
        postRepository.save(post2);

        List<Post> results = postRepository.searchPosts("Django");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Post::getTitle).anyMatch(t -> t.contains("Django"));
    }
}
