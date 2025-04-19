<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">🛒 Available Products</h2>

    <table class="neon-table">
      <thead>
      <tr>
        <th>Picture</th>
        <th>Name</th>
        <th>Category</th>
        <th>Price</th>
        <th>Stock</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="product in products" :key="product.id">
        <td>
          <img
              v-if="product.image"
              :src="product.image"
              alt="Product Image"
              style="width: 70px; height: auto;"
          />
          <div v-else>-- no image --</div>
        </td>
        <td>{{ product.name }}</td>
        <td>{{ product.category?.name || 'Uncategorized' }}</td>
        <td>${{ product.price }}</td>
        <td>{{ product.stockQuantity }}</td>
        <td>
          <router-link :to="'/product/' + product.id" class="btn btn-info">
            Details
          </router-link>
          <!-- Authenticated & in‑stock -->
          <button
              v-if="isAuthenticated && product.stockQuantity > 0"
              class="btn btn-success"
              @click="addProductToCart(product.id)"
          >
            Add to Cart
          </button>
          <!-- Guest & in‑stock -->
          <button
              v-else-if="!isAuthenticated && product.stockQuantity > 0"
              class="btn btn-success"
              @click="goLogin"
          >
            Add to Cart
          </button>
          <!-- Out of stock -->
          <button
              v-else
              class="btn btn-secondary"
              disabled
          >
            Out of Stock
          </button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import api from '@/services/api';

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
    ...mapActions(['addToCart']),
    async fetchProducts() {
      try {
        const res = await api.getProducts();
        this.products = res.data;
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    },
    async addProductToCart(productId) {
      try {
        await this.addToCart({ productId, quantity: 1 });
      } catch (error) {
        console.error('Error adding item to cart:', error);
      }
    },
    goLogin() {
      this.$router.push('/login');
    }
  },
  mounted() {
    this.fetchProducts();
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
</style>
