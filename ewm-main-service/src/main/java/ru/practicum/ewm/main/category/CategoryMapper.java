package ru.practicum.ewm.main.category;

import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toEntity(NewCategoryDto dto) {
        return new Category(null, dto.getName());
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
