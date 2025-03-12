package hr.java.web.javawebproject.service;



import hr.java.web.javawebproject.exception.ResourceNotFoundException;
import hr.java.web.javawebproject.model.*;
import hr.java.web.javawebproject.repositories.CartItemRepository;
import hr.java.web.javawebproject.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;

    @Transactional
    public Order placeOrder(User user, String paymentMethod) {
        // get cart
        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        // optional: handle PayPal integration or COD logic
        // For now, assume we have validated payment externally if PayPal

        // build order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            BigDecimal itemPrice = p.getPrice(); // store current price
            int quantity = ci.getQuantity();
            BigDecimal lineTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(p)
                    .price(itemPrice)
                    .quantity(quantity)
                    .build();
            order.getItems().add(oi);

            // optional: reduce stock from product if needed
        }

        order.setTotal(total);
        Order savedOrder = orderRepo.save(order);

        // clear cart
        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepo.findByUserId(user.getId());
    }

    public Order getOrder(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    // Admin filter
    public List<Order> filterOrders(Long userId, LocalDateTime start, LocalDateTime end) {
        if (userId != null && start != null && end != null) {
            return orderRepo.findByUserIdAndOrderDateBetween(userId, start, end);
        } else if (userId != null) {
            return orderRepo.findByUserId(userId);
        } else if (start != null && end != null) {
            return orderRepo.findByOrderDateBetween(start, end);
        } else {
            return orderRepo.findAll();
        }
    }
}
