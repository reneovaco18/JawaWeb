package hr.java.web.javawebproject.controller;



import hr.java.web.javawebproject.model.CartItem;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.service.CartService;
import hr.java.web.javawebproject.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public List<CartItem> getCart(Authentication auth) {
        // auth.getName() is the user's email from JWT
        User user = userService.getByEmail(auth.getName());
        return cartService.getCartItems(user);
    }

    @PostMapping
    public CartItem addToCart(Authentication auth,
                              @RequestParam Long productId,
                              @RequestParam int quantity) {
        User user = userService.getByEmail(auth.getName());
        return cartService.addToCart(user, productId, quantity);
    }

    @PutMapping
    public CartItem updateCartItem(Authentication auth,
                                   @RequestParam Long productId,
                                   @RequestParam int quantity) {
        User user = userService.getByEmail(auth.getName());
        return cartService.updateCartItem(user, productId, quantity);
    }

    @DeleteMapping("/{productId}")
    public void removeItem(Authentication auth,
                           @PathVariable Long productId) {
        User user = userService.getByEmail(auth.getName());
        cartService.removeCartItem(user, productId);
    }
}
