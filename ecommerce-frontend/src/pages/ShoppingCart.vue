<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">Your Cart</h2>

    <table v-if="cart.length > 0" class="neon-table">
      <thead>
      <tr>
        <th>Product</th>
        <th>Quantity</th>
        <th>Price</th>
        <th>Total</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in cart" :key="item.id">
        <td>
          <router-link v-if="item.product?.id" :to="'/product/' + item.product.id">
            {{ item.product?.name || 'Unnamed Product' }}
          </router-link>
        </td>
        <td>
          <input
              type="number"
              v-model.number="item.quantity"
              min="1"
              max="item.product?.stockQuantity || 0"
              @change="updateQuantity(item)"
              class="quantity-input"
          />
        </td>
        <td>${{ item.product?.price?.toFixed(2) || '0.00' }}</td>
        <td>${{ (item.product?.price * item.quantity)?.toFixed(2) || '0.00' }}</td>
        <td>
          <button class="btn btn-danger" @click="removeItem(item.product?.id)">
            Remove
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="cart.length === 0" class="empty-cart">
      <p class="text-center">Your cart is empty!</p>
    </div>

    <div v-else class="checkout-section">
      <h4 class="text-center">Total: ${{ cartTotal }}</h4>
      <div class="text-center">
        <button class="btn btn-primary" @click="checkout">Proceed to Checkout</button>
      </div>
    </div>
  </div>
</template>

<script>
import { debounce } from "lodash"; // Import lodash for debouncing
import { mapState, mapActions } from "vuex";

export default {
  computed: {
    ...mapState(["cart"]),
    cartTotal() {
      // Dynamically calculate the total cart value
      return this.cart
          .reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0)
          .toFixed(2);
    },
  },
  methods: {
    ...mapActions(["fetchCart", "addToCart", "removeFromCart"]), // Map Vuex actions

    // Update quantity with debounce to prevent fast, repeated triggers
    updateQuantity: debounce(async function (item) {
      if (item.quantity <= 0) {
        item.quantity = 1; // Reset invalid quantity
        alert("Quantity cannot be less than 1.");
        return;
      }

      try {
        await this.addToCart({ productId: item.product.id, quantity: item.quantity });
      } catch (error) {
        console.error("Error updating quantity:", error);
        alert("Failed to update item quantity. Try again.");
      }
    }, 300),

    async removeItem(productId) {
      try {
        if (productId) {
          await this.removeFromCart({ productId }); // Call Vuex action to remove item
          alert("Item removed successfully.");
        }
      } catch (error) {
        console.error("Error removing item:", error);
        alert("Failed to remove product from cart.");
      }
    },

    async checkout() {
      alert("Proceeding to checkout...");
      // Add your checkout logic here
    },
  },
  async mounted() {
    try {
      await this.fetchCart(); // Fetch cart items when component mounts
    } catch (error) {
      console.error("Failed to load cart:", error);
    }
  },
};
</script>

<style scoped>
/* Scoped styles for ShoppingCart.vue */
.container {
  padding: 20px;
}

.neon-text {
  color: #00ffcc;
}

.neon-table th,
.neon-table td {
  text-align: center;
}

.empty-cart p {
  font-size: larger;
}

.checkout-section h4,
.checkout-section button {
  margin-top: 20px;
}
</style>
