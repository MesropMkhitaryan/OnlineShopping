package com.example.productservice.service.impl;

import com.example.productservice.dto.request.ProductRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.feign.Role;
import com.example.productservice.feign.User;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.BucketService;
import com.example.productservice.util.IOUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private CategoryServiceImpl categoryService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private BucketService bucketService;

    @Mock
    private IOUtil imageUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveProduct() {
        String authHeader = "fakeAuthHeader";
        User user = createUser();
        ProductRequest productRequest = new ProductRequest();
        Product product = new Product();
        product.setId(UUID.randomUUID());
        Category category = createCategory();
        when(userFeignClient.getCurrentUser(authHeader)).thenReturn(user);
        when(categoryService.findById(productRequest.getCategoryId())).thenReturn(category);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(repository.save(productCaptor.capture())).thenReturn(product);

        Product resultProduct = productService.save(productRequest, authHeader);
    }

    private User createUser() {
        return new User(UUID.randomUUID(), "John", "Doe", "john.doe@example.com", "password", Role.USER, 100.0);
    }

    private Category createCategory() {
        return new Category(UUID.randomUUID(), "Category");
    }

    @Test
    void list_ShouldReturnListOfProductResponses() {
        Product product1 = new Product();
        Product product2 = new Product();

        List<Product> productList = List.of(product1, product2);

        ProductResponse response1 = new ProductResponse();
        ProductResponse response2 = new ProductResponse();

        List<ProductResponse> expectedResponseList = List.of(response1, response2);

        when(repository.findAllByIsDeletedFalse()).thenReturn(productList);
        when(mapper.map(product1, ProductResponse.class)).thenReturn(response1);
        when(mapper.map(product2, ProductResponse.class)).thenReturn(response2);

        List<ProductResponse> actualResponseList = productService.list();

        assertEquals(expectedResponseList, actualResponseList);
    }
}