<template>
  <div class="container">
    <h2>Your Cart</h2>
    <table class="table">
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
          <router-link :to="'/product/' + item.product.id">{{ item.product.name }}</router-link>
        </td>
        <td>
          <input type="number" v-model="item.quantity" min="1" :max="item.product.stockQuantity" @change="updateQuantity(item)">
        </td>
        <td>{{ item.product.stockQuantity }}</td>
        <td>${{ item.product.price }}</td>
        <td>${{ (item.product.price * item.quantity).toFixed(2) }}</td>
        <td>
          <button class="btn btn-danger" @click="removeItem(item.product.id)">Remove</button>
        </td>
      </tr>
      </tbody>
    </table>
    <h4>Total: ${{ cartTotal }}</h4>
    <button class="btn btn-primary" @click="checkout">Proceed to Checkout</button>
  </div>
</template>

<script>
import api from '../services/api';
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['cart']),
    cartTotal() {
      return this.cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0).toFixed(2);
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
      // Add PayPal integration here later
    },
    ...mapActions(['fetchCart'])
  },
  mounted() {
    this.fetchCart();
  }
};
</script>
