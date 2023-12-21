package com.example.productservice.service.impl;

import com.example.productservice.customException.EmptyOrderException;
import com.example.productservice.dto.request.OrderRequest;
import com.example.productservice.feign.Role;
import com.example.productservice.feign.User;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.model.Order;
import com.example.productservice.model.OrderStatus;
import com.example.productservice.model.OrdersProduct;
import com.example.productservice.model.Product;
import com.example.productservice.repository.OrderRepository;
import com.example.productservice.repository.OrdersProductRepository;
import com.example.productservice.service.BucketService;
import com.example.productservice.service.OrderService;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BucketService bucketService;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private ProductService productService;
    @Mock
    private OrdersProductRepository orderProductRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository,bucketService,userFeignClient,productService,orderProductRepository);
    }

    @Test
    void testOrderProductWithEmptyOrderException() {
        HashMap<UUID,Integer> emptyHashMap = new HashMap<>();
        OrderRequest emptyOrderRequest = new OrderRequest(0,emptyHashMap);
        String authHeader = "your_auth_header";

        assertThrows(EmptyOrderException.class, () -> {
            orderService.orderProduct(emptyOrderRequest, authHeader);
        });
    }

    @Test
    void testOrderProduct_HappyPath() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest(50.0, (HashMap<UUID, Integer>) createProductQuantityMap());
        String authHeader = "valid-auth-token";

        User expectedUser = getUser();
        Mockito.when(userFeignClient.getCurrentUser(authHeader)).thenReturn(expectedUser);

        Order mockCreatedOrder = getOrder();
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(mockCreatedOrder);

        OrdersProduct mockOrdersProduct = getOrderProducts();
        Mockito.when(orderProductRepository.save(any(OrdersProduct.class))).thenReturn(mockOrdersProduct);

        Order actualOrder = orderService.orderProduct(orderRequest, authHeader);

        assertEquals(mockCreatedOrder.getUserId(), actualOrder.getUserId());
        assertEquals(1, actualOrder.getOrderProducts().size());
    }

    @Test
    void testOrderProduct_FeignClientFailure() {
        OrderRequest orderRequest = new OrderRequest(50.0, (HashMap<UUID, Integer>) createProductQuantityMap());
        String authHeader = "invalid-auth-token";

        Mockito.when(userFeignClient.getCurrentUser(anyString())).thenThrow(new RuntimeException("Unauthorized"));

        assertThrows(RuntimeException.class, () -> orderService.orderProduct(orderRequest, authHeader));
    }

    @Test
    void testCreateOrder() {
        User user = getUser();

        Order mockCreatedOrder = getOrder();
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(mockCreatedOrder);

        Order actualOrder = orderService.createOrder(user);

        assertEquals(mockCreatedOrder, actualOrder);
    }

    private Map<UUID, Integer> createProductQuantityMap() {
        Map<UUID, Integer> productQuantityMap = new HashMap<>();
        productQuantityMap.put(createTestProduct().getId(), createTestProduct().getQuantity());
        return productQuantityMap;
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setPrice(50.0);
        return product;
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

    Order getOrder(){
        return Order.builder()
                .userId(getUser().getId())
                .status(OrderStatus.UNPAID)
                .sum(0.0)
                .orderProducts(new ArrayList<>())
                .build();
    }

    OrdersProduct getOrderProducts(){
        return OrdersProduct.builder()
                .id(UUID.randomUUID())
                .order(getOrder())
                .product(createTestProduct())
                .quantity(1)
                .build();
    }

}