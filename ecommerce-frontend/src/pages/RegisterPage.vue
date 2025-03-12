<template>
  <div class="container">
    <h2>Register</h2>
    <form @submit.prevent="register">
      <div class="form-group">
        <label>Email:</label>
        <input type="email" v-model="email" required class="form-control">
      </div>
      <div class="form-group">
        <label>Name:</label>
        <input type="text" v-model="name" required class="form-control">
      </div>
      <div class="form-group">
        <label>Password:</label>
        <input type="password" v-model="password" required class="form-control">
      </div>
      <button class="btn btn-success">Register</button>
    </form>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  data() {
    return {
      email: '',
      name: '',
      password: ''
    };
  },
  methods: {
    async register() {
      try {
        await api.register({ email: this.email, name: this.name, password: this.password });
        alert('Registration successful! Please login.');
        this.$router.push('/login');
      } catch (err) {
        alert('Registration failed.');
      }
    }
  }
};
</script>
