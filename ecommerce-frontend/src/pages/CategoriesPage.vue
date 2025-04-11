<template>
  <div class="container page-container">
    <h2 class="neon-text">Categories</h2>

    <!-- Neon-Styled Table for Categories -->
    <table class="neon-table">
      <thead>
      <tr>
        <th>Name</th>
        <!-- Add other columns here, e.g., <th>Description</th> if needed -->
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="category in categories" :key="category.id">
        <td>{{ category.name }}</td>
        <!-- If you have category.description, you could add:
             <td>{{ category.description }}</td> -->
        <td>
          <!-- Example of a button linking to products under this category -->
          <router-link
              :to="'/category/' + category.id"
              class="btn btn-blue"
          >
            View Products
          </router-link>
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Example: If no categories exist, show a fallback -->
    <div v-if="categories.length === 0" class="no-results">
      No categories available
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  data() {
    return {
      categories: []
    };
  },
  async mounted() {
    try {
      const res = await api.getCategories();
      this.categories = res.data;
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  }
};
</script>

<style scoped>
/* Container styling similar to your admin dashboard */
.page-container {
  padding-top: 100px;
  max-width: 1200px;
  margin: 0 auto;

  background-color: #121212;
  color: #e0e0e0;
  font-family: 'Roboto', sans-serif;
}

/* Neon text for the heading */
.neon-text {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.7);
  margin-bottom: 30px;
}

/* Neon-styled table, similar to your second screenshot */
.neon-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.2);
}

/* Table header style */
.neon-table th {
  background-color: #2d2d2d;
  color: #00ffff;
  text-align: left;
  padding: 12px 15px;
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
}

/* Table cell style */
.neon-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #333;
}

/* Table row background color / hover effect */
.neon-table tbody tr {
  background-color: #1e1e1e;
  transition: background-color 0.3s;
}
.neon-table tbody tr:hover {
  background-color: #2a2a2a;
}

/* Example button styles that match your second screenshot */
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  margin-right: 8px;
  margin-bottom: 8px;
}

.btn-blue {
  background-color: #2196f3;
  color: white;
}
.btn-blue:hover {
  background-color: #1976d2;
  box-shadow: 0 0 10px rgba(33, 150, 243, 0.5);
}

/* If no categories exist, show a fallback message */
.no-results {
  text-align: center;
  padding: 20px;
  color: #90a4ae;
  font-style: italic;
  background-color: #1e1e1e;
  border-radius: 8px;
  margin-top: 10px;
}
</style>
