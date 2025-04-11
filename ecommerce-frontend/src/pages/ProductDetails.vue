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

      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';
import { mapActions } from 'vuex';
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
    ...mapActions(['addToCart']),



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
