<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">🛒 Order History</h2>
    <table class="neon-table">
      <thead>
      <tr>
        <th>Date</th>
        <th>Payment Method</th>
        <th>Total</th>
        <th>Details</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="order in orders" :key="order.id">
        <td>{{ new Date(order.orderDate).toLocaleString() }}</td>
        <td>{{ order.paymentMethod }}</td>
        <td>${{ order.total }}</td>
        <td>
          <router-link :to="'/order/' + order.id" class="btn btn-info">
            View
          </router-link>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      orders: []
    };
  },
  async mounted() {
    const res = await api.getOrders();
    this.orders = res.data;
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 80px;
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
