<template>
  <div class="container">
    <h2>Login</h2>
    <form @submit.prevent="login">
      <div class="form-group">
        <label>Email:</label>
        <input type="email" v-model="email" required class="form-control">
      </div>
      <div class="form-group">
        <label>Password:</label>
        <input type="password" v-model="password" required class="form-control">
      </div>
      <button class="btn btn-primary">Login</button>
    </form>
  </div>
</template>

<script>
import api from '../services/api';
import { mapActions } from 'vuex';

export default {
  data() {
    return {
      email: '',
      password: ''
    };
  },
  methods: {
    async login() {
      try {
        const res = await api.login({ email: this.email, password: this.password });
        this.setAuthToken(res.data.token);
        this.$router.push('/');
      } catch (err) {
        alert('Login failed.');
      }
    },
    ...mapActions(['setAuthToken'])
  }
};
</script>
