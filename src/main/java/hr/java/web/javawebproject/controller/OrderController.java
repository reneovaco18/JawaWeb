package hr.java.web.javawebproject.controller;



import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.OrderService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Add explicit role requirement
    public List<Order> getUserOrders(Authentication auth) {
        User user = userService.getByEmail(auth.getName());
        return orderService.getUserOrders(user);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Order getOne(Authentication auth, @PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        if (!order.getUser().getId().equals(userService.getByEmail(auth.getName()).getId())) {
            throw new AccessDeniedException("You don't own this order");
        }
        return order;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Order placeOrder(Authentication auth, @RequestParam String paymentMethod) {
        User user = userService.getByEmail(auth.getName());
        return orderService.placeOrder(user, paymentMethod);
    }
}
