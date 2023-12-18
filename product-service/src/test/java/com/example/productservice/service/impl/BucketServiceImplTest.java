package com.example.productservice.service.impl;

import com.example.productservice.customException.BucketNotFoundException;
import com.example.productservice.feign.Role;
import com.example.productservice.feign.User;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.model.Bucket;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.BucketRepository;
import com.example.productservice.service.BucketService;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BucketServiceImplTest {

    private final BucketRepository bucketRepository =  Mockito.mock(BucketRepository.class);
    private final UserFeignClient userFeignClient = Mockito.mock(UserFeignClient.class);
    private ProductService productService = Mockito.mock(ProductService.class);
    private BucketService bucketService;
   @BeforeEach
   void setUp(){
       bucketService  =new BucketServiceImpl(bucketRepository,userFeignClient, productService);
   }

    @Test
    void testIsNewProductInBucket_ProductAlreadyExists() {
        Bucket bucket = getBucket();
        Product product = new Product();
        bucket.getProduct().add(product);

        assertFalse(bucketService.isProductNewInBucket(bucket, product));
    }

    @Test
    void testIsNewProductInBucket_EmptyBucket() {
        Bucket bucket = getBucket();
        Product newProduct = new Product();

        assertTrue(bucketService.isProductNewInBucket(bucket, newProduct));
    }

    @Test
    void testIsNewProductInBucket_NullProduct() {
        Bucket bucket = new Bucket();

        assertThrows(NullPointerException.class, () -> bucketService.isProductNewInBucket(bucket, null));
    }

    @Test
    public void testAddProductToExistingBucket() {
        //arrange
        String token = "@$#%hbuu^0";
        User user = getUser();
        Product product = getProduct();
        Bucket bucket = getBucket();
        UUID id = product.getId();
        when(userFeignClient.getCurrentUser(token)).thenReturn(user);
        when(productService.findById(id)).thenReturn(product);
        when(bucketRepository.findByUserId(getUser().getId())).thenReturn(Optional.of(bucket));

        bucketService.addProductToExistingBucket(bucket,product);

        assertFalse(bucketService.isProductNewInBucket(bucket,product));
        assertTrue(bucket.getProduct().add(product));
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    public void testAddProductToNonExistingBucket() {
        //arrange
        String token = "@$#%hbuu^0";
        User user = getUser();
        Product product = getProduct();
        UUID id = product.getId();

        when(userFeignClient.getCurrentUser(token)).thenReturn(user);
        when(productService.findById(id)).thenReturn(product);
        when(bucketRepository.findByUserId(getUser().getId())).thenReturn(Optional.empty());

        bucketService.createAndSaveNewBucket(user.getId(), product);

        verify(bucketRepository, times(1)).save(any(Bucket.class));
    }

    @Test
    void testFindByUser_BucketFound() {
        String authHeader = "fakeAuthHeader";
        User user = new User();
        Bucket bucket = new Bucket();

        when(userFeignClient.getCurrentUser(authHeader)).thenReturn(user);
        when(bucketRepository.findByUserId(user.getId())).thenReturn(Optional.of(bucket));

        Bucket resultBucket = bucketService.findByUser(authHeader);

        assertEquals(bucket, resultBucket);
        verify(userFeignClient, times(1)).getCurrentUser(authHeader);
        verify(bucketRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testFindByUser_NoBucketFound() {
        String authHeader = "fakeAuthHeader";
        User user = new User();

        when(userFeignClient.getCurrentUser(authHeader)).thenReturn(user);
        when(bucketRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        BucketNotFoundException exception = assertThrows(BucketNotFoundException.class, () ->
                bucketService.findByUser(authHeader));

        assertEquals("There is no bucket found for user with id: " + user.getId(), exception.getMessage());
        verify(userFeignClient, times(1)).getCurrentUser(authHeader);
        verify(bucketRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void deleteProductFromBucket() {

    }

    @Test
    void findByUserSuccess() {
        User user = getUser();
        String token = "@$#%hbuu^0";
        Bucket bucket = getBucket();

        when(userFeignClient.getCurrentUser(token)).thenReturn(user);
        when(bucketRepository.findByUserId(user.getId())).thenReturn(Optional.of(bucket));
   }

    @Test
    void findByUserThrowBucketNotFoundException() {
        User user = getUser();
        String token = "@$#%hbuu^0";

        when(userFeignClient.getCurrentUser(token)).thenReturn(user);
        when(bucketRepository.findByUserId(user.getId())).thenThrow(BucketNotFoundException.class);
    }

    @Test
    void removeProductFromBuckets() {
    }

    private User getUser(){
        return User.builder()
                .id(UUID.randomUUID())
                .firstName("poxos")
                .lastName("poxosyan")
                .email("poxos@mail")
                .password("12345678")
                .budget(0)
                .userRole(Role.USER)
                .build();
    }
    private Product getProduct(){
        return Product.builder()
                .id(UUID.randomUUID())
                .title("product1")
                .price(22)
                .category(new Category(UUID.randomUUID(),"cstegory"))
                .description("aaaaaa")
                .quantity(23)
                .userId(getUser().getId())
                .isDeleted(false)
                .build();
    }
    private Bucket getBucket(){
       ArrayList<Product> list = new ArrayList<>();
       list.add(getProduct());
       return Bucket.builder()
               .id(UUID.randomUUID())
               .product(list)
               .userId(getUser().getId())
               .build();
    }
}