<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">Products in {{ categoryName }}</h2>
    <div class="row">
      <div class="col-md-4" v-for="product in products" :key="product.id">
        <div class="card">
          <img :src="product.image || 'https://via.placeholder.com/150'" class="card-img-top" alt="Product Image">
          <div class="card-body">
            <h5 class="card-title">{{ product.name }}</h5>
            <p class="card-text">${{ product.price }}</p>
            <router-link :to="'/product/' + product.id" class="btn btn-primary">Details</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  data() {
    return {
      products: [],
      categoryName: ''
    };
  },
  async mounted() {
    const categoryId = this.$route.params.id;
    try {
      const res = await api.getProductsByCategory(categoryId);
      this.products = res.data;
      if (this.products.length > 0) {
        this.categoryName = this.products[0].category.name;
      } else {
        this.categoryName = 'Category';
      }
    } catch (error) {
      console.error('Error fetching products by category:', error);
    }
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 80px;
}
.card {
  margin-bottom: 20px;
}
</style>
