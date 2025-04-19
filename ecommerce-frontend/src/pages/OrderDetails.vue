<template>
  <div class="container page-container">
    <h2 class="neon-text order-title">Order #{{ orderId }}</h2>
    <div class="order-info">
      <p class="info-line"><strong>Date:</strong> {{ formatDate(order.orderDate) }}</p>
      <p class="info-line"><strong>Payment Method:</strong> {{ order.paymentMethod }}</p>
      <p class="info-line"><strong>Total:</strong> ${{ order.total }}</p>
    </div>

    <h4 class="section-heading">Items</h4>
    <ul class="items-list">
      <li v-for="item in order.items" :key="item.id" class="item">
        <router-link :to="`/product/${item.product.id}`" class="item-name">
          {{ item.product.name }}
        </router-link>
        <span class="item-qty">Qty: {{ item.quantity }}</span>
        <span class="item-price">${{ item.price.toFixed(2) }}</span>
      </li>
    </ul>

    <div v-if="order.items?.length === 0" class="no-results">
      No items in this order.
    </div>
  </div>
</template>

<script>
import api from '@/services/api';
import dayjs from 'dayjs';

export default {
  data() {
    return {
      orderId: null,
      order: { items: [] }
    };
  },
  methods: {
    formatDate(dateString) {
      return dateString ? dayjs(dateString).format('YYYY-MM-DD HH:mm:ss') : '';
    },
    async fetchOrder() {
      try {
        const res = await api.getOrder(this.orderId);
        this.order = res.data;
      } catch (err) {
        console.error('Failed to fetch order', err);
      }
    }
  },
  mounted() {
    this.orderId = this.$route.params.orderId;
    this.fetchOrder();
  }
};
</script>

<style scoped>
/* Container styling */
.page-container {
  padding-top: 100px;
  max-width: 800px;
  margin: 0 auto;
  background-color: #121212;
  color: #e0e0e0;
  font-family: 'Roboto', sans-serif;
}

/* Neon heading */
.order-title {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.7);
  margin-bottom: 20px;
  font-size: 2rem;
}

/* Order info card */
.order-info {
  background-color: #1e1e1e;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  margin-bottom: 30px;
}
.info-line {
  margin: 8px 0;
  font-size: 1rem;
}

/* Section heading */
.section-heading {
  color: #9c27b0;
  margin-bottom: 15px;
  font-size: 1.5rem;
}

/* Items list styling */
.items-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #1e1e1e;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 12px;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.1);
  transition: background-color 0.3s;
}
.item:hover {
  background-color: #2a2a2a;
}
.item-name {
  flex: 2;
  font-size: 1rem;
  text-decoration: none;
  color: #00ffff;
  transition: text-shadow 0.3s;
}
.item-name:hover {
  text-shadow: 0 0 5px rgba(0, 255, 255, 0.9);
}
.item-qty, .item-price {
  flex: 1;
  text-align: right;
  font-size: 1rem;
}

/* No results fallback */
.no-results {
  text-align: center;
  padding: 20px;
  color: #90a4ae;
  font-style: italic;
  background-color: #1e1e1e;
  border-radius: 8px;
  margin-top: 10px;
}
</style>
