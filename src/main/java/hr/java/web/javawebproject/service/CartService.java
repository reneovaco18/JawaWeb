package hr.java.web.javawebproject.service;

import hr.java.web.javawebproject.exception.ResourceNotFoundException;
import hr.java.web.javawebproject.model.CartItem;
import hr.java.web.javawebproject.model.Product;
import hr.java.web.javawebproject.model.User;
import hr.java.web.javawebproject.repositories.CartItemRepository;
import hr.java.web.javawebproject.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public List<CartItem> getCartItems(User user) {
        List<CartItem> items = cartRepo.findByUserId(user.getId());
        items.forEach(item -> item.getProduct().getName()); // Force product initialization
        return items;
    }

    public CartItem addToCart(User user, Long productId, int quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return cartRepo.findByUserId(user.getId()).stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    return cartRepo.save(existingItem);
                })
                .orElseGet(() -> cartRepo.save(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(quantity)
                        .build()));
    }

    public CartItem updateCartItem(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            removeCartItem(user, productId);
            return null;
        }

        CartItem item = cartRepo.findByUserId(user.getId()).stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
        item.setQuantity(quantity);
        return cartRepo.save(item);
    }

    public void removeCartItem(User user, Long productId) {
        CartItem item = cartRepo.findByUserId(user.getId()).stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
        cartRepo.delete(item);
    }

    public void clearCart(User user) {
        List<CartItem> items = cartRepo.findByUserId(user.getId());
        cartRepo.deleteAll(items);
    }
}
