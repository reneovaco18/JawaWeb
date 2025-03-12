package hr.java.web.javawebproject.controller;



import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.OrderService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public List<Order> getUserOrders(Authentication auth) {
        User user = userService.getByEmail(auth.getName());
        return orderService.getUserOrders(user);
    }

    @GetMapping("/{orderId}")
    public Order getOne(Authentication auth, @PathVariable Long orderId) {
        // could also validate that this order belongs to the current user if you want
        return orderService.getOrder(orderId);
    }

    @PostMapping
    public Order placeOrder(Authentication auth, @RequestParam String paymentMethod) {
        User user = userService.getByEmail(auth.getName());
        return orderService.placeOrder(user, paymentMethod);
    }
}
