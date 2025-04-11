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
              :min="1"
              :max="item.product?.stockQuantity || 0"
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
import { debounce } from "lodash";
import { mapState, mapActions } from "vuex";

export default {
  computed: {
    ...mapState(["cart"]),
    cartTotal() {
      return this.cart
          .reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0)
          .toFixed(2);
    }
  },
  methods: {
    ...mapActions(["fetchCart", "updateCartItem", "removeFromCart", "placeOrder"]),
    updateQuantity: debounce(async function (item) {
      if (item.quantity <= 0) {
        item.quantity = 1;
        alert("Quantity cannot be less than 1.");
        return;
      }
      if (item.product.stockQuantity && item.quantity > item.product.stockQuantity) {
        item.quantity = item.product.stockQuantity;
        alert("Quantity cannot exceed available stock.");
        return;
      }
      try {
        await this.updateCartItem({ productId: item.product.id, quantity: item.quantity });
      } catch (error) {
        console.error("Error updating quantity:", error);
        alert("Failed to update item quantity. Try again.");
      }
    }, 300),
    removeItem(productId) {
      this.removeFromCart({ productId });
    },
    async checkout() {
      // Prompt the user for a payment method (COD or PAYPAL)
      let paymentMethod = window.prompt("Enter payment method (COD or PAYPAL)", "COD");
      if (!paymentMethod) {
        alert("Payment method is required.");
        return;
      }
      try {
        await this.placeOrder({ paymentMethod });
        // Optionally, redirect to the orders page after successful checkout.
        this.$router.push("/orders");
      } catch (error) {
        console.error("Checkout error:", error);
      }
    }
  },
  async mounted() {
    try {
      await this.fetchCart();
    } catch (error) {
      console.error("Failed to load cart:", error);
    }
  }
};
</script>

<style scoped>
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
