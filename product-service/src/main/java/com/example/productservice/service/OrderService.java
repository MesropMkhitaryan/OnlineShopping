package com.example.productservice.service;

import com.example.productservice.config.JwtParser;
import com.example.productservice.dto.OrderRequest;
import com.example.productservice.dto.User;
import com.example.productservice.model.Order;
import com.example.productservice.model.OrderStatus;
import com.example.productservice.model.OrdersProducts;
import com.example.productservice.model.Product;
import com.example.productservice.repository.OrderRepository;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final BucketService bucketService;
    private final JwtParser jwtParser;
    private final ProductRepository productRepository;

    public Order orderProduct(OrderRequest orderRequest, String authHeader) {
        if (orderRequest.getProductIds().size() != 0) {
            String token = authHeader.substring("Bearer ".length());
            User user = jwtParser.parseToken(token);
            Order order = Order.builder()
                    .id(null)
                    .userId(user.getId())
                    .status(OrderStatus.UNPAID)
                    .sum(0.0)
                    .orderProducts(new ArrayList<>())
                    .build();

            order = orderRepository.save(order);
            Order finalOrder = order;
            List<OrdersProducts> orderProducts = orderRequest.getProductIds()
                    .stream()
                    .map(productId -> {
                        Product product = productService.findById(productId);
                        Integer orderQuantity = orderRequest.getQuantity().get(productId);
                        product.setQuantity(product.getQuantity() - orderQuantity);
                        productRepository.save(product);
                        bucketService.deleteProductFromBucket(productId, authHeader);
                        OrdersProducts orderProduct = new OrdersProducts();
                        orderProduct.setProduct(product);
                        orderProduct.setQuantity(orderQuantity);
                        orderProduct.setOrder(finalOrder);
                        return orderProduct;
                    })
                    .collect(Collectors.toList());

            double sum = orderProducts.stream()
                    .mapToDouble(orderProduct ->
                            orderProduct.getProduct().getPrice() * orderProduct.getQuantity())
                    .sum();

            order.setSum(sum);
            order.setOrderProducts(orderProducts);
            order = orderRepository.save(order);


            return order;
        }
        return null;
    }


}
