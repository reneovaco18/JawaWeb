<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">Your Cart</h2>
    <table class="neon-table">
      <thead>
      <tr>
        <th>Product</th>
        <th>Quantity</th>
        <th>Available Stock</th>
        <th>Price</th>
        <th>Total</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in cart" :key="item.id">
        <td>
          <router-link :to="'/product/' + item.product.id">
            {{ item.product.name }}
          </router-link>
        </td>
        <td>
          <input
              type="number"
              v-model.number="item.quantity"
              min="1"
              :max="item.product.stockQuantity"
              @change="updateQuantity(item)"
              class="quantity-input"
          />
        </td>
        <td>{{ item.product.stockQuantity }}</td>
        <td>${{ item.product.price }}</td>
        <td>${{ (item.product.price * item.quantity).toFixed(2) }}</td>
        <td>
          <button class="btn btn-danger" @click="removeItem(item.product.id)">
            Remove
          </button>
        </td>
      </tr>
      </tbody>
    </table>
    <h4 class="text-center">Total: ${{ cartTotal }}</h4>
    <div class="text-center">
      <button class="btn btn-primary" @click="checkout">
        Proceed to Checkout
      </button>
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
      return this.cart
          .reduce((sum, item) => sum + item.product.price * item.quantity, 0)
          .toFixed(2);
    }
  },
  methods: {
    async updateQuantity(item) {
      await api.addToCart(item.product.id, item.quantity);
      this.fetchCart();
    },
    async removeItem(productId) {
      await api.removeFromCart(productId);
      this.fetchCart();
    },
    async checkout() {
      alert('Redirecting to PayPal checkout (Placeholder)');
    },
    ...mapActions(['fetchCart'])
  },
  mounted() {
    this.fetchCart();
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 80px;
}
.neon-table {
  width: 100%;
  margin: 20px auto;
  border-collapse: collapse;
}
.neon-table th,
.neon-table td {
  padding: 12px;
  text-align: center;
  border: 1px solid var(--primary-color);
}
.quantity-input {
  width: 60px;
  text-align: center;
}
</style>
