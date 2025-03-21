<template>
  <nav class="navbar">
    <div class="logo">
      <router-link to="/">🟣 CYBER STORE</router-link>
    </div>
    <div class="nav-links">
      <!-- Show these if not logged in -->
      <router-link v-if="!isAuthenticated" to="/login">Login</router-link>
      <router-link v-if="!isAuthenticated" to="/register">Register</router-link>

      <!-- For any authenticated user -->
      <router-link v-if="isAuthenticated" to="/categories">Categories</router-link>
      <router-link v-if="isAuthenticated" to="/orders">Orders</router-link>
      <router-link v-if="isAuthenticated" to="/profile">Profile</router-link>

      <!-- Admin-only link -->
      <router-link v-if="isAdmin" to="/admin">Admin Dashboard</router-link>

      <router-link v-if="isAuthenticated" to="/cart">Cart</router-link>

      <button v-if="isAuthenticated" class="btn logout-btn" @click="handleLogout">
        Logout
      </button>
    </div>
  </nav>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['user', 'role']),
    isAuthenticated() {
      return this.user !== null;
    },
    isAdmin() {
      return this.role === 'ADMIN';
    }
  },
  methods: {
    ...mapActions(['logout']),
    handleLogout() {
      this.logout();
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
/* Additional styling if needed */
</style>
