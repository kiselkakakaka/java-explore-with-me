package ru.practicum.ewm.main.category;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.CategoryDto;

@RestController
@RequestMapping("/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        return categoryService.getById(catId);
    }
}
