package com.example.demo.controllers;

import com.example.demo.entities.Comment;
import com.example.demo.entities.Post;
import com.example.demo.entities.User;
import com.example.demo.models.CommentDTO;
import com.example.demo.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C014 - Test lấy tất cả các comment theo post ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách comment theo post ID hợp lệ.
     */
    @Test
    void testGetAllCommentsByPostId_C014() {
        // Mock dữ liệu trả về từ service
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setBody("Comment 1");
        comment1.setCreatedAt(new Date());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setBody("Comment 2");
        comment2.setCreatedAt(new Date());

        List<Comment> mockComments = Arrays.asList(comment1, comment2);
        // Mô phỏng hành vi của các phương thức trong CommentService
        // khi được gọi với tham số là 1L (post ID)
        when(commentService.findAll(1L)).thenReturn(mockComments);

        // Gọi phương thức controller
        ResponseEntity<?> response = commentController.getAllcommentByPostId(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(commentService, times(1)).findAll(1L);
    }

    /**
     * C015 - Test tạo mới comment.
     * Mô tả: Kiểm tra khi người dùng tạo mới một comment.
     */
    @Test
    void testCreateComment_C015() {
        // Sử dụng setter để thiết lập dữ liệu cho CommentDTO
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBody("New Comment");
        commentDTO.setUserId(1L);
        commentDTO.setPostId(1L);
        commentDTO.setCreatedAt(new Date());

        // Mock dữ liệu trả về từ service
        Long mockCommentId = 1L; // ID của comment được tạo
        when(commentService.create(commentDTO)).thenReturn(mockCommentId);

        // Gọi phương thức controller
        ResponseEntity<?> response = commentController.create(commentDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCommentId, response.getBody()); // Kiểm tra ID trả về
        verify(commentService, times(1)).create(commentDTO);
    }

    /**
     * C016 - Test xóa comment.
     * Mô tả: Kiểm tra khi người dùng xóa một comment theo ID.
     */
    @Test
    void testDeleteComment_C016() {
        // Gọi phương thức controller
        commentController.delete(1L);

        // Kiểm tra service được gọi đúng cách
        verify(commentService, times(1)).delete(1L);
    }

    /**
     * C017 - Test lấy comment theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu comment theo ID hợp lệ.
     */
    @Test
    void testGetCommentById_C017() {
        // Mock dữ liệu trả về từ service
        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setBody("Comment 1");
        mockComment.setCreatedAt(new Date());

        User user = new User();
        user.setId(1L);
        mockComment.setUser(user);

        Post post = new Post();
        post.setId(1L);
        mockComment.setPost(post);

        when(commentService.get(1L)).thenReturn(mockComment);

        // Gọi phương thức controller
        ResponseEntity<?> response = commentController.getcomment(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Comment 1", ((Comment) response.getBody()).getBody());
        verify(commentService, times(1)).get(1L);
    }
}