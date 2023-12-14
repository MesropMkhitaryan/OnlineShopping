package com.example.productservice.service;

import com.example.productservice.config.JwtParser;
import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.dto.ProductUpdateRequest;
import com.example.productservice.dto.User;
import com.example.productservice.model.Bucket;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.BucketRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.util.ImgUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CategoryService categoryService;
    private final BucketRepository bucketRepository;
    private final ModelMapper mapper;
    private final JwtParser jwtParser;
    private final ImgUtil imageUtil;


    public Product save(ProductRequest request, String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        User user = jwtParser.parseToken(token);
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
    @Transactional
    public void delete(UUID id) {
        Product product = findById(id);
        List<Bucket> bucketsByProductId = bucketRepository.findBucketsByProductId(id);
        for (Bucket bucket : bucketsByProductId) {
            for (Product bucketProduct : bucket.getProduct()) {
                if (bucketProduct.getId().equals(id)){
                    bucket.getProduct().remove(product);
                    bucketRepository.save(bucket);
                }
            }
        }
        product.setDeleted(true);
        repository.save(product);
    }

    public Product findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("There is no product with: " + id + " :id"));
    }

    public Product update(UUID id, ProductUpdateRequest request) {
        Product product = findById(id);
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        return repository.save(product);
    }

    public List<ProductResponse> findByUserId(String authHeader){
        String token = authHeader.substring("Bearer ".length());
        User user = jwtParser.parseToken(token);
        List<Product> productsByUserId = repository.findAllByUserIdAndIsDeletedFalse(user.getId());
        return productsByUserId.stream()
                .map(product -> mapper.map(product, ProductResponse.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ProductResponse> findByCategotyId(UUID categoryId){
        List<Product> productsByCategoryID = repository.findAllByCategoryId(categoryId);
        return productsByCategoryID.stream()
                .map(product -> mapper.map(product, ProductResponse.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public UUID findByToken(String authHeader) {
        String token = authHeader.substring("Bearer ".length());

        User user = jwtParser.parseToken(token);
        return user.getId();
    }
}
