package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.dto.ProductDto;
import hr.java.web.javawebproject.model.Category;
import hr.java.web.javawebproject.model.Product;
import hr.java.web.javawebproject.repositories.CategoryRepository;
import hr.java.web.javawebproject.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // -------------------- LOCAL STORAGE CONSTANT --------------------
    // Change this to the folder where you want to store images.
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";


    public ProductController(ProductRepository productRepository,
                             CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // -------------------- GET ALL --------------------
    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // -------------------- GET BY ID --------------------
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    // -------------------- GET BY CATEGORY --------------------
    @GetMapping("/by-category/{categoryId}")
    public List<Product> getByCategory(@PathVariable Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // -------------------- CREATE --------------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@RequestBody ProductDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    // -------------------- UPDATE --------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(category);

        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    // -------------------- DELETE --------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- UPLOAD IMAGE (LOCAL STORAGE) --------------------
    @PostMapping("/{id}/uploadImage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        try {
            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate a unique file name while preserving the original file extension if available.
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = "product-" + id + "-" + System.currentTimeMillis() + fileExtension;

            // Save the file locally
            File dest = new File(uploadDir, filename);
            file.transferTo(dest);

            // Construct a URL that can be used to access the file.
            // This assumes that you have configured static resource mapping for the UPLOAD_DIR.
            String publicUrl = "http://localhost:8085/uploads/" + filename;



            // Save the file URL in the product record
            product.setImage(publicUrl);
            productRepository.save(product);

            return ResponseEntity.ok(product);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
