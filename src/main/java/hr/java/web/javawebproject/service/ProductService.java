package hr.java.web.javawebproject.service;




import hr.java.web.javawebproject.exception.ResourceNotFoundException;
import hr.java.web.javawebproject.model.Category;
import hr.java.web.javawebproject.model.Product;
import hr.java.web.javawebproject.repositories.CategoryRepository;
import hr.java.web.javawebproject.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No product with ID=" + id));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    public Product createProduct(Product product, Long categoryId) {
        Category cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No category with ID=" + categoryId));
        product.setCategory(cat);
        return productRepo.save(product);
    }

    public Product updateProduct(Long id, Product productData) {
        Product existing = getProductById(id);
        existing.setName(productData.getName());
        existing.setDescription(productData.getDescription());
        existing.setPrice(productData.getPrice());
        if (productData.getStockQuantity() != null) {
            existing.setStockQuantity(productData.getStockQuantity());
        }
        // If changing category
        if (productData.getCategory() != null) {
            Long catId = productData.getCategory().getId();
            Category cat = categoryRepo.findById(catId)
                    .orElseThrow(() -> new ResourceNotFoundException("No category with ID=" + catId));
            existing.setCategory(cat);
        }
        return productRepo.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("No product with ID=" + id);
        }
        productRepo.deleteById(id);
    }
}
