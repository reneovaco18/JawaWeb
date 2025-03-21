<template>
  <!-- Only show profile if user is not null -->
  <div class="container page-container" v-if="user">
    <h2 class="neon-text text-center">👤 Your Cyber Profile</h2>
    <div class="profile-info">
      <p>
        <strong>Email:</strong>
        <span class="highlight">{{ user.email }}</span>
      </p>
      <p>
        <strong>Name:</strong>
        <span class="highlight">{{ user.name }}</span>
      </p>
      <button class="btn btn-danger" @click="handleLogout">
        🔴 Logout
      </button>
    </div>
  </div>

  <!-- Fallback for when user is null -->
  <div v-else>
    <p>You are not logged in.</p>
    <router-link to="/login">Go to Login</router-link>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['user']) // user might be null if not logged in
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
.page-container {
  padding-top: 80px;
  text-align: center;
}

.profile-info {
  max-width: 400px;
  margin: 20px auto;
  padding: 20px;
  border: 2px solid var(--primary-color);
  border-radius: 10px;
  box-shadow: var(--glow-effect);
}
</style>
