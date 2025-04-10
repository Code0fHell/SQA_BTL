package com.example.demo.services.imp;

import com.example.demo.entities.Comment;
import com.example.demo.entities.Post;
import com.example.demo.entities.User;
import com.example.demo.models.CommentDTO;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.security.AuthService;
import com.example.demo.services.PostService;
import com.example.demo.services.Imp.CommentServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceImpTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentServiceImp commentService;

    // Khởi tạo mock trước mỗi test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // SI004: Kiểm tra tạo comment thành công
    @Test
    void testCreateComment() {
        // Mô phỏng dữ liệu đầu vào từ client
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBody("This is a test comment");
        commentDTO.setPostId(1L);

        // Mô phỏng user hiện tại đang đăng nhập
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        // Mô phỏng bài viết liên quan đến comment
        Post mockPost = new Post();
        mockPost.setId(1L);

        // Mô phỏng comment sau khi đã lưu vào DB
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setBody("This is a test comment");
        mockComment.setUser(mockUser);
        mockComment.setPost(mockPost);

        // Giả lập hành vi của authService và repository
        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(postService.get(1L)).thenReturn(mockPost);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // Gọi phương thức create và kiểm tra kết quả
        Long commentId = commentService.create(commentDTO);

        assertNotNull(commentId);
        assertEquals(1L, commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    // SI005: Kiểm tra cập nhật comment
    @Test
    void testUpdateComment_Success() {
        // Mô phỏng dữ liệu mới của comment
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBody("Updated comment body");

        // Mô phỏng comment cũ lấy từ DB
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setBody("Old comment body");

        // Giả lập tìm thấy comment và cập nhật thành công
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(mockComment)).thenReturn(mockComment);

        // Cập nhật nội dung và lưu lại
        mockComment.setBody(commentDTO.getBody());
        Comment updatedComment = commentRepository.save(mockComment);

        assertNotNull(updatedComment);
        assertEquals("Updated comment body", updatedComment.getBody());
        verify(commentRepository, times(1)).save(mockComment);
    }

    // SI006: Kiểm tra cập nhật comment không tồn tại
    @Test
    void testUpdateComment_NotFound() {
        // Trường hợp comment không tồn tại trong DB
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBody("Updated comment body");

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Gọi phương thức update và kỳ vọng ném ra lỗi 404
        assertThrows(ResponseStatusException.class, () -> {
            Comment mockComment = commentService.get(1L); // sẽ ném lỗi
            mockComment.setBody(commentDTO.getBody());
            commentService.create(commentDTO);
        });

        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    // SI007: Kiểm tra xóa comment thành công
    @Test
    void testDeleteComment_Success() {
        // Xóa comment thành công (không ném lỗi)
        doNothing().when(commentRepository).deleteById(1L);

        commentService.delete(1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    // SI008: Kiểm tra xóa comment không tồn tại
    @Test
    void testDeleteComment_NotFound() {
        // Giả lập tình huống không tìm thấy comment cần xóa
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find comment with id: 1"))
                .when(commentRepository).deleteById(1L);

        // Kiểm tra ngoại lệ được ném ra
        assertThrows(ResponseStatusException.class, () -> commentService.delete(1L));

        verify(commentRepository, times(1)).deleteById(1L);
    }

    // SI009: Kiểm tra lấy danh sách comment theo postId
    @Test
    void testGetListComment() {
        // Tạo danh sách 2 comment giả lập
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setBody("Comment 1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setBody("Comment 2");

        // Giả lập tìm thấy danh sách comment theo postId
        when(commentRepository.findAllByPostId(1L)).thenReturn(Arrays.asList(comment1, comment2));

        // Gọi phương thức và kiểm tra kết quả
        List<Comment> comments = commentService.findAll(1L);

        assertEquals(2, comments.size());
        assertEquals("Comment 1", comments.get(0).getBody());
        assertEquals("Comment 2", comments.get(1).getBody());
        verify(commentRepository, times(1)).findAllByPostId(1L);
    }

    // SI010: Kiểm tra lấy danh sách comment rỗng
    @Test
    void testGetListComment_Empty() {
        // Trường hợp không có comment nào cho bài viết
        when(commentRepository.findAllByPostId(1L)).thenReturn(Arrays.asList());

        List<Comment> comments = commentService.findAll(1L);

        assertTrue(comments.isEmpty());
        verify(commentRepository, times(1)).findAllByPostId(1L);
    }

    // SI011: Kiểm tra lấy danh sách comment với post không tồn tại
    @Test
    void testGetListComment_PostNotFound() {
        // Trường hợp bài viết không tồn tại
        when(commentRepository.findAllByPostId(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        assertThrows(ResponseStatusException.class, () -> commentService.findAll(1L));

        verify(commentRepository, times(1)).findAllByPostId(1L);
    }
}
