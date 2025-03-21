<template>
  <div class="container form-container">
    <h2 class="neon-text text-center">🔹 Cyberpunk Login 🔹</h2>
    <form @submit.prevent="handleLogin">
      <input
          v-model="email"
          placeholder="Email"
          required
          class="cyber-input"
      />
      <input
          v-model="password"
          type="password"
          placeholder="Password"
          required
          class="cyber-input"
      />
      <button type="submit" class="btn">Login</button>
    </form>
    <p v-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
  </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  data() {
    return {
      email: '', // renamed from "username" to "email"
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    ...mapActions(['login']),
    async handleLogin() {
      const success = await this.login({
        email: this.email, // send "email" now instead of "username"
        password: this.password
      });
      if (success) {
        this.$router.push('/');
      } else {
        this.errorMessage = 'Invalid login credentials!';
      }
    }
  }
};
</script>

<style scoped>
.form-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 30px;
  background: rgba(20, 20, 20, 0.9);
  border: 2px solid var(--primary-color);
  box-shadow: var(--glow-effect);
  border-radius: 10px;
  text-align: center;
}
</style>
