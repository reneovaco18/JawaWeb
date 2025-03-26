// src/services/api.js
import axios from 'axios';

// This creates a reusable axios instance with a predefined baseURL
const apiClient = axios.create({
    baseURL: 'http://localhost:8085/api', // Your backend base URL
    headers: { 'Content-Type': 'application/json' },
    withCredentials: true // ⬅️ Required for sending cookies and allowing CORS with credentials
});

export default {
    // Product API
    getProducts() {
        return apiClient.get('/products');
    },
    getProduct(id) {
        return apiClient.get(`/products/${id}`);
    },
    getProductsByCategory(categoryId) {
        return apiClient.get(`/products/by-category/${categoryId}`);
    },

    // Category API
    getCategories() {
        return apiClient.get('/categories');
    },
    createCategory(categoryData) {
        return apiClient.post('/categories', categoryData);
    },
    updateCategory(id, categoryData) {
        return apiClient.put(`/categories/${id}`, categoryData);
    },
    deleteCategory(id) {
        return apiClient.delete(`/categories/${id}`);
    },

    // Auth API
    register(userData) {
        return apiClient.post('/auth/register', userData);
    },
    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },

    // Cart API
    getCart() {
        return apiClient.get('/cart', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },
    addToCart(productId, quantity) {
        return apiClient.post(`/cart?productId=${productId}&quantity=${quantity}`, null, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },
    removeFromCart(productId) {
        return apiClient.delete(`/cart/${productId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },

    // Orders API
    getOrders() {
        return apiClient.get('/orders', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },
    placeOrder(paymentMethod) {
        return apiClient.post(`/orders?paymentMethod=${paymentMethod}`, null, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },

    // Admin Login Logs API
    getLoginLogs() {
        return apiClient.get('/admin/logins', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },

    // Product deletion for admin
    deleteProduct(id) {
        return apiClient.delete(`/products/${id}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },
    createProduct(productData) {
        return apiClient.post('/products', productData, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },
    updateProduct(id, productData) {
        return apiClient.put(`/products/${id}`, productData, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
    },
};
