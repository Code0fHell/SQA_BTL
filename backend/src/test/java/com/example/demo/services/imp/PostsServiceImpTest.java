package com.example.demo.services.imp;

import com.example.demo.entities.Post;
import com.example.demo.models.PostDTO;
import com.example.demo.repositories.PostRepository;
import com.example.demo.services.Imp.PostsServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostsServiceImpTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostsServiceImp postsService;

    @BeforeEach
    void setUp() {
        // Khởi tạo các mock trước mỗi test
        MockitoAnnotations.openMocks(this);
    }

    // SI015: Kiểm tra lấy danh sách toàn bộ bài viết
    @Test
    void testFindAllPosts() {
        // Mô phỏng có 2 bài viết trong cơ sở dữ liệu
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setBody("Body of Post 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setBody("Body of Post 2");

        // Khi gọi findAll() thì trả về danh sách trên
        List<Post> mockPosts = Arrays.asList(post1, post2);
        when(postRepository.findAll()).thenReturn(mockPosts);

        // Gọi hàm cần test
        List<Post> posts = postsService.findAll();

        // Kiểm tra kết quả đúng như mong đợi
        assertEquals(2, posts.size());
        assertEquals("Post 1", posts.get(0).getTitle());
        verify(postRepository, times(1)).findAll();
    }

    // SI016: Kiểm tra lấy danh sách bài viết khi không có bài nào
    @Test
    void testFindAll_NoPosts() {
        // Trường hợp không có bài viết nào
        when(postRepository.findAll()).thenReturn(Arrays.asList());

        List<Post> posts = postsService.findAll();

        // Kỳ vọng danh sách trả về rỗng
        assertTrue(posts.isEmpty());
        verify(postRepository, times(1)).findAll();
    }

    // SI017: Kiểm tra lấy bài viết theo ID
    @Test
    void testGetPostById_Success() {
        // Mô phỏng bài viết tồn tại với ID 1
        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Post 1");
        mockPost.setBody("Body of Post 1");

        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        // Gọi hàm get(id)
        Post post = postsService.get(1L);

        // Kiểm tra dữ liệu đúng như kỳ vọng
        assertNotNull(post);
        assertEquals("Post 1", post.getTitle());
        assertEquals("Body of Post 1", post.getBody());
        verify(postRepository, times(1)).findById(1L);
    }

    // SI018: Kiểm tra lấy bài viết theo ID không tìm thấy
    @Test
    void testGetPostById_NotFound() {
        // Mô phỏng không tìm thấy bài viết
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Gọi hàm get(id), kỳ vọng xảy ra lỗi (throw exception)
        assertThrows(ResponseStatusException.class, () -> postsService.get(1L));
        verify(postRepository, times(1)).findById(1L);
    }

    // SI019: Kiểm tra tạo mới bài viết
    @Test
    void testCreatePost() {
        // Dữ liệu đầu vào là một đối tượng PostDTO
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("New Post");
        postDTO.setBody("Body of New Post");

        // Mô phỏng đối tượng Post sau khi map từ DTO
        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("New Post");
        mockPost.setBody("Body of New Post");

        when(modelMapper.map(postDTO, Post.class)).thenReturn(mockPost);
        when(postRepository.save(mockPost)).thenReturn(mockPost);

        // Gọi hàm tạo mới
        Long postId = postsService.create(postDTO);

        // Kiểm tra kết quả trả về là ID đúng
        assertNotNull(postId);
        assertEquals(1L, postId);
        verify(postRepository, times(1)).save(mockPost);
    }

    // SI020: Kiểm tra tìm kiếm bài viết
    @Test
    void testSearchPosts_Success() {
        // Mô phỏng kết quả tìm kiếm trả về 2 bài viết
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setBody("Body of Post 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setBody("Body of Post 2");

        List<Post> mockPosts = Arrays.asList(post1, post2);
        when(postRepository.searchPosts("query")).thenReturn(mockPosts);

        // Gọi hàm search
        List<Post> posts = postsService.search("query");

        // Kiểm tra danh sách trả về đúng
        assertEquals(2, posts.size());
        assertEquals("Post 1", posts.get(0).getTitle());
        verify(postRepository, times(1)).searchPosts("query");
    }

    // SI021: Kiểm tra tìm kiếm bài viết không có kết quả tương ứng
    @Test
    void testSearchPosts_NoResults() {
        // Trường hợp tìm kiếm không ra kết quả
        when(postRepository.searchPosts("query")).thenReturn(Arrays.asList());

        // Gọi hàm search, kỳ vọng xảy ra lỗi
        assertThrows(ResponseStatusException.class, () -> postsService.search("query"));
        verify(postRepository, times(1)).searchPosts("query");
    }

    // SI022: Kiểm tra tìm kiếm bài viết với chuỗi rỗng
    @Test
    void testSearchPosts_EmptyQuery() {
        // Trường hợp truyền chuỗi tìm kiếm rỗng
        assertThrows(ResponseStatusException.class, () -> postsService.search(""));
        // Không được gọi vào repository khi query rỗng
        verify(postRepository, never()).searchPosts(anyString());
    }

    // SI023: Kiểm tra cập nhật bài viết
    @Test
    void testUpdatePost_Success() {
        // PostDTO chứa nội dung cập nhật
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Updated Post");
        postDTO.setBody("Updated Body");

        // Bài viết hiện tại đang tồn tại trong DB
        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Old Post");
        mockPost.setBody("Old Body");

        // Mock thao tác tìm thấy và lưu
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(postRepository.save(mockPost)).thenReturn(mockPost);

        // Gọi hàm update
        Post updatedPost = postsService.update(1L, postDTO);

        // Kiểm tra nội dung bài viết đã được cập nhật
        assertNotNull(updatedPost);
        assertEquals("Updated Post", updatedPost.getTitle());
        assertEquals("Updated Body", updatedPost.getBody());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(mockPost);
    }

    // SI024: Kiểm tra cập nhật bài viết không tồn tại
    @Test
    void testUpdatePost_NotFound() {
        // Không tìm thấy bài viết để cập nhật
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Updated Post");
        postDTO.setBody("Updated Body");

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Gọi update và kỳ vọng lỗi
        assertThrows(ResponseStatusException.class, () -> postsService.update(1L, postDTO));
        verify(postRepository, times(1)).findById(1L);
    }

    // SI025: Kiểm tra xóa bài viết
    @Test
    void testDeletePost() {
        // Trường hợp xóa thành công
        doNothing().when(postRepository).deleteById(1L);

        postsService.delete(1L);

        // Đảm bảo hàm xóa được gọi đúng
        verify(postRepository, times(1)).deleteById(1L);
    }

    // SI026: Kiểm tra xóa bài viết không tồn tại
    @Test
    void testDeletePost_NotFound() {
        // Trường hợp không tìm thấy bài viết để xóa → ném exception
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find post with id: 1"))
                .when(postRepository).deleteById(1L);

        assertThrows(ResponseStatusException.class, () -> postsService.delete(1L));
        verify(postRepository, times(1)).deleteById(1L);
    }
}
