<!-- ──────────────────────────────────────────────────────────────
     src/pages/ProductDetail.vue
───────────────────────────────────────────────────────────────── -->
<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">{{ product.name }}</h2>

    <div class="product-detail">
      <img :src="product.image" alt="Product Image" class="product-img" />

      <div class="product-info">
        <p>{{ product.description }}</p>
        <p>
          Price: <span class="highlight">{{ product.price }}$</span>
        </p>

        <div class="add-to-cart-section">
          <input type="number"
                 v-model.number="quantity"
                 :min="1"
                 :max="product.stockQuantity" />

          <!-- logged‑in & stock ok -->
          <button v-if="isAuthenticated && product.stockQuantity > 0"
                  class="btn btn-success"
                  @click="handleAddToCart">
            Add to Cart
          </button>

          <!-- guest & stock ok -->
          <button v-else-if="!isAuthenticated && product.stockQuantity > 0"
                  class="btn btn-success"
                  @click="goLogin">
            Add to Cart
          </button>

          <!-- out of stock -->
          <button v-else
                  class="btn btn-secondary"
                  disabled>
            Out of Stock
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import api from '@/services/api';

export default {
  data() {
    return {
      product : {},
      quantity: 1
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
    async fetchProduct() {
      const productId = this.$route.params.id;
      const { data }  = await api.getProduct(productId);
      this.product    = data;
    },
    async handleAddToCart() {
      if (!this.isAuthenticated) {
        this.goLogin();
        return;
      }
      await this.addToCart({
        productId: this.product.id,
        quantity : this.quantity
      });
    },
    goLogin() {
      this.$router.push({
        name : 'Login',
        query: { redirect: this.$route.fullPath }
      });
    }
  },

  mounted() { this.fetchProduct(); }
};
</script>

<style scoped>
.product-detail   { display:flex; gap:40px; margin-block:30px; }
.product-img      { width:280px; border-radius:12px; object-fit:cover; }
.product-info     { max-width:500px; }
.add-to-cart-section { margin-top:20px; }
.highlight        { color:var(--highlight-color); font-weight:bold; text-shadow:var(--glow-effect); }
</style>
