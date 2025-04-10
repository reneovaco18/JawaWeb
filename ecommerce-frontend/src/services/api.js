import axios from 'axios';
import store from '@/store';

const apiClient = axios.create({
    baseURL: 'http://localhost:8085/api', // or your domain
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true
});

// Add token to every request
apiClient.interceptors.request.use(config => {
    const token = store.state.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Handle 403 errors
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

    // Auth
    register(userData) {
        return apiClient.post('/auth/register', userData);
    },
    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },

    // Cart
    getCart() {
        return apiClient.get('/cart');
    },
    addToCart(productId, quantity) {
        return apiClient.post(`/cart?productId=${productId}&quantity=${quantity}`);
    },
    removeFromCart(productId) {
        return apiClient.delete(`/cart/${productId}`);
    },

    // Orders
    getOrders() {
        return apiClient.get('/orders');
    },
    placeOrder(paymentMethod) {
        return apiClient.post(`/orders?paymentMethod=${paymentMethod}`);
    },

    // Admin logs
    getLoginLogs() {
        return apiClient.get('/admin/logins');
    },

    // Product Management
    deleteProduct(id) {
        return apiClient.delete(`/products/${id}`);
    },
    createProduct(productData) {
        return apiClient.post('/products', productData);
    },
    updateProduct(id, productData) {
        return apiClient.put(`/products/${id}`, productData);
    },

    // ** New: Upload product image **
    uploadProductImage(productId, formData) {
        // Must send multipart/form-data
        return apiClient.post(`/products/${productId}/uploadImage`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
};
