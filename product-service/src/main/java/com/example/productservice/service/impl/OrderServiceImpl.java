package com.example.productservice.service.impl;

import com.example.productservice.customException.EmptyOrderException;
import com.example.productservice.dto.request.OrderRequest;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BucketService bucketService;
    private final UserFeignClient userFeignClient;
    private final ProductService productService;
    private final OrdersProductRepository orderProductRepository;

    @Transactional
    public Order orderProduct(OrderRequest orderRequest, String authHeader) {
        if (orderRequest.getQuantity().isEmpty()) {
            throw new EmptyOrderException("Order don't have single product");
        }

        User user = userFeignClient.getCurrentUser(authHeader);

        Order order = createOrder(user);
        List<OrdersProduct> orderProducts = createOrderProducts(orderRequest, order, authHeader);

        double sum = calculateOrderSum(orderProducts);

        order.setSum(sum);
        order.setOrderProducts(orderProducts);

        return orderRepository.save(order);
    }

    private Order createOrder(User user) {
        return orderRepository.save(Order.builder()
                .userId(user.getId())
                .status(OrderStatus.UNPAID)
                .sum(0.0)
                .orderProducts(new ArrayList<>())
                .build());
    }

    private List<OrdersProduct> createOrderProducts(OrderRequest orderRequest, Order order, String authHeader) {
        return orderRequest.getQuantity().keySet().stream()
                .map(productId -> createOrderProduct(productId, orderRequest, order, authHeader))
                .collect(Collectors.toList());
    }

    private OrdersProduct createOrderProduct(UUID productId, OrderRequest orderRequest, Order order, String authHeader) {
        Product product = productService.findById(productId);
        Integer orderedProductQuantity = orderRequest.getQuantity().get(productId);

        productService.updateProductQuantity(product, orderedProductQuantity);
        bucketService.deleteProductFromBucket(productId, authHeader);

        OrdersProduct orderProduct = OrdersProduct.builder()
                .product(product)
                .quantity(orderedProductQuantity)
                .order(order)
                .build();

        return orderProductRepository.save(orderProduct);
    }



    private double calculateOrderSum(List<OrdersProduct> orderProducts) {
        return orderProducts.stream()
                .mapToDouble(orderProduct -> orderProduct.getProduct().getPrice() * orderProduct.getQuantity())
                .sum();
    }

}
