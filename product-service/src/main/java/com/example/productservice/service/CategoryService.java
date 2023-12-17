package com.example.productservice.service;

import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    Category save(CategoryRequest request);
    void delete(UUID id);
    Category findById(UUID id);
    List<Category> list();
}
