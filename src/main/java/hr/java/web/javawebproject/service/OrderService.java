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

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final PayPalPaymentService payPalPaymentService; // still used in other flows if needed

    /**
     * Compute the total for the current user's cart.
     */
    public BigDecimal computeCartTotal(User user) {
        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        return total;
    }

    /**
     * This method is used in the normal checkout process (e.g., Cash on Delivery).
     * It creates the order immediately.
     */
    @Transactional
    public Order placeOrder(User user, String paymentMethod) {
        if ("PAYPAL".equalsIgnoreCase(paymentMethod)) {
            // For PayPal orders, we now use a two-step process.
            // Direct placement in this endpoint for PayPal is not used.
            throw new UnsupportedOperationException("Use PayPal endpoints for PayPal orders");
        }
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
            // Reduce product stock immediately
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
        order.setTotal(total);
        Order savedOrder = orderRepo.save(order);
        cartRepo.deleteAll(cartItems);
        return savedOrder;
    }

    /**
     * Finalize a PayPal order.
     * This method is called after a successful PayPal capture and will create the Order,
     * reduce stock, and clear the user’s cart.
     */
    @Transactional
    public Order finalizePayPalOrder(User user) {
        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .paymentMethod("PAYPAL")
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            int quantity = ci.getQuantity();

            if (p.getStockQuantity() < quantity) {
                throw new ResourceNotFoundException("Insufficient stock for product: " + p.getName());
            }
            // Reduce product stock after successful PayPal capture
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
        order.setTotal(total);
        Order savedOrder = orderRepo.save(order);
        // Clear the cart for the user.
        cartRepo.deleteAll(cartItems);
        return savedOrder;
    }

    /**
     * Returns orders for a given user.
     * For an admin user, this returns all orders from all users.
     */
    public List<Order> getUserOrders(User user) {
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            return orderRepo.findAll();
        }
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
