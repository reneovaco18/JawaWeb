package hr.java.web.javawebproject.service;

import hr.java.web.javawebproject.exception.BadRequestException;
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
        // Force product initialization to avoid lazy loading issues.
        items.forEach(item -> {
            if (item.getProduct() != null) {
                item.getProduct().getName();
            }
        });
        return items;
    }

    // Add to cart: If the item exists, increase quantity (provided the new total does not exceed stock).
    public CartItem addToCart(User user, Long productId, int quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        CartItem existingItem = cartRepo.findByUserId(user.getId())
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock");
            }
            existingItem.setQuantity(newQuantity);
            return cartRepo.save(existingItem);
        } else {
            if (quantity > product.getStockQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock");
            }
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
            return cartRepo.save(newItem);
        }
    }

    // Update the quantity of an existing cart item (validate against stock)
    public CartItem updateCartItem(User user, Long productId, int quantity) {
        CartItem item = cartRepo.findByUserId(user.getId()).stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        Product product = item.getProduct();
        if (quantity > product.getStockQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }

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
