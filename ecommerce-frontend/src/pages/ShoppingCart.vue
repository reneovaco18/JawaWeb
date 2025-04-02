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
      <p class="text-center">Your cart is empty</p>
    </div>

    <div v-else class="checkout-section">
      <h4 class="text-center">Total: ${{ cartTotal }}</h4>
      <div class="text-center">
        <button class="btn btn-primary" @click="checkout">
          Proceed to Checkout
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['cart']),
    cartTotal() {
      return this.cart.reduce((sum, item) => {
        const price = item.product?.price || 0;
        return sum + (price * item.quantity);
      }, 0).toFixed(2);
    }
  },
  methods: {
    async updateQuantity(item) {
      if (item.quantity > 0) {
        await api.addToCart(item.product?.id, item.quantity);
        this.fetchCart();
      }
    },
    async removeItem(productId) {
      if (productId) {
        await api.removeFromCart(productId);
        this.fetchCart();
      }
    },
    ...mapActions(['fetchCart'])
  },
  mounted() {
    this.fetchCart();
  }
};
</script>
