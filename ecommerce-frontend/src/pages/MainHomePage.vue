<template>
  <div class="container">
    <h2>Products</h2>
    <table class="table">
      <thead>
      <tr>
        <th>Name</th>
        <th>Category</th>
        <th>Price</th>
        <th>Stock</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="product in products" :key="product.id">
        <td>{{ product.name }}</td>
        <td>{{ product.category?.name || 'Uncategorized' }}</td>
        <td>${{ product.price }}</td>
        <td>{{ product.stockQuantity }}</td>
        <td>
          <router-link :to="'/product/' + product.id" class="btn btn-info">Details</router-link>
          <button class="btn btn-primary" @click="promptLogin()" v-if="!isAuthenticated">Add to Cart</button>
          <button class="btn btn-success" @click="addToCart(product.id)" v-if="isAuthenticated">Add to Cart</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import api from '../services/api';
import { mapState } from 'vuex';

export default {
  data() {
    return {
      products: []
    };
  },
  computed: {
    ...mapState(['token']),
    isAuthenticated() {
      return !!this.token;
    }
  },
  methods: {
    async fetchProducts() {
      try {
        const res = await api.getProducts();
        this.products = res.data;
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    },
    async addToCart(productId) {
      try {
        await api.addToCart(productId, 1);
        alert('Added to cart!');
      } catch (error) {
        console.error('Error adding to cart:', error);
        alert('Failed to add product to cart.');
      }
    },
    promptLogin() {
      alert('Please log in to add items to your cart.');
      this.$router.push('/login');
    }
  },
  mounted() {
    this.fetchProducts();
  }
};
</script>
