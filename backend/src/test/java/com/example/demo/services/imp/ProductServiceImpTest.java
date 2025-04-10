package com.example.demo.services.imp;

import com.example.demo.entities.Product;
import com.example.demo.models.ProductDTO;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.services.Imp.ProductServiceImp;

import org.springframework.test.util.ReflectionTestUtils;
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

class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImp productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Thiết lập giá trị cho biến modelMapper bên trong ProductServiceImp
        ReflectionTestUtils.setField(productService, "modelMapper", modelMapper);
    }

    // SI027: Kiểm tra lấy danh sách toàn bộ sản phẩm
    @Test
    void testFindAllProducts() {
        // Mô phỏng 2 sản phẩm giả định có trong cơ sở dữ liệu
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(100L);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(200L);

        // Khi gọi repository để lấy danh sách sản phẩm thì trả về 2 sản phẩm trên
        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(mockProducts);

        // Gọi hàm cần kiểm thử
        List<Product> products = productService.findAll();

        // Kiểm tra kết quả đúng với kỳ vọng
        assertEquals(2, products.size());
        assertEquals("Product 1", products.get(0).getName());
        assertEquals(100L, products.get(0).getPrice());
        verify(productRepository, times(1)).findAll();
    }

    // SI028: Kiểm tra lấy danh sách sản phẩm rỗng
    @Test
    void testFindAll_NoProducts() {
        // Mô phỏng trường hợp không có sản phẩm nào trong DB
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<Product> products = productService.findAll();

        // Kỳ vọng danh sách sản phẩm trả về rỗng
        assertTrue(products.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    // SI029: Kiểm tra lấy sản phẩm theo ID
    @Test
    void testGetProductById_Success() {
        // Mô phỏng tìm thấy sản phẩm với ID là 1
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Product 1");
        mockProduct.setPrice(100L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product product = productService.get(1L);

        // Kiểm tra sản phẩm đúng như mong đợi
        assertNotNull(product);
        assertEquals("Product 1", product.getName());
        assertEquals(100L, product.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    // SI030: Kiểm tra lấy sản phẩm theo ID không tồn tại
    @Test
    void testGetProductById_NotFound() {
        // Mô phỏng không tìm thấy sản phẩm
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Kỳ vọng hàm get() ném ra ngoại lệ vì không có sản phẩm
        assertThrows(ResponseStatusException.class, () -> productService.get(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    // SI031: Kiểm tra tạo sản phẩm mới
    @Test
    void testCreateProduct() {
        // Tạo một DTO đại diện cho sản phẩm cần tạo
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Product");
        productDTO.setPrice(150L);

        // Mô phỏng sản phẩm sau khi được ánh xạ từ DTO và lưu vào DB
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("New Product");
        mockProduct.setPrice(150L);

        // Mock hành vi ánh xạ và lưu vào DB
        when(modelMapper.map(productDTO, Product.class)).thenReturn(mockProduct);
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);

        // Gọi phương thức create
        Long productId = productService.create(productDTO);

        // Kỳ vọng trả về ID sản phẩm mới được tạo
        assertNotNull(productId);
        assertEquals(1L, productId);
        verify(productRepository, times(1)).save(mockProduct);
    }

    // SI032: Kiểm tra cập nhật thông tin sản phẩm
    @Test
    void testUpdateProduct_Success() {
        // productDTO đại diện cho sản phẩm mới cần cập nhật
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setPrice(200L);

        // Sản phẩm cũ (trong DB)
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Product");
        existingProduct.setPrice(100L);

        // Product mới được map từ DTO
        Product mappedProduct = new Product();
        mappedProduct.setId(1L); // mapToEntity sẽ giữ lại ID
        mappedProduct.setName("Updated Product");
        mappedProduct.setPrice(200L);

        // Mock hành vi
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(modelMapper.map(productDTO, Product.class)).thenReturn(mappedProduct);
        when(productRepository.save(mappedProduct)).thenReturn(mappedProduct);

        // Act
        Product updatedProduct = productService.update(1L, productDTO);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(200L, updatedProduct.getPrice());

        verify(productRepository).findById(1L);
        verify(modelMapper).map(productDTO, Product.class);
        verify(productRepository).save(mappedProduct);
    }

    // SI033: Kiểm tra cập nhật sản phẩm không tồn tại
    @Test
    void testUpdateProduct_NotFound() {
        // Trường hợp không tìm thấy sản phẩm cần cập nhật
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setPrice(200L);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Kỳ vọng hàm update sẽ ném ra ngoại lệ nếu không tìm thấy
        assertThrows(ResponseStatusException.class, () -> productService.update(1L, productDTO));
        verify(productRepository, times(1)).findById(1L);
    }

    // SI034: Kiểm tra xóa sản phẩm
    @Test
    void testDeleteProduct() {
        // Trường hợp xóa sản phẩm thành công
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        // Đảm bảo hàm xóa được gọi 1 lần
        verify(productRepository, times(1)).deleteById(1L);
    }

    // SI035: Kiểm tra xóa sản phẩm không tồn tại
    @Test
    void testDeleteProduct_NotFound() {
        // Mô phỏng xóa thất bại vì không tìm thấy ID
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product with id: 1"))
                .when(productRepository).deleteById(1L);

        // Kỳ vọng xảy ra lỗi khi xóa sản phẩm không tồn tại
        assertThrows(ResponseStatusException.class, () -> productService.delete(1L));
        verify(productRepository, times(1)).deleteById(1L);
    }

    // SI036: Kiểm tra tìm kiếm sản phẩm với chuỗi rỗng
    @Test
    void testSearchProducts_EmptyQuery() {
        // Trường hợp nhập chuỗi tìm kiếm rỗng
        assertThrows(ResponseStatusException.class, () -> productService.search(""));
        // Đảm bảo không có truy vấn nào được gửi đến repository
        verify(productRepository, never()).searchProducts(anyString());
    }

    // SI037: Kiểm tra tìm kiếm sản phẩm mà không có kết quả tương ứng
    @Test
    void testSearchProducts_NoResults() {
        // Trường hợp tìm kiếm không ra kết quả
        when(productRepository.searchProducts("nonexistent")).thenReturn(Arrays.asList());

        // Kỳ vọng xảy ra lỗi thông báo không tìm thấy sản phẩm nào
        assertThrows(ResponseStatusException.class, () -> productService.search("nonexistent"));
        verify(productRepository, times(1)).searchProducts("nonexistent");
    }

    // SI038: Kiểm tra tìm kiếm sản phẩm thành công
    @Test
    void testSearchProducts_Success() {
        // Mô phỏng tìm kiếm thành công ra 2 sản phẩm
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(100L);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(200L);

        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productRepository.searchProducts("query")).thenReturn(mockProducts);

        List<Product> products = productService.search("query");

        // Kiểm tra số lượng và thông tin sản phẩm được trả về
        assertEquals(2, products.size());
        assertEquals("Product 1", products.get(0).getName());
        verify(productRepository, times(1)).searchProducts("query");
    }

}
