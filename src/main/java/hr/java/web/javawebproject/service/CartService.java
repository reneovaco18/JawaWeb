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
        return cartRepo.findByUserId(user.getId());
    }

    public CartItem addToCart(User user, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be >= 1");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product with ID=" + productId));

        // optional: check product stock

        // check if cart item exists
        List<CartItem> existingItems = cartRepo.findByUserId(user.getId());
        CartItem item = existingItems.stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            // create new
            item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        return cartRepo.save(item);
    }

    public CartItem updateCartItem(User user, Long productId, int quantity) {
        // if quantity <= 0, we remove
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
