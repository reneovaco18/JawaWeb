package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.OrderService;
import hr.java.web.javawebproject.service.PayPalPaymentService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PayPalController {

    private final PayPalPaymentService payPalService;
    private final OrderService orderService;
    private final UserService userService;

    @Value("${app.frontendUrl}")
    private String frontendUrl;

    /**
     * Step 1: Browser GET → this returns a 302 that the browser will follow (to PayPal)
     */
    @GetMapping("/checkout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public RedirectView checkout(Authentication auth) {
        User user = userService.getByEmail(auth.getName());
        BigDecimal total = orderService.computeCartTotal(user);

        Map<String, Object> resp = payPalService.createOrder(total, "USD");
        String approvalUrl = (String) resp.get("approvalUrl");
        return new RedirectView(approvalUrl);
    }

    /**
     * Step 2: PayPal calls this with ?token=… after approval; we capture and then send the user back
     */
    @GetMapping("/return")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public RedirectView complete(
            String token,
            Authentication auth
    ) {
        payPalService.captureOrder(token);
        User user = userService.getByEmail(auth.getName());
        Order order = orderService.finalizePayPalOrder(user);
        // Redirect back to your Vue app’s order details page
        return new RedirectView(frontendUrl + "/order/" + order.getId());
    }
}
