package ru.practicum.ewm.main.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    public NewCategoryDto() {
    }

    public NewCategoryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
