<template>
  <div class="container page-container">
    <h2 class="neon-text">👨‍💼 Admin Dashboard</h2>

    <!-- PRODUCTS -->
    <section class="section">
      <h3>Manage Products</h3>
      <router-link to="/admin/products/new" class="btn btn-purple">
        ➕ Add Product
      </router-link>
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
          <td>{{ product.category.name }}</td>
          <td>${{ product.price }}</td>
          <td>{{ product.stockQuantity }}</td>
          <td>
            <button class="btn btn-warning" @click="editProduct(product.id)">
              Edit
            </button>
            <button class="btn btn-danger" @click="deleteProduct(product.id)">
              Delete
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </section>

    <!-- CATEGORIES -->
    <section class="section">
      <h3>Manage Categories</h3>
      <router-link to="/admin/categories/new" class="btn btn-purple">
        ➕ Add Category
      </router-link>
      <table class="neon-table">
        <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="category in categories" :key="category.id">
          <td>{{ category.name }}</td>
          <td>{{ category.description }}</td>
          <td>
            <button class="btn btn-warning" @click="editCategory(category.id)">
              Edit
            </button>
            <button class="btn btn-danger" @click="deleteCategory(category.id)">
              Delete
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </section>

    <!-- LOGIN LOGS -->
    <section class="section">
      <h3>User Login Logs</h3>
      <ul class="log-list">
        <li v-for="log in logs" :key="log.id">
          {{ log.user.email }} -
          {{ new Date(log.loginTime).toLocaleString() }} from {{ log.ipAddress }}
        </li>
      </ul>
    </section>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      products: [],
      logs: [],
      categories: []
    };
  },
  methods: {
    async fetchProducts() {
      const res = await api.getProducts();
      this.products = res.data;
    },
    async fetchLogs() {
      const res = await api.getLoginLogs();
      this.logs = res.data;
    },
    async fetchCategories() {
      const res = await api.getCategories();
      this.categories = res.data;
    },
    async deleteProduct(id) {
      await api.deleteProduct(id);
      this.fetchProducts();
    },
    async deleteCategory(id) {
      await api.deleteCategory(id);
      this.fetchCategories();
    },
    editProduct(id) {
      this.$router.push(`/admin/products/${id}/edit`);
    },
    editCategory(id) {
      this.$router.push(`/admin/categories/${id}/edit`);
    }
  },
  mounted() {
    this.fetchProducts();
    this.fetchLogs();
    this.fetchCategories();
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 80px;
}

.section {
  margin-bottom: 40px;
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
