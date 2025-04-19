<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">Your Cart</h2>

    <table v-if="cart.length" class="neon-table">
      <thead>
      <tr>
        <th>Product</th>
        <th>Quantity</th>
        <th>Price</th>
        <th>Total</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in cart" :key="item.product.id">
        <td>
          <router-link :to="`/product/${item.product.id}`">
            {{ item.product.name }}
          </router-link>
        </td>
        <td>
          <input
              type="number"
              v-model.number="item.quantity"
              :min="1"
              :max="item.product.stockQuantity"
              @change="updateQuantity(item)"
          />
        </td>
        <td>${{ item.product.price.toFixed(2) }}</td>
        <td>${{ (item.product.price * item.quantity).toFixed(2) }}</td>
        <td>
          <button class="btn btn-danger" @click="removeItem(item.product.id)">
            Remove
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-else class="empty-cart">
      <p class="text-center">Your cart is empty!</p>
    </div>

    <div v-if="cart.length" class="checkout-section">
      <h4 class="text-center">Total: ${{ cartTotal }}</h4>
      <div class="text-center">
        <button class="btn btn-primary" @click="checkout">
          Proceed to Checkout
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['cart', 'token']),
    isAuthenticated() {
      return !!this.token;
    },
    cartTotal() {
      return this.cart
          .reduce((sum, i) => sum + i.product.price * i.quantity, 0)
          .toFixed(2);
    }
  },
  methods: {
    ...mapActions([
      'fetchCart',
      'updateCartItem',
      'removeFromCart',
      'placeOrder'
    ]),

    updateQuantity: debounce(async function (item) {
      if (!this.isAuthenticated) {
        this.$router.push('/login');
        return;
      }
      if (item.quantity < 1) item.quantity = 1;
      if (item.quantity > item.product.stockQuantity) {
        item.quantity = item.product.stockQuantity;
      }
      try {
        await this.updateCartItem({
          productId: item.product.id,
          quantity: item.quantity
        });
      } catch {
        alert('Failed to update quantity.');
      }
    }, 300),

    removeItem(productId) {
      if (!this.isAuthenticated) {
        this.$router.push('/login');
        return;
      }
      this.removeFromCart({productId});
    },

    async checkout() {
      if (!this.isAuthenticated) {
        this.$router.push('/login');
        return;
      }
      const method = window.prompt('Enter payment method (COD or PAYPAL)', 'COD');
      if (!method) return;

      try {
        const res = await this.placeOrder(method);

        if (method.toUpperCase() === 'PAYPAL' && res.status === 303) {
          const redirectUrl = res.headers.location || res.headers.Location;
          if (redirectUrl) {
            window.location.href = redirectUrl;
          } else {
            alert('Redirect URL missing from server.');
          }
        } else {
          this.$router.push('/orders');
        }
      } catch (err) {
        console.error(err);
        alert('Checkout failed, please try again.');
      }
    }
  },

  async mounted() {
    if (!this.isAuthenticated) {
      this.$router.push('/login');
      return;
    }
    await this.fetchCart();
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}

.neon-text {
  color: #00ffcc;
}

.neon-table th, .neon-table td {
  text-align: center;
}

.empty-cart p {
  font-size: larger;
}

.checkout-section h4, .checkout-section button {
  margin-top: 20px;
}
</style>
