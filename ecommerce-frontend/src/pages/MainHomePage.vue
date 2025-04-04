<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">🛒 Available Products</h2>
    <table class="neon-table">
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
          <button
              class="btn btn-success"
              v-if="isAuthenticated"
              @click="addProductToCart(product.id)"
          >
            Add to Cart
          </button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import api from "@/services/api";

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
    ...mapActions(['addToCart']),actions: {
      async addToCart({ commit, state }, { productId, quantity }) {
        try {
          // Call backend to add the product to the cart
          const addedCartItem = await api.addToCart(productId, quantity);

          // Update cart optimistically
          const existingItemIndex = state.cart.findIndex(
              item => item.product && item.product.id === productId // Add check for item.product existence
          );

          const updatedCart = [...state.cart];
          if (existingItemIndex !== -1) {
            // If the item already exists, update the quantity
            updatedCart[existingItemIndex].quantity += quantity;
          } else {
            // If it's a new item, add it to the cart
            updatedCart.push(addedCartItem);
          }
          commit('setCart', updatedCart);

          // Centralized alert for success
          alert('Product added to cart successfully!');
        } catch (error) {
          console.error('Error adding product to cart:', error);
          alert('Failed to add product to cart. Please try again.');
        }
      }
    },
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
