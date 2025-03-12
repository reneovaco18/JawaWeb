package hr.java.web.javawebproject.service;




import hr.java.web.javawebproject.exception.BadRequestException;
import hr.java.web.javawebproject.exception.ResourceNotFoundException;
import hr.java.web.javawebproject.model.Category;
import hr.java.web.javawebproject.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category with ID=" + id));
    }

    public Category createCategory(Category cat) {
        // check unique?
        if (categoryRepo.findByName(cat.getName()).isPresent()) {
            throw new BadRequestException("Category name already exists");
        }
        return categoryRepo.save(cat);
    }

    public Category updateCategory(Long id, Category data) {
        Category existing = getCategoryById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return categoryRepo.save(existing);
    }

    public void deleteCategory(Long id) {
        Category existing = getCategoryById(id);
        // optionally check if it has products, etc.
        categoryRepo.delete(existing);
    }
}
