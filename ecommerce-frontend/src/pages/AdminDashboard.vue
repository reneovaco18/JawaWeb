<template>
  <div class="container page-container">
    <h2 class="neon-text">👨‍💼 Admin Dashboard</h2>

    <!-- Products Section -->
    <section class="section">
      <h3>Manage Products</h3>
      <router-link to="/admin/products/new" class="btn btn-purple">
        ➕ Add Product
      </router-link>
      <table class="neon-table">
        <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th>Price</th>
          <th>Stock</th>
          <th>Category</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.name }}</td>
          <td>{{ product.description }}</td>
          <td>{{ product.price }}</td>
          <td>{{ product.stockQuantity }}</td>
          <td>{{ product.category.name }}</td>
          <td>
            <button @click="editProduct(product.id)" class="btn btn-blue">✏️ Edit</button>
            <button @click="deleteProduct(product.id)" class="btn btn-red">🗑️ Delete</button>
          </td>
        </tr>
        </tbody>
      </table>
      <div v-if="products.length === 0" class="no-results">
        No products available
      </div>
    </section>

    <!-- Categories Section -->
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
            <button @click="editCategory(category.id)" class="btn btn-blue">✏️ Edit</button>
            <button @click="deleteCategory(category.id)" class="btn btn-red">🗑️ Delete</button>
          </td>
        </tr>
        </tbody>
      </table>
      <div v-if="categories.length === 0" class="no-results">
        No categories available
      </div>
    </section>

    <!-- Enhanced Login Logs Section -->
    <section class="section">
      <h3>User Login Logs</h3>

      <!-- Filters -->
      <div class="dashboard-filters">
        <!-- Email Filter -->
        <input v-model="filters.email" placeholder="Search by email" class="filter-input" />

        <!-- Date Filters -->
        <div class="date-filters">
          <input type="date" v-model="filters.startDate" class="filter-input" />
          to
          <input type="date" v-model="filters.endDate" class="filter-input" />
        </div>

        <!-- Status Filter -->
        <select v-model="filters.status" class="filter-select">
          <option value="">All Statuses</option>
          <option value="true">Successful</option>
          <option value="false">Failed</option>
        </select>

        <!-- Apply Filters Button -->
        <button @click="applyFilters" class="filter-button">🔍 Apply Filters</button>
      </div>

      <!-- Logs Table -->
      <div class="logs-container">
        <table class="neon-table">
          <!-- Table Header -->
          <thead><tr><th>Email</th><th>IP Address</th><th>Login Time</th><th>Status</th><th>Device Info</th></tr></thead>

          <!-- Table Body -->
          <tbody><tr v-for='log in filteredLogs' :key='log.id'><td>{{ log.user?.email || 'Unknown' }}</td><td>{{ log.ipAddress }}</td><td>{{ formatDateTime(log.loginTime) }}</td><td :class="{ 'success-status': log.success, 'fail-status': !log.success }">{{ log.success ? '✅ Success' : '❌ Failed' }}</td><td>{{ getDeviceInfo(log.userAgent) }}</td></tr></tbody></table>

        <div v-if='filteredLogs.length===0' class='no-results'>No matching login records found.</div>
      </div>
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
      categories: [],
      filters: {
        email: '', // Initialize email filter
        startDate: null,
        endDate: null,
        status: ''
      },
      filteredLogs: [] // Initialize filteredLogs as an empty array
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

