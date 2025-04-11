<!-- src/pages/OrderDetails.vue -->
<template>
  <div class="container page-container">
    <h2>Order #{{ orderId }}</h2>
    <p>Date: {{ formatDate(order.orderDate) }}</p>
    <p>Payment Method: {{ order.paymentMethod }}</p>
    <p>Total: ${{ order.total }}</p>

    <h4>Items</h4>
    <ul>
      <li v-for="item in order.items" :key="item.id">
        Product: {{ item.product.name }},
        Qty: {{ item.quantity }},
        Price: ${{ item.price }}
      </li>
    </ul>
  </div>
</template>

<script>
import api from '@/services/api';
import dayjs from 'dayjs';

export default {
  data() {
    return {
      orderId: null,
      order: {}
    };
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return '';
      return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss');
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
/* Additional styling if needed */
</style>
