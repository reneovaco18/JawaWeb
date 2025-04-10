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
      this.filteredLogs = [...this.logs]; // Initialize filtered logs with all logs
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

      // Simple parsing - you can make this more sophisticated
      if (userAgent.includes('Chrome')) return 'Chrome Browser';
      if (userAgent.includes('Firefox')) return 'Firefox Browser';
      if (userAgent.includes('Safari')) return 'Safari Browser';
      if (userAgent.includes('Edge')) return 'Edge Browser';
      if (userAgent.includes('MSIE') || userAgent.includes('Trident')) return 'Internet Explorer';
      return userAgent.substring(0, 50) + '...';
    },

    applyFilters() {
      // Filter the logs based on the filter criteria
      this.filteredLogs = this.logs.filter(log => {
        // Email filter
        if (this.filters.email && (!log.user || !log.user.email ||
            !log.user.email.toLowerCase().includes(this.filters.email.toLowerCase()))) {
          return false;
        }

        // Date filters
        if (this.filters.startDate) {
          const logDate = new Date(log.loginTime);
          const startDate = new Date(this.filters.startDate);
          if (logDate < startDate) return false;
        }

        if (this.filters.endDate) {
          const logDate = new Date(log.loginTime);
          const endDate = new Date(this.filters.endDate);
          endDate.setHours(23, 59, 59); // End of day
          if (logDate > endDate) return false;
        }

        // Status filter
        if (this.filters.status !== '') {
          const successStatus = this.filters.status === 'true';
          if (log.success !== successStatus) return false;
        }

        return true;
      });
    }

  },

  mounted() {
    this.fetchProducts();
    this.fetchLogs();
    this.fetchCategories();
  }
};
</script>

<style>

  /* General Layout */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #121212;
  color: #e0e0e0;
  font-family: 'Roboto', sans-serif;
}

.neon-text {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.7);
  margin-bottom: 30px;
}

/* Sections */
.section {
  background-color: #1e1e1e;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}

.section h3 {
  color: #9c27b0;
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 1.5rem;
}

/* Buttons */
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-right: 8px;
  margin-bottom: 16px;
  display: inline-flex;
  align-items: center;
}

.btn-purple {
  background-color: #9c27b0;
  color: white;
}

.btn-purple:hover {
  background-color: #7b1fa2;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.5);
}

.btn-blue {
  background-color: #2196f3;
  color: white;
}

.btn-blue:hover {
  background-color: #1976d2;
  box-shadow: 0 0 10px rgba(33, 150, 243, 0.5);
}

.btn-red {
  background-color: #f44336;
  color: white;
}

.btn-red:hover {
  background-color: #d32f2f;
  box-shadow: 0 0 10px rgba(244, 67, 54, 0.5);
}

/* Tables */
.neon-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.2);
}

.neon-table th {
  background-color: #2d2d2d;
  color: #00ffff;
  text-align: left;
  padding: 12px 15px;
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
}

.neon-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #333;
}

.neon-table tbody tr {
  background-color: #1e1e1e;
  transition: background-color 0.3s;
}

.neon-table tbody tr:hover {
  background-color: #2a2a2a;
}

.neon-table tbody tr:last-child td {
  border-bottom: none;
}

/* Filter Section */
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

.filter-input, .filter-select {
  padding: 8px 12px;
  border: 1px solid #444;
  border-radius: 4px;
  background-color: #1e1e1e;
  color: #e0e0e0;
  min-width: 200px;
}

.date-filters {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-button {
  background-color: #00bcd4;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.filter-button:hover {
  background-color: #00acc1;
  box-shadow: 0 0 10px rgba(0, 188, 212, 0.5);
}

/* Status Indicators */
.success-status {
  color: #4caf50;
  font-weight: 500;
}

.fail-status {
  color: #f44336;
  font-weight: 500;
}

/* No Results Message */
.no-results {
  text-align: center;
  padding: 20px;
  color: #90a4ae;
  font-style: italic;
  background-color: #1e1e1e;
  border-radius: 8px;
  margin-top: 10px;
}

/* Responsive Adjustments */
@media (max-width: 992px) {
  .dashboard-filters {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-input, .filter-select {
    width: 100%;
    min-width: auto;
  }}
</style>