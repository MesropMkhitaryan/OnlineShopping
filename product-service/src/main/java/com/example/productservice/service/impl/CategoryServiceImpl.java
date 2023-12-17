package com.example.productservice.service.impl;

import com.example.productservice.customException.CategoryNotFoundException;
import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.model.Category;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public Category save(CategoryRequest request) {
        var category = Category.builder()
                .id(null)
                .title(request.getTitle())
                .build();
        return repository.save(category);
    }

    public void delete(UUID id) {
        Category category = findById(id);
        repository.delete(category);
    }

    public Category findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new CategoryNotFoundException("There is no category with: " + id + " :id"));
    }

    public List<Category> list() {
        return repository.findAll();
    }

}
