<template>
  <div class="container">
    <h2>{{ product.name }}</h2>
    <p>{{ product.description }}</p>
    <p><strong>Category:</strong> {{ product.category?.name }}</p>
    <p><strong>Price:</strong> ${{ product.price }}</p>
    <p><strong>Stock:</strong> {{ product.stockQuantity }}</p>
    <button v-if="isAuthenticated" class="btn btn-success" @click="addToCart">Add to Cart</button>
    <button v-else class="btn btn-primary" @click="promptLogin">Login to Add</button>
  </div>
</template>

<script>
import api from '../services/api';
import { mapState } from 'vuex';

export default {
  data() {
    return {
      product: {}
    };
  },
  computed: {
    ...mapState(['token']),
    isAuthenticated() {
      return !!this.token;
    }
  },
  methods: {
    async fetchProduct() {
      const res = await api.getProduct(this.$route.params.id);
      this.product = res.data;
    },
    async addToCart() {
      await api.addToCart(this.product.id, 1);
      alert('Added to cart!');
    },
    promptLogin() {
      alert('Please log in to add items to your cart.');
      this.$router.push('/login');
    }
  },
  mounted() {
    this.fetchProduct();
  }
};
</script>
