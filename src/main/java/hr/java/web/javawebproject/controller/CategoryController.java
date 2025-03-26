package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.model.Category;
import hr.java.web.javawebproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService catService;

    @GetMapping
    public List<Category> getAll() {
        return catService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getOne(@PathVariable Long id) {
        return catService.getCategoryById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> create(@RequestBody Category cat) {
        Category created = catService.createCategory(cat);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category cat) {
        Category updated = catService.updateCategory(id, cat);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
