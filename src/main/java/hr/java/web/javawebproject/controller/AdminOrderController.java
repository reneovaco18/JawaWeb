package hr.java.web.javawebproject.controller;



import hr.java.web.javawebproject.model.Order;
import hr.java.web.javawebproject.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> filterOrders(@RequestParam(required = false) Long userId,
                                    @RequestParam(required = false) String startDate,
                                    @RequestParam(required = false) String endDate) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (startDate != null) {
            start = LocalDateTime.parse(startDate);
        }
        if (endDate != null) {
            end = LocalDateTime.parse(endDate);
        }
        return orderService.filterOrders(userId, start, end);
    }

    @GetMapping("/{orderId}")
    public Order getOne(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }
}
