<template>
  <div class="container">
    <h2>Profile</h2>
    <p><strong>Email:</strong> {{ user.email }}</p>
    <p><strong>Name:</strong> {{ user.name }}</p>
    <button class="btn btn-danger" @click="logout">Logout</button>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  computed: {
    ...mapState(['user'])
  },
  methods: {
    ...mapActions(['logout'])
  }
};
</script>
