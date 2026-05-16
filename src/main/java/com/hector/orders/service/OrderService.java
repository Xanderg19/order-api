package com.hector.orders.service;

import com.hector.orders.exception.InsufficientStockException;
import com.hector.orders.exception.InvalidOrderStateException;
import com.hector.orders.exception.OrderNotFoundException;
import com.hector.orders.model.entity.Mapper.OrderMapper;
import com.hector.orders.model.entity.Order;
import com.hector.orders.model.entity.OrderItem;
import com.hector.orders.model.entity.Product;
import com.hector.orders.model.entity.dto.OrderDTO;
import com.hector.orders.model.entity.dto.OrderItemsDTO;
import com.hector.orders.model.entity.enums.OrderStatus;
import com.hector.orders.repository.OrderItemRepository;
import com.hector.orders.repository.OrderRepository;
import com.hector.orders.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderDTO createOrder() {

        Order order = new Order();

        order.setStatus(OrderStatus.CREATED);
        order.setTotal(BigDecimal.ZERO);
        order.setItems(new ArrayList<>());
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(savedOrder);
    }

    @Transactional
    public Order createOrderItem(Long orderId, OrderItemsDTO dto) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Orden no encontrada"));

        if (order.getStatus() != OrderStatus.CREATED) {

            throw new InvalidOrderStateException(
                    "La orden no está en estado CREATED"
            );
        }

        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que 0"
            );
        }

        Product product = productRepository.findById(dto.getProductoId())
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        if (product.getStock() < dto.getCantidad()) {

            throw new InsufficientStockException(
                    "Stock insuficiente para el producto"
            );
        }

        OrderItem item = new OrderItem();

        item.setProduct(product);
        item.setOrder(order);
        item.setQuantity(dto.getCantidad());
        item.setPrice(product.getPrice());

        order.getItems().add(item);

        product.setStock(product.getStock() - dto.getCantidad());

        BigDecimal total = order.getItems().stream()
                .map(i -> i.getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        return orderRepository.save(order);
    }

    public Product verificarStock(Long producId) {

        Product product = productRepository.findById(producId)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        if (product.getStock() <= 0) {

            throw new InsufficientStockException(
                    "Stock insuficiente para el producto"
            );
        }

        return product;
    }

    @Transactional
    public Order confirmarOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Orden no encontrada"));

        if (order.getStatus() != OrderStatus.CREATED) {

            throw new InvalidOrderStateException(
                    "Solo se pueden confirmar órdenes en estado CREATED"
            );
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {

            throw new InvalidOrderStateException(
                    "La orden debe tener al menos un item"
            );
        }

        if (order.getTotal() == null ||
                order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidOrderStateException(
                    "El total debe ser mayor que 0"
            );
        }

        order.getItems().forEach(item -> {

            Product product = productRepository.findById(
                            item.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado"));

            if (product.getStock() < item.getQuantity()) {

                throw new InsufficientStockException(
                        "Stock insuficiente para el producto: "
                                + product.getName()
                );
            }
        });

        order.setStatus(OrderStatus.CONFIRMED);

        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Orden no encontrada"));
    }
}