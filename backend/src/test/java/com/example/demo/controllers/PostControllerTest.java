package com.example.demo.controllers;

import com.example.demo.entities.Post;
import com.example.demo.models.PostDTO;
import com.example.demo.services.PostService;
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

public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C034 - Test lấy tất cả các bài viết.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách tất cả các bài viết.
     */
    @Test
    void testGetAllPosts_C034() {
        // Mock dữ liệu trả về từ service
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setBody("This is the body of Post 1");
        post1.setCreateDate(new Date());

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setBody("This is the body of Post 2");
        post2.setCreateDate(new Date());

        List<Post> mockPosts = Arrays.asList(post1, post2);
        when(postService.findAll()).thenReturn(mockPosts);

        // Gọi phương thức controller
        ResponseEntity<?> response = postController.getAllPost();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(postService, times(1)).findAll();
    }

    /**
     * C035 - Test tạo mới bài viết.
     * Mô tả: Kiểm tra khi người dùng tạo mới một bài viết.
     */
    @Test
    void testCreatePost_C035() {
        // Sử dụng setter để thiết lập dữ liệu cho PostDTO
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("New Post");
        postDTO.setBody("This is a new post.");
        postDTO.setCreateDate(new Date());
        postDTO.setImageUrl("http://example.com/image.jpg");

        // Mock dữ liệu trả về từ service
        Long mockPostId = 1L; // ID của bài viết được tạo
        when(postService.create(postDTO)).thenReturn(mockPostId);

        // Gọi phương thức controller
        ResponseEntity<?> response = postController.create(postDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPostId, response.getBody()); // Kiểm tra ID trả về
        verify(postService, times(1)).create(postDTO);
    }

    /**
     * C036 - Test cập nhật bài viết.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin bài viết.
     */
    @Test
    void testUpdatePost_C036() {
        // Sử dụng setter để thiết lập dữ liệu cho PostDTO
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Updated Post");
        postDTO.setBody("This is an updated post.");
        postDTO.setModifyDate(new Date());
        postDTO.setImageUrl("http://example.com/updated-image.jpg");

        // Mock dữ liệu trả về từ service
        Post mockUpdatedPost = new Post();
        mockUpdatedPost.setId(1L);
        mockUpdatedPost.setTitle("Updated Post");
        mockUpdatedPost.setBody("This is an updated post.");
        mockUpdatedPost.setModifyDate(new Date());
        mockUpdatedPost.setImageUrl("http://example.com/updated-image.jpg");

        when(postService.update(1L, postDTO)).thenReturn(mockUpdatedPost);

        // Gọi phương thức controller
        ResponseEntity<?> response = postController.update(1L, postDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Post", ((Post) response.getBody()).getTitle());
        verify(postService, times(1)).update(1L, postDTO);
    }

    /**
     * C037 - Test xóa bài viết.
     * Mô tả: Kiểm tra khi người dùng xóa một bài viết theo ID.
     */
    @Test
    void testDeletePost_C037() {
        // Gọi phương thức controller
        postController.delete(1L);

        // Kiểm tra service được gọi đúng cách
        verify(postService, times(1)).delete(1L);
    }

    /**
     * C038 - Test lấy bài viết theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu bài viết theo ID hợp lệ.
     */
    @Test
    void testGetPostById_C038() {
        // Mock dữ liệu trả về từ service
        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Post 1");
        mockPost.setBody("This is post 1.");
        mockPost.setCreateDate(new Date());
        mockPost.setImageUrl("http://example.com/image.jpg");

        when(postService.get(1L)).thenReturn(mockPost);

        // Gọi phương thức controller
        ResponseEntity<?> response = postController.getPost(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Post 1", ((Post) response.getBody()).getTitle());
        verify(postService, times(1)).get(1L);
    }
}