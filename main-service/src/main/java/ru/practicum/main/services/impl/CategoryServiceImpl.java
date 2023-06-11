package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.exception.AlreadyExistsException;
import ru.practicum.main.exception.CategoryIsNotEmptyException;
import ru.practicum.main.exception.CategoryNotExistException;
import ru.practicum.main.mappers.CategoryMapper;
import ru.practicum.main.models.Category;
import ru.practicum.main.repositories.CategoryRepository;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.services.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new AlreadyExistsException("Категория с таким именем уже существует: " + newCategoryDto.getName());
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isEmpty()) {
            throw new RuntimeException("Название категории не может быть пустым");
        }
        Category savedCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryNotExistException("Такой категории нет " + catId));
        if (!savedCategory.getName().equals(categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyExistsException("Категория с таким именем уже существует: " + categoryDto.getName());
        } else {
            savedCategory.setName(categoryDto.getName());
            return categoryMapper.toCategoryDto(categoryRepository.save(savedCategory));
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryIsNotEmptyException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll(pageable).toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotExistException("Category doesn't exist"));
        return categoryMapper.toCategoryDto(category);
    }

}
