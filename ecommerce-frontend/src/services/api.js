import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: { 'Content-Type': 'application/json' }
});

export default {
    // Product API
    getProducts() {
        return apiClient.get('/products');
    },
    getProduct(id) {
        return apiClient.get(`/products/${id}`);
    },
    getCategories() {
        return apiClient.get('/categories');
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
    }
};
