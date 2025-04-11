package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.OrderService;
import hr.java.web.javawebproject.service.PayPalPaymentService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PayPalController {

    private final PayPalPaymentService payPalPaymentService;
    private final OrderService orderService;
    private final UserService userService;

    /**
     * Endpoint to create a PayPal order.
     * This computes the order total from the current user's cart, creates the PayPal order,
     * and returns the approval URL so the front end can redirect the buyer.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayPalOrder(Authentication auth) {
        // Get the current user
        User user = userService.getByEmail(auth.getName());
        // Compute the order total from the user's cart.
        // (You can reuse your logic in OrderService.placeOrder or a dedicated method.)
        BigDecimal total = orderService.computeCartTotal(user);
        // Call PayPal to create the order
        Map<String, Object> payPalResponse = payPalPaymentService.createOrder(total, "USD");
        if (payPalResponse != null && payPalResponse.containsKey("approvalUrl")) {
            // Return the PayPal order id and approval URL to the front end
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("payPalOrderId", payPalResponse.get("id"));
            responseBody.put("approvalUrl", payPalResponse.get("approvalUrl"));
            responseBody.put("totalAmount", total);
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(500).body("Failed to create PayPal order");
        }
    }

    /**
     * Endpoint to capture a PayPal order after the buyer has approved it.
     * Once captured, this method finalizes the order by storing it in the database,
     * deducting stock, and clearing the cart.
     */
    @PostMapping("/capture")
    public ResponseEntity<?> capturePayPalOrder(@RequestParam String payPalOrderId, Authentication auth) {
        // Capture the PayPal order
        Map<String, Object> captureResponse = payPalPaymentService.captureOrder(payPalOrderId);
        // Optionally, verify the capture response (for example, check status == "COMPLETED").
        // Now finalize the order in our system.
        User user = userService.getByEmail(auth.getName());
        Order finalizedOrder = orderService.finalizePayPalOrder(user);
        // Return the finalized order details to the front end.
        return ResponseEntity.ok(finalizedOrder);
    }
}
