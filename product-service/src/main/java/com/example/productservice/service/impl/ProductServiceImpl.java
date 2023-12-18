package com.example.productservice.service.impl;

import com.example.productservice.customException.ProductNotFoundException;
import com.example.productservice.dto.request.ProductRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.feign.User;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.BucketService;
import com.example.productservice.service.CategoryService;
import com.example.productservice.service.ProductService;
import com.example.productservice.util.IOUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final UserFeignClient userFeignClient;
    private final CategoryService categoryService;
    private final BucketService bucketService;
    private final ModelMapper mapper;
    private final IOUtil imageUtil;

    @Autowired
    public ProductServiceImpl(
            ProductRepository repository,
            UserFeignClient userFeignClient,
            CategoryService categoryService,
            BucketService bucketService,
            ModelMapper mapper,
            IOUtil imageUtil
    ) {
        this.repository = repository;
        this.userFeignClient = userFeignClient;
        this.categoryService = categoryService;
        this.bucketService = bucketService;
        this.mapper = mapper;
        this.imageUtil = imageUtil;
    }


    public Product save(ProductRequest request, String authHeader) {
        User user = userFeignClient.getCurrentUser(authHeader);
        Category category = categoryService.findById(request.getCategoryId());
        var product = Product.builder()
                .id(null)
                .title(request.getTitle())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .userId(user.getId())
                .category(category)
                .build();
        return repository.save(product);
    }

    public Product saveImgIntoProduct(UUID productId, MultipartFile image){
        Product byId = findById(productId);
        String photo = imageUtil.saveImageFile(image);
        byId.setPhoto(photo);
        return repository.save(byId);
    }

    public List<ProductResponse> list() {
        return repository.findAllByIsDeletedFalse()
                .stream()
                .map(product -> mapper.map(product, ProductResponse.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void delete(UUID id, String authHeader) {
        Product product = findById(id);
        bucketService.deleteProductFromMultipleBuckets(id, authHeader);
        product.setDeleted(true);
        repository.save(product);
    }

    public void updateProductQuantity(Product product, int quantity) {
        product.setQuantity(product.getQuantity() - quantity);
        repository.save(product);
    }

    public Product findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException("There is no product with: " + id + " :id"));
    }

    public Product update(UUID id, ProductUpdateRequest request) {
        Product product = findById(id);
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        return repository.save(product);
    }

    public List<ProductResponse> findAllByUserId(String authHeader){
        User user = userFeignClient.getCurrentUser(authHeader);
        List<Product> productsByUserId = repository.findAllByUserIdAndIsDeletedFalse(user.getId());
        return productsByUserId.stream()
                .map(product -> mapper.map(product, ProductResponse.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public UUID findByToken(String authHeader) {
        User user = userFeignClient.getCurrentUser(authHeader);
        return user.getId();
    }

}
