<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">Categories</h2>
    <ul class="list-group">
      <li v-for="category in categories" :key="category.id" class="list-group-item">
        <router-link :to="'/category/' + category.id">{{ category.name }}</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  data() {
    return {
      categories: []
    };
  },
  async mounted() {
    try {
      const res = await api.getCategories();
      this.categories = res.data;
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 80px;
}
.list-group {
  list-style: none;
  padding: 0;
}
.list-group-item {
  padding: 10px;
  border: 1px solid var(--primary-color);
  margin-bottom: 5px;
}
</style>
