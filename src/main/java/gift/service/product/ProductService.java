package gift.service.product;

import gift.domain.product.Product;
import gift.domain.product.ProductReposiotory;
import gift.mapper.ProductMapper;
import gift.web.dto.ProductDto;
import gift.web.exception.ProductNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
// Service단에서는 DTO를 Entity로 변환해서 Repository로 넘겨주고, Entity를 DTO로 변환해서 Controller에서 넘겨주면 되나?
@Service
public class ProductService {
    private final ProductReposiotory productReposiotory;
    private final ProductMapper productMapper;

    public ProductService(ProductReposiotory productReposiotory, ProductMapper productMapper) {
        this.productReposiotory = productReposiotory;
        this.productMapper = productMapper;
    }

    public List<ProductDto> getProducts() {
        return productReposiotory.findAll()
            .stream()
            .map(productMapper::toDto)
            .toList();
    }

    public ProductDto getProductById(Long id) {
        return productReposiotory.findById(id)
            .map(productMapper::toDto)
            .orElseThrow(() -> new ProductNotFoundException("제품이 없슴다."));
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productReposiotory.save(productMapper.toEntity(productDto));
        return productMapper.toDto(product);
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productReposiotory.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("제품이 없슴다."));

        product.updateProduct(
            productDto.name(),
            productDto.price(),
            productDto.imageUrl()
        );

        return productMapper.toDto(productReposiotory.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productReposiotory.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("제품이 없슴다."));
        productReposiotory.delete(product);
    }
}
