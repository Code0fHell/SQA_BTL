package com.example.demo.controllers;

import com.example.demo.entities.Product;
import com.example.demo.models.ProductDTO;
import com.example.demo.services.ProductService;
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

public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * C039 - Test lấy tất cả các sản phẩm.
     * Mô tả: Kiểm tra khi người dùng yêu cầu danh sách tất cả các sản phẩm.
     */
    @Test
    void testGetAllProducts_C039() {
        // Mock dữ liệu trả về từ service
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productService.findAll()).thenReturn(mockProducts);

        // Gọi phương thức controller
        ResponseEntity<List<Product>> response = productController.getAllProducts();

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).findAll();
    }

    /**
     * C040 - Test lấy sản phẩm theo ID.
     * Mô tả: Kiểm tra khi người dùng yêu cầu sản phẩm theo ID hợp lệ.
     */
    @Test
    void testGetProductById_C040() {
        // Mock dữ liệu trả về từ service
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Product 1");

        when(productService.get(1L)).thenReturn(mockProduct);

        // Gọi phương thức controller
        ResponseEntity<Product> response = productController.getProduct(1L);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product 1", response.getBody().getName());
        verify(productService, times(1)).get(1L);
    }

    /**
     * C041 - Test tìm kiếm sản phẩm.
     * Mô tả: Kiểm tra khi người dùng tìm kiếm sản phẩm theo từ khóa.
     */
    @Test
    void testSearchProducts_C041() {
        // Mock dữ liệu trả về từ service
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productService.search("Product")).thenReturn(mockProducts);

        // Gọi phương thức controller
        ResponseEntity<List<Product>> response = productController.searchProducts("Product");

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).search("Product");
    }

    /**
     * C042 - Test tạo mới sản phẩm.
     * Mô tả: Kiểm tra khi admin tạo mới một sản phẩm.
     */
    @Test
    void testCreateProduct_C042() {
        // Sử dụng setter để thiết lập dữ liệu cho ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Product");
        productDTO.setPrice(100L);

        // Mock dữ liệu trả về từ service
        Long mockProductId = 1L;
        when(productService.create(productDTO)).thenReturn(mockProductId);

        // Gọi phương thức controller
        ResponseEntity<Long> response = productController.createProduct(productDTO);

        // Kiểm tra kết quả trả về
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockProductId, response.getBody());
        verify(productService, times(1)).create(productDTO);
    }

    /**
     * C043 - Test cập nhật sản phẩm.
     * Mô tả: Kiểm tra khi admin cập nhật thông tin sản phẩm.
     */
    @Test
    void testUpdateProduct_C043() {
        // Sử dụng setter để thiết lập dữ liệu cho ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setPrice(150L);

        // Mock dữ liệu trả về từ service
        Product mockUpdatedProduct = new Product();
        mockUpdatedProduct.setId(1L);
        mockUpdatedProduct.setName("Updated Product");
        mockUpdatedProduct.setPrice(150L);

        when(productService.update(1L, productDTO)).thenReturn(mockUpdatedProduct);

        // Gọi phương thức controller
        ResponseEntity<Product> response = productController.updateProduct(1L, productDTO);

        // Kiểm tra kết quả trả về
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Product", response.getBody().getName());
        verify(productService, times(1)).update(1L, productDTO);
    }

    /**
     * C044 - Test xóa sản phẩm.
     * Mô tả: Kiểm tra khi admin xóa một sản phẩm theo ID.
     */
    @Test
    void testDeleteProduct_C044() {
        // Gọi phương thức controller
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Kiểm tra kết quả trả về
        assertEquals(204, response.getStatusCodeValue());
        verify(productService, times(1)).delete(1L);
    }
}