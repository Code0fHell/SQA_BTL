package com.example.demo.repositories;

import com.example.demo.entities.Comment;
import com.example.demo.entities.Post;
import com.example.demo.entities.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Test cho CommentRepository.
 * Dùng @DataJpaTest để kiểm thử JPA Repository trong môi trường đơn giản và nhẹ.
 */
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Hàm tạo một đối tượng Post với tiêu đề cho trước và lưu vào database.
     */
    private Post createPost(String title) {
        Post post = new Post();
        post.setTitle(title);
        post.setBody("Body of " + title);
        post.setCreateDate(new Date());
        post.setModifyDate(new Date());
        return postRepository.save(post);
    }

    /**
     * Hàm tạo một đối tượng User với username cho trước và lưu vào database.
     */
    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        user.setEmail(username + "@example.com");
        user.setPhone("0123456789");
        user.setCreated(new Date());
        user.setProviderId("local");
        user.setUserStatus(true);
        return userRepository.save(user);
    }

    /**
     * Hàm tạo một comment và lưu vào database.
     */
    private Comment createComment(String body, User user, Post post) {
        Comment comment = new Comment();
        comment.setBody(body);
        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    /**
     * R004 - Test tìm kiếm một comment theo ID.
     * Kiểm tra xem kết quả trả về có tồn tại và đúng nội dung không.
     */
    @Test
    @DisplayName("Tìm comment theo ID")
    void testFindOneById() {
        // Tạo dữ liệu mẫu
        User user = createUser("commenter");
        Post post = createPost("Bài viết có comment");
        Comment comment = createComment("Comment đầu tiên", user, post);

        // Truy vấn comment theo ID
        Comment found = commentRepository.findOneById(comment.getId());

        // Kiểm tra kết quả
        assertThat(found).isNotNull(); // Không null
        assertThat(found.getBody()).isEqualTo("Comment đầu tiên"); // Đúng nội dung
        assertThat(found.getPost().getId()).isEqualTo(post.getId()); // Liên kết đúng bài viết
    }

    /**
     * R005 - Test tìm tất cả comment theo postId.
     * Kỳ vọng: trả về danh sách comment tương ứng với bài viết.
     */
    @Test
    @DisplayName("Tìm tất cả comment theo postId")
    void testFindAllByPostId() {
        // Tạo user và post
        User user = createUser("tester");
        Post post = createPost("Post nhiều comment");

        // Thêm nhiều comment
        createComment("Cmt 1", user, post);
        createComment("Cmt 2", user, post);
        createComment("Cmt 3", user, post);

        // Truy vấn tất cả comment theo postId
        List<Comment> comments = commentRepository.findAllByPostId(post.getId());

        // Kiểm tra số lượng và nội dung
        assertThat(comments).hasSize(3);
        assertThat(comments).extracting(Comment::getBody).contains("Cmt 1", "Cmt 2", "Cmt 3");
    }

    /**
     * R006 - Test trường hợp không có comment nào nếu postId không tồn tại.
     * Kỳ vọng: danh sách trả về rỗng.
     */
    @Test
    @DisplayName("Không có comment nếu postId không đúng")
    void testFindAllByInvalidPostId() {
        // Truy vấn comment với postId không tồn tại
        List<Comment> comments = commentRepository.findAllByPostId(999L);

        // Kết quả phải rỗng
        assertThat(comments).isEmpty();
    }
}
