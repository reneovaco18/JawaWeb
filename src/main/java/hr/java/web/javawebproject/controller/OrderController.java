package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.OrderService;
import hr.java.web.javawebproject.service.PayPalPaymentService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService         orderService;
    private final UserService          userService;
    private final PayPalPaymentService payPalService;

    /* ────────────────────────────────────────────────────────────────
       List orders for this user (or all orders if ADMIN)
    ──────────────────────────────────────────────────────────────── */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getUserOrders(Authentication auth) {
        User user = userService.getByEmail(auth.getName());
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    /* ────────────────────────────────────────────────────────────────
       Single‑order details (ADMIN can view any)
    ──────────────────────────────────────────────────────────────── */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Order> getOne(Authentication auth,
                                        @PathVariable Long orderId) {
        User  user  = userService.getByEmail(auth.getName());
        Order order = orderService.getOrder(orderId);
        boolean admin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (!admin && !order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(order);
    }

    /* ────────────────────────────────────────────────────────────────
       Place an order
       • PAYPAL  →  return JSON {approvalUrl:"…"}  (200 OK)
       • others  →  place immediately (200 OK)
    ──────────────────────────────────────────────────────────────── */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> placeOrder(Authentication auth,
                                        @RequestParam String paymentMethod) {

        User user = userService.getByEmail(auth.getName());

        /* ---------- PAYPAL branch ---------- */
        if ("PAYPAL".equalsIgnoreCase(paymentMethod)) {
            BigDecimal total = orderService.computeCartTotal(user);
            Map<String,Object> pp = payPalService.createOrder(total, "USD");
            String approvalUrl = (String) pp.get("approvalUrl");

            return ResponseEntity.ok(Map.of("approvalUrl", approvalUrl));
        }

        /* ---------- COD / other methods ---------- */
        orderService.placeOrder(user, paymentMethod);
        return ResponseEntity.ok().build();
    }
}
