package hr.java.web.javawebproject.controller;

import hr.java.web.javawebproject.model.CartItem;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.CartService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication auth) {
        User user = userService.getByEmail(auth.getName());
        List<CartItem> cartItems = cartService.getCartItems(user);
        // Ensure products are initialized
        cartItems.forEach(item -> {
            if (item.getProduct() != null) {
                item.getProduct().getName();
            }
        });
        return ResponseEntity.ok(cartItems);
    }

    // Add a product to cart (or increase the quantity)
    @PostMapping
    public ResponseEntity<CartItem> addToCart(
            Authentication auth,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        User user = userService.getByEmail(auth.getName());
        CartItem updatedCartItem = cartService.addToCart(user, productId, quantity);
        return ResponseEntity.ok(updatedCartItem);
    }

    // Update cart item quantity (or remove if quantity <= 0)
    @PutMapping
    public ResponseEntity<CartItem> updateCartItem(
            Authentication auth,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        User user = userService.getByEmail(auth.getName());
        // If the updated quantity is zero (or less), remove the item
        if (quantity <= 0) {
            cartService.removeCartItem(user, productId);
            return ResponseEntity.noContent().build();
        }
        CartItem updatedCartItem = cartService.updateCartItem(user, productId, quantity);
        return ResponseEntity.ok(updatedCartItem);
    }

    // Remove a product from cart
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItem(
            Authentication auth,
            @PathVariable Long productId
    ) {
        User user = userService.getByEmail(auth.getName());
        cartService.removeCartItem(user, productId);
        return ResponseEntity.noContent().build();
    }
}
