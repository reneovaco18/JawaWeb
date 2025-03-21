<template>
  <button class="btn btn-danger" @click="handleLogout">Logout</button>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  methods: {
    ...mapActions(['logout']),
    handleLogout() { // renamed method to avoid collision
      this.logout();
      this.$router.push('/login');
    }
  }
};
</script>
