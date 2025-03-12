package hr.java.web.javawebproject.controller;


import hr.java.web.javawebproject.model.Category;
import hr.java.web.javawebproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
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
}
