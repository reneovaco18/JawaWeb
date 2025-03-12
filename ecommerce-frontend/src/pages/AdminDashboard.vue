<template>
  <div class="container">
    <h2>Admin Dashboard</h2>

    <h3>Products</h3>
    <router-link to="/admin/products/new" class="btn btn-success">Add Product</router-link>
    <table class="table">
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
          <button class="btn btn-warning">Edit</button>
          <button class="btn btn-danger" @click="deleteProduct(product.id)">Delete</button>
        </td>
      </tr>
      </tbody>
    </table>

    <h3>User Login Logs</h3>
    <ul>
      <li v-for="log in logs" :key="log.id">
        {{ log.user.email }} - {{ new Date(log.loginTime).toLocaleString() }} from {{ log.ipAddress }}
      </li>
    </ul>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      products: [],
      logs: []
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
    async deleteProduct(id) {
      await api.deleteProduct(id);
      this.fetchProducts();
    }
  },
  mounted() {
    this.fetchProducts();
    this.fetchLogs();
  }
};
</script>
