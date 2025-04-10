<template>
  <div class="container page-container">
    <h2 class="neon-text">{{ isEditMode ? 'Edit Product' : 'Add Product' }}</h2>

    <form @submit.prevent="handleSubmit">
      <div class="mb-3">
        <label>Name</label>
        <input v-model="product.name" required class="form-control" />
      </div>

      <div class="mb-3">
        <label>Description</label>
        <textarea v-model="product.description" class="form-control"></textarea>
      </div>

      <div class="mb-3">
        <label>Price ($)</label>
        <input type="number" v-model="product.price" required class="form-control" />
      </div>

      <div class="mb-3">
        <label>Stock Quantity</label>
        <input type="number" v-model="product.stockQuantity" class="form-control" />
      </div>

      <div class="mb-3">
        <label>Category</label>
        <select v-model="selectedCategoryId" class="form-select">
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>

      <!-- Only show the upload if editing an existing product (that we have an ID for) -->
      <div class="mb-3" v-if="isEditMode">
        <label>Upload Product Image</label>
        <input type="file" @change="handleFileChange" />
        <button type="button" class="btn btn-purple" @click="uploadImage">
          Upload
        </button>
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
      product: {
        name: '',
        description: '',
        price: '',
        stockQuantity: 0,
        category: {}
      },
      categories: [],
      selectedCategoryId: null,
      selectedFile: null // We'll store the file here
    };
  },
  computed: {
    isEditMode() {
      return !!this.$route.params.id;
    }
  },
  methods: {
    async fetchCategories() {
      const res = await api.getCategories();
      this.categories = res.data;
    },
    async fetchProduct() {
      const res = await api.getProduct(this.$route.params.id);
      this.product = res.data;
      this.selectedCategoryId = res.data.category.id;
    },
    async handleSubmit() {
      // Build the DTO
      const dto = {
        name: this.product.name,
        description: this.product.description,
        price: parseFloat(this.product.price),
        stockQuantity: parseInt(this.product.stockQuantity),
        categoryId: this.selectedCategoryId
      };

      if (this.isEditMode) {
        await api.updateProduct(this.$route.params.id, dto);
      } else {
        // Create new product
        await api.createProduct(dto);
      }

      // Navigate back to admin
      this.$router.push('/admin');
    },
    handleFileChange(e) {
      this.selectedFile = e.target.files[0];
    },
    async uploadImage() {
      if (!this.selectedFile) {
        alert('No file selected.');
        return;
      }
      try {
        const formData = new FormData();
        formData.append('file', this.selectedFile);

        // Upload to /products/:id/uploadImage
        const res = await api.uploadProductImage(this.$route.params.id, formData);
        this.product = res.data;
        alert('Image uploaded successfully!');
      } catch (err) {
        console.error('Failed to upload image', err);
        alert('Image upload failed!');
      }
    }
  },
  async mounted() {
    await this.fetchCategories();
    if (this.isEditMode) {
      await this.fetchProduct();
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
