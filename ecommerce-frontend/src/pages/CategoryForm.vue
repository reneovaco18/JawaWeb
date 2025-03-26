<template>
  <div class="container page-container">
    <h2 class="neon-text text-center">{{ isEditMode ? 'Edit Category' : 'Add Category' }}</h2>

    <form @submit.prevent="handleSubmit">
      <div class="mb-3">
        <label>Name</label>
        <input v-model="category.name" required class="form-control" />
      </div>

      <div class="mb-3">
        <label>Description</label>
        <textarea v-model="category.description" class="form-control"></textarea>
      </div>

      <button type="submit" class="btn btn-purple">
        {{ isEditMode ? 'Update' : 'Create' }}
      </button>
    </form>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  data() {
    return {
      category: {
        name: '',
        description: ''
      }
    };
  },
  computed: {
    isEditMode() {
      return !!this.$route.params.id;
    }
  },
  async mounted() {
    if (this.isEditMode) {
      const res = await api.getCategory(this.$route.params.id);
      this.category = res.data;
    }
  },
  methods: {
    async handleSubmit() {
      if (this.isEditMode) {
        await api.updateCategory(this.$route.params.id, this.category);
      } else {
        await api.createCategory(this.category);
      }
      this.$router.push('/admin');
    }
  }
};
</script>

<style scoped>
.page-container {
  padding-top: 60px;
  max-width: 600px;
}
</style>
