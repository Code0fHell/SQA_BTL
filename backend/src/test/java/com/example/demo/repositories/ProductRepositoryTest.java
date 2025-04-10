package com.example.demo.repositories;

import com.example.demo.entities.Product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cho ProductRepository – Kiểm tra tính chính xác của các phương thức tìm kiếm sản phẩm.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Hàm tạo dữ liệu sản phẩm để dùng trong các test case.
     *
     * @param name        tên sản phẩm
     * @param description mô tả sản phẩm
     * @param materials   chất liệu
     * @param instruction hướng dẫn sử dụng
     * @param price       giá tiền
     * @return đối tượng Product đã được khởi tạo
     */
    private Product createProduct(String name, String description, String materials, String instruction, Long price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setMaterials(materials);
        product.setInstruction(instruction);
        product.setPrice(price);
        return product;
    }

    /**
     * R013 - Kiểm tra khả năng tìm sản phẩm bằng từ khóa trong tên sản phẩm.
     */
    @Test
    @DisplayName("Tìm kiếm sản phẩm theo tên chứa từ khóa")
    void testSearchProductsByName() {
        Product product = createProduct("Áo thun trắng", "Sản phẩm thời trang nam", "Cotton", "Giặt tay", 150000L);
        productRepository.save(product);

        List<Product> results = productRepository.searchProducts("thun");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).contains("thun");
    }

    /**
     * R014 - Kiểm tra khả năng tìm kiếm từ khóa xuất hiện trong mô tả sản phẩm.
     */
    @Test
    @DisplayName("Tìm sản phẩm theo mô tả chứa từ khóa")
    void testSearchProductByDescription() {
        Product product = createProduct("Áo sơ mi", "Chất liệu thoáng mát", "Linen", "Ủi nhẹ", 200000L);
        productRepository.save(product);

        List<Product> results = productRepository.searchProducts("thoáng");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).contains("thoáng");
    }

    /**
     * R015 - Kiểm tra kết quả rỗng khi từ khóa không khớp với tên hoặc mô tả sản phẩm nào.
     */
    @Test
    @DisplayName("Không tìm thấy sản phẩm khi từ khóa không khớp")
    void testSearchProductsNoMatch() {
        Product product = createProduct("Giày thể thao", "Giày chạy bộ chuyên dụng", "Vải", "Không giặt máy", 500000L);
        productRepository.save(product);

        List<Product> results = productRepository.searchProducts("non-existent");

        assertThat(results).isEmpty();
    }

    /**
     * R016 - Kiểm tra kết quả tìm kiếm khi có nhiều sản phẩm chứa cùng từ khóa.
     */
    @Test
    @DisplayName("Tìm thấy nhiều sản phẩm với cùng từ khóa")
    void testSearchMultipleProducts() {
        Product p1 = createProduct("Áo hoodie", "Phong cách trẻ trung", "Nỉ", "Giặt tay", 400000L);
        Product p2 = createProduct("Hoodie nữ", "Hoodie thời trang cho nữ", "Nỉ", "Giặt máy", 450000L);
        productRepository.save(p1);
        productRepository.save(p2);

        List<Product> results = productRepository.searchProducts("hoodie");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName).contains("Áo hoodie", "Hoodie nữ");
    }

    /**
     * R017 - Kiểm tra tìm kiếm không phân biệt chữ hoa và chữ thường.
     */
    @Test
    @DisplayName("Tìm kiếm không phân biệt chữ hoa chữ thường")
    void testSearchCaseInsensitive() {
        Product product = createProduct("Đầm Dạ Hội", "Trang phục sang trọng", "Lụa", "Giặt khô", 1000000L);
        productRepository.save(product);

        List<Product> results = productRepository.searchProducts("đẦm");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).containsIgnoringCase("đầm");
    }
}
