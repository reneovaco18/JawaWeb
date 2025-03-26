<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">{{ product.name }}</h2>
    <div class="product-detail">
      <img :src="product.image" alt="Product Image" class="product-img" />
      <div class="product-info">
        <p>{{ product.description }}</p>
        <p>
          Price:
          <span class="highlight">{{ product.price }}$</span>
        </p>
        <button class="btn" @click="addToCart">Add to Cart</button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  data() {
    return {
      product: {}
    };
  },
  async mounted() {
    const productId = this.$route.params.id;
    const response = await api.getProduct(productId);
    this.product = response.data;
  },
  methods: {
    async addToCart() {
      if (!this.$store.state.token) {
        alert('🔒 Please log in to add products to cart.');
        this.$router.push('/login');
        return;
      }

      try {
        const quantity = 1;
        await api.addToCart(this.product.id, quantity);
        await this.$store.dispatch('fetchCart');

        alert('✅ Product added to cart!');
        this.$router.push('/cart');
      } catch (error) {
        console.error('❌ Failed to add product to cart:', error);
        alert('Failed to add product to cart.');
      }
    }
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 80px;
  text-align: center;
}

.product-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.product-img {
  width: 100%;
  max-width: 500px;
  border-radius: 10px;
  box-shadow: var(--glow-effect);
  margin-bottom: 20px;
}

.product-info {
  max-width: 500px;
}

.highlight {
  color: var(--highlight-color);
  font-weight: bold;
  text-shadow: var(--glow-effect);
}
</style>
