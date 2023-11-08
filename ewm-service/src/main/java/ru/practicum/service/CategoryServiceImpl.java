package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

import static ru.practicum.Constant.ALREADY_EXIST_CATEGORY;
import static ru.practicum.Constant.NOT_FOUND_CATEGORY;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        if (categoryRepository.existsAllByName(category.getName())) {
            throw new ValidationException(String.format(ALREADY_EXIST_CATEGORY, category.getName()));
        }
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(saved);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        if (categoryRepository.existsAllByName(categoryDto.getName())) {
            Category catStorage = categoryRepository.findAllByName(categoryDto.getName()).get(0);
            if (catStorage.getId() != catId) {
                throw new ValidationException(String.format(ALREADY_EXIST_CATEGORY, categoryDto.getName()));
            }
        }
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_CATEGORY, catId)));
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format(NOT_FOUND_CATEGORY, catId));
        }
        if (eventRepository.existsAllByCategoryId(catId)) {
            throw new ValidationException("The category is not empty.");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Category> allCategories = categoryRepository.findAll(pageRequest);
        return CategoryMapper.listToCategoryDto(allCategories);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryByID(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_CATEGORY, catId)));
        return CategoryMapper.toCategoryDto(category);
    }

}
