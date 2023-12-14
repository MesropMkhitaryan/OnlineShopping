package com.example.productservice.service;

import com.example.productservice.config.JwtParser;
import com.example.productservice.dto.CategoryRequest;
import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.dto.User;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

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
        return repository.findById(id).orElseThrow(() -> new RuntimeException("There is no product with: " + id + " :id"));
    }

    public List<Category> list() {
        return repository.findAll();
    }

}
