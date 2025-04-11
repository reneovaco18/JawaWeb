<template>
  <div class="container page-container">
    <h2 class="neon-text">{{ isEditMode ? 'Edit Product' : 'Add Product' }}</h2>

    <form @submit.prevent="handleSubmit">
      <!-- Product Name -->
      <div class="mb-3">
        <label>Name</label>
        <input v-model="product.name" required class="form-control" />
      </div>

      <!-- Product Description -->
      <div class="mb-3">
        <label>Description</label>
        <textarea v-model="product.description" class="form-control"></textarea>
      </div>

      <!-- Price -->
      <div class="mb-3">
        <label>Price ($)</label>
        <input type="number" v-model="product.price" required class="form-control" />
      </div>

      <!-- Stock Quantity -->
      <div class="mb-3">
        <label>Stock Quantity</label>
        <input type="number" v-model="product.stockQuantity" class="form-control" />
      </div>

      <!-- Category -->
      <div class="mb-3">
        <label>Category</label>
        <select v-model="selectedCategoryId" class="form-select">
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>

      <!-- File Upload: Always show this so that even new products can have an image -->
      <div class="mb-3">
        <label>Upload Product Image</label>
        <input type="file" @change="handleFileChange" class="form-control" />
      </div>

      <!-- Submit Button -->
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
      selectedFile: null  // To hold the image file
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
      // Build the DTO from form data
      const dto = {
        name: this.product.name,
        description: this.product.description,
        price: parseFloat(this.product.price),
        stockQuantity: parseInt(this.product.stockQuantity),
        categoryId: this.selectedCategoryId
      };

      if (this.isEditMode) {
        // Update product if editing
        await api.updateProduct(this.$route.params.id, dto);
        // If a new file is selected, upload it
        if (this.selectedFile) {
          await this.uploadImage(this.$route.params.id);
        }
      } else {
        // Create new product and capture its id from the response
        const response = await api.createProduct(dto);
        const newProductId = response.data.id;
        // If an image file was selected, upload it for the new product
        if (this.selectedFile) {
          await this.uploadImage(newProductId);
        }
      }

      // Redirect back to the admin dashboard
      this.$router.push('/admin');
    },
    handleFileChange(e) {
      this.selectedFile = e.target.files[0];
    },
    async uploadImage(productId) {
      if (!this.selectedFile) {
        alert('No file selected.');
        return;
      }
      try {
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        // Note: The API URL for uploading image expects the product id in the URL.
        // This call will now include the file and use our backend's defined endpoint.
        const res = await api.uploadProductImage(productId, formData);
        // Optionally update the product info if needed.
        this.product = res.data;
        alert('Image uploaded successfully!');
      } catch (error) {
        console.error('Failed to upload image', error);
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
