package com.example.demo.controllers;

import com.example.demo.entities.Category;
import com.example.demo.models.CategoryDTO;
import com.example.demo.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C009 - Test lấy tất cả các danh mục.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách tất cả các danh mục.
     */
    @Test
    void testGetAllCategories_C009() {
        // Mock dữ liệu trả về từ service
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> mockCategories = Arrays.asList(category1, category2);
        when(categoryService.findAll()).thenReturn(mockCategories);

        // Gọi phương thức controller
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(mockCategories, response.getBody()); // So sánh toàn bộ danh sách
        verify(categoryService, times(1)).findAll();
    }

    /**
     * C010 - Test lấy danh mục theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh mục theo ID hợp lệ.
     */
    @Test
    void testGetCategoryById_C010() {
        // Mock dữ liệu trả về từ service
        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Category 1");

        when(categoryService.get(1L)).thenReturn(mockCategory);

        // Gọi phương thức controller
        ResponseEntity<Category> response = categoryController.getCategory(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Category 1", response.getBody().getName());
        verify(categoryService, times(1)).get(1L);
    }

    /**
     * C011 - Test tạo mới danh mục.
     * Mô tả: Kiểm tra khi người dùng tạo mới một danh mục.
     */
    @Test
    void testCreateCategory_C011() {
        // Sử dụng setter để thiết lập dữ liệu cho CategoryDTO
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("New Category");

        // Mock dữ liệu trả về từ service
        when(categoryService.create(categoryDTO)).thenReturn(1L);

        // Gọi phương thức controller
        ResponseEntity<Long> response = categoryController.createCategory(categoryDTO);

        // Kiểm tra kết quả trả về
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1L, response.getBody());
        verify(categoryService, times(1)).create(categoryDTO);
    }

    /**
     * C012 - Test cập nhật danh mục.
     * Mô tả: Kiểm tra khi người dùng cập nhật thông tin danh mục.
     */
    @Test
    void testUpdateCategory_C012() {
        // Sử dụng setter để thiết lập dữ liệu cho CategoryDTO
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Category");

        // Mock dữ liệu trả về từ service
        Category mockUpdatedCategory = new Category();
        mockUpdatedCategory.setId(1L);
        mockUpdatedCategory.setName("Updated Category");

        when(categoryService.update(1L, categoryDTO)).thenReturn(mockUpdatedCategory);

        // Gọi phương thức controller
        ResponseEntity<Category> response = categoryController.updateCategory(1L, categoryDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Category", response.getBody().getName());
        verify(categoryService, times(1)).update(1L, categoryDTO);
    }

    /**
     * C013 - Test xóa danh mục.
     * Mô tả: Kiểm tra khi người dùng xóa một danh mục theo ID.
     */
    @Test
    void testDeleteCategory_C013() {
        // Gọi phương thức controller
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Kiểm tra kết quả trả về
        assertEquals(204, response.getStatusCodeValue());
        verify(categoryService, times(1)).delete(1L);
    }
}