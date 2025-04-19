<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">🛒 Order History</h2>

    <!-- Admin‑only filter panel -->
    <div v-if="isAdmin" class="dashboard-filters">
      <input
          v-model="filters.email"
          placeholder="Filter by customer email"
          class="filter-input"
      />
      <div class="date-filters">
        <input type="date" v-model="filters.startDate" class="filter-input" />
        to
        <input type="date" v-model="filters.endDate" class="filter-input" />
      </div>
      <button @click="applyFilters" class="filter-button">🔍 Apply</button>
      <button @click="resetFilters" class="filter-button">🔄 Reset</button>
    </div>

    <table class="neon-table">
      <thead>
      <tr>
        <th v-if="isAdmin">Customer</th>
        <th>Date</th>
        <th>Payment Method</th>
        <th>Total</th>
        <th>Details</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="o in filteredOrders" :key="o.id">
        <td v-if="isAdmin">{{ o.userEmail }}</td>
        <td>{{ formatDate(o.orderDate) }}</td>
        <td>{{ o.paymentMethod || 'N/A' }}</td>
        <td>${{ o.total }}</td>
        <td>
          <router-link :to="`/order/${o.id}`" class="btn btn-info">
            View
          </router-link>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="filteredOrders.length === 0" class="no-results">
      No orders found.
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import api from '@/services/api';
import dayjs from 'dayjs';

export default {
  data() {
    return {
      orders: [],
      filteredOrders: [],
      filters: {
        email: '',
        startDate: null,
        endDate: null
      }
    };
  },
  computed: {
    ...mapState(['role']),
    isAdmin() {
      return this.role === 'ADMIN';
    }
  },
  methods: {
    async loadOrders() {
      const res = await api.getOrders();
      this.orders = res.data;
      this.filteredOrders = [...this.orders];
    },
    formatDate(dateString) {
      return dateString
          ? dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
          : '';
    },
    applyFilters() {
      this.filteredOrders = this.orders.filter(o => {
        if (
            this.filters.email &&
            !o.userEmail
                ?.toLowerCase()
                .includes(this.filters.email.toLowerCase())
        ) {
          return false;
        }
        if (
            this.filters.startDate &&
            dayjs(o.orderDate).isBefore(this.filters.startDate)
        ) {
          return false;
        }
        if (
            this.filters.endDate &&
            dayjs(o.orderDate).isAfter(
                dayjs(this.filters.endDate).endOf('day')
            )
        ) {
          return false;
        }
        return true;
      });
    },
    resetFilters() {
      this.filters = { email: '', startDate: null, endDate: null };
      this.filteredOrders = [...this.orders];
    }
  },
  async mounted() {
    await this.loadOrders();
  }
};
</script>

<style scoped>
/* reuse your existing neon-table, dashboard-filters, filter-input, filter-button, no-results, etc. */

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
