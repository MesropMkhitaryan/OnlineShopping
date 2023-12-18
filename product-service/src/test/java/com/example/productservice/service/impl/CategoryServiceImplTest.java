package com.example.productservice.service.impl;

import com.example.productservice.customException.CategoryNotFoundException;
import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.model.Category;
import com.example.productservice.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void save_ShouldReturnSavedCategory() {
        CategoryRequest request = new CategoryRequest("TestCategory");
        Category expectedCategory = new Category(UUID.randomUUID(), "TestCategory");

        when(repository.save(Mockito.any(Category.class))).thenReturn(expectedCategory);

        Category savedCategory = categoryService.save(request);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(categoryCaptor.capture());

        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(expectedCategory.getTitle(), capturedCategory.getTitle());
    }

    @Test
    public void delete_ShouldDeleteCategory() {
        UUID categoryId = UUID.randomUUID();
        Category categoryProduct = new Category();
        categoryProduct.setId(categoryId);
        when(repository.findById(categoryId)).thenReturn(Optional.of(categoryProduct));

        Category categoryById = categoryService.findById(categoryId);
        assertEquals(categoryById, categoryProduct);
        categoryService.delete(categoryId);
        verify(repository, Mockito.times(1)).delete(eq(categoryById));
    }

    @Test
    public void delete_WithNonexistentCategory_ShouldThrowCategoryNotFoundException() {
        UUID nonexistentCategoryId = UUID.randomUUID();
        when(repository.findById(nonexistentCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(nonexistentCategoryId))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("There is no category with");
    }

    @Test
    public void findById_ShouldReturnCategory() {
        UUID categoryId = UUID.randomUUID();
        Category expectedCategory = Category.builder()
                .id(categoryId)
                .title("TestCategory")
                .build();
        when(repository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        Category foundCategory = categoryService.findById(categoryId);

        assertEquals(expectedCategory,foundCategory);
    }

    @Test
    public void findById_WithNonexistentCategory_ShouldThrowCategoryNotFoundException() {
        UUID nonexistentCategoryId = UUID.randomUUID();
        when(repository.findById(nonexistentCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(nonexistentCategoryId))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("There is no category with");
    }

    @Test
    public void list_ShouldReturnListOfCategories() {
        List<Category> expectedCategories = Arrays.asList(
                Category.builder().id(UUID.randomUUID()).title("Category1").build(),
                Category.builder().id(UUID.randomUUID()).title("Category2").build()
        );
        when(repository.findAll()).thenReturn(expectedCategories);

        List<Category> categories = categoryService.list();

        assertEquals(expectedCategories,categories);
    }

}