import axios from 'axios';
import store from '@/store';

const apiClient = axios.create({
    baseURL: 'http://localhost:8085/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true
});

// Request interceptor to add token
apiClient.interceptors.request.use(config => {
    const token = store.state.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Response interceptor to handle 403 errors
apiClient.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 403) {
            store.dispatch('logout');
            window.location.href = '/login?error=session_expired';
        }
        return Promise.reject(error);
    }
);

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
    getCategory(id) {
        return apiClient.get(`/categories/${id}`);
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
        return apiClient.get('/cart');
    },
    addToCart(productId, quantity) {
        return apiClient.post(`/cart?productId=${productId}&quantity=${quantity}`);
    },
    removeFromCart(productId) {
        return apiClient.delete(`/cart/${productId}`);
    },

    // Orders API
    getOrders() {
        return apiClient.get('/orders');
    },
    placeOrder(paymentMethod) {
        return apiClient.post(`/orders?paymentMethod=${paymentMethod}`);
    },

    // Admin Login Logs API
    getLoginLogs() {
        return apiClient.get('/admin/logins');
    },

    // Product management
    deleteProduct(id) {
        return apiClient.delete(`/products/${id}`);
    },
    createProduct(productData) {
        return apiClient.post('/products', productData);
    },
    updateProduct(id, productData) {
        return apiClient.put(`/products/${id}`, productData);
    }
};
