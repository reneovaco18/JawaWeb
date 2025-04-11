package hr.java.web.javawebproject.service;

import hr.java.web.javawebproject.exception.ResourceNotFoundException;
import hr.java.web.javawebproject.model.CartItem;
import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.model.OrderItem;
import hr.java.web.javawebproject.model.Product;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.repositories.CartItemRepository;
import hr.java.web.javawebproject.repositories.OrderRepository;
import hr.java.web.javawebproject.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final PayPalPaymentService payPalPaymentService;

    /**
     * Places an order for the specified user using the provided payment method.
     * For "PAYPAL", a PayPal order is created and captured.
     * The product stock is updated (decremented) based on the ordered quantity.
     */
    @Transactional
    public Order placeOrder(User user, String paymentMethod) {
        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            int quantity = ci.getQuantity();

            if (p.getStockQuantity() < quantity) {
                throw new ResourceNotFoundException("Insufficient stock for product: " + p.getName());
            }
            p.setStockQuantity(p.getStockQuantity() - quantity);
            productRepo.save(p);

            BigDecimal itemPrice = p.getPrice();
            BigDecimal lineTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(p)
                    .price(itemPrice)
                    .quantity(quantity)
                    .build();
            order.getItems().add(oi);
        }

        if ("PAYPAL".equalsIgnoreCase(paymentMethod)) {
            Map<String, Object> createResponse = payPalPaymentService.createOrder(total, "USD");
            if (createResponse != null && createResponse.containsKey("id")) {
                String payPalOrderId = (String) createResponse.get("id");
                Map<String, Object> captureResponse = payPalPaymentService.captureOrder(payPalOrderId);
                // Optionally, store details from captureResponse if required.
            }
        }

        order.setTotal(total);
        Order savedOrder = orderRepo.save(order);
        cartRepo.deleteAll(cartItems);
        return savedOrder;
    }

    /**
     * Returns orders for a given user.
     */
    public List<Order> getUserOrders(User user) {
        return orderRepo.findByUserId(user.getId());
    }

    /**
     * Returns a single order by its ID.
     */
    public Order getOrder(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    /**
     * Returns orders filtered by user and/or date range.
     */
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
