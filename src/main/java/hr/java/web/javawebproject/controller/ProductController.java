package hr.java.web.javawebproject.controller;



import hr.java.web.javawebproject.model.Product;
import hr.java.web.javawebproject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/by-category/{catId}")
    public List<Product> getByCategory(@PathVariable Long catId) {
        return productService.getProductsByCategory(catId);
    }
}
