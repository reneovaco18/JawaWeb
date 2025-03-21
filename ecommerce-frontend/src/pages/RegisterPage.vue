<template>
  <div class="container form-container">
    <h2 class="neon-text text-center">📝 Create Your Cyber Account</h2>
    <form @submit.prevent="register">
      <div class="form-group">
        <label>Email:</label>
        <input type="email" v-model="email" required class="cyber-input" />
      </div>
      <div class="form-group">
        <label>Name:</label>
        <input type="text" v-model="name" required class="cyber-input" />
      </div>
      <div class="form-group">
        <label>Password:</label>
        <input type="password" v-model="password" required class="cyber-input" />
      </div>
      <button type="submit" class="btn btn-purple">🚀 Register</button>
    </form>
    <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      email: '',
      name: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    async register() {
      try {
        await api.register({
          email: this.email,
          name: this.name,
          password: this.password
        });
        alert('✅ Registration successful! Please login.');
        this.$router.push('/login');
      } catch (err) {
        this.errorMessage = '❌ Registration failed. Try again.';
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
.form-group {
  margin-bottom: 15px;
}
</style>
