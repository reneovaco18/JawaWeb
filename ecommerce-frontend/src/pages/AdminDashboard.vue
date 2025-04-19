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

        <!-- Apply & Reset Filters Buttons -->
        <button @click="applyFilters" class="filter-button">🔍 Apply Filters</button>
        <button @click="resetFilters" class="filter-button">🔄 Reset Filters</button>
      </div>

      <!-- Logs Table -->
      <div class="logs-container">
        <table class="neon-table">
          <thead>
          <tr>
            <th>Email</th>
            <th>IP Address</th>
            <th>Login Time</th>
            <th>Status</th>
            <th>Device Info</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="log in filteredLogs" :key="log.id">
            <td>{{ log.user?.email || 'Unknown' }}</td>
            <td>{{ log.ipAddress }}</td>
            <td>{{ formatDateTime(log.loginTime) }}</td>
            <td :class="{ 'success-status': log.success, 'fail-status': !log.success }">
              {{ log.success ? '✅ Success' : '❌ Failed' }}
            </td>
            <td>{{ getDeviceInfo(log.userAgent) }}</td>
          </tr>
          </tbody>
        </table>

        <div v-if="filteredLogs.length === 0" class="no-results">
          No matching login records found.
        </div>
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
        email: '',
        startDate: null,
        endDate: null,
        status: ''
      },
      filteredLogs: []
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
      this.filteredLogs = [...this.logs];
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
    },
    formatDateTime(dateTime) {
      if (!dateTime) return 'N/A';
      const date = new Date(dateTime);
      return date.toLocaleString();
    },
    getDeviceInfo(userAgent) {
      if (!userAgent) return 'Unknown';
      if (userAgent.includes('Chrome')) return 'Chrome Browser';
      if (userAgent.includes('Firefox')) return 'Firefox Browser';
      if (userAgent.includes('Safari')) return 'Safari Browser';
      if (userAgent.includes('Edge')) return 'Edge Browser';
      if (userAgent.includes('MSIE') || userAgent.includes('Trident')) return 'Internet Explorer';
      return userAgent.substring(0, 50) + '...';
    },
    applyFilters() {
      this.filteredLogs = this.logs.filter(log => {
        if (this.filters.email && (!log.user || !log.user.email || !log.user.email.toLowerCase().includes(this.filters.email.toLowerCase()))) {
          return false;
        }
        if (this.filters.startDate) {
          const logDate = new Date(log.loginTime);
          const start = new Date(this.filters.startDate);
          if (logDate < start) return false;
        }
        if (this.filters.endDate) {
          const logDate = new Date(log.loginTime);
          const end = new Date(this.filters.endDate);
          end.setHours(23,59,59);
          if (logDate > end) return false;
        }
        if (this.filters.status !== '') {
          const success = this.filters.status === 'true';
          if (log.success !== success) return false;
        }
        return true;
      });
    },
    resetFilters() {
      this.filters.email = '';
      this.filters.startDate = null;
      this.filters.endDate = null;
      this.filters.status = '';
      this.filteredLogs = [...this.logs];
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
/* ...existing styles unchanged... */
.dashboard-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #2d2d2d;
  border-radius: 8px;
  align-items: center;
}
</style>
