// src/services/api.js
import axios from 'axios';
import store from '@/store';

/* ------------------------------------------------------------------ */
/*  Axios instance                                                    */
/* ------------------------------------------------------------------ */
const apiClient = axios.create({
    baseURL       : 'http://localhost:8085/api',
    headers       : { 'Content-Type': 'application/json' },
    withCredentials: true
});

/* -------- Add JWT to every request -------------------------------- */
apiClient.interceptors.request.use(cfg => {
    const t = store.state.token;
    if (t) cfg.headers.Authorization = `Bearer ${t}`;
    return cfg;
});

/* -------- 403 ⇒ force logout -------------------------------------- */
apiClient.interceptors.response.use(
    r => r,
    err => {
        if (err.response?.status === 403) {
            store.dispatch('logout');
            window.location.href = '/login?error=session_expired';
        }
        return Promise.reject(err);
    }
);

/* ------------------------------------------------------------------ */
/*  REST helpers                                                      */
/* ------------------------------------------------------------------ */
export default {
    /* — Products — */            getProducts()                { return apiClient.get('/products'); },
    getProduct(id)               { return apiClient.get(`/products/${id}`); },
    getProductsByCategory(id)    { return apiClient.get(`/products/by-category/${id}`); },

    /* — Categories — */          getCategories()              { return apiClient.get('/categories'); },
    createCategory(d)            { return apiClient.post('/categories', d); },
    updateCategory(id, d)        { return apiClient.put(`/categories/${id}`, d); },
    deleteCategory(id)           { return apiClient.delete(`/categories/${id}`); },

    /* — Auth — */                register(d)                  { return apiClient.post('/auth/register', d); },
    login(c)                     { return apiClient.post('/auth/login', c); },

    /* — Cart — */                getCart()                    { return apiClient.get('/cart'); },
    addToCart(pid, q)            { return apiClient.post(`/cart?productId=${pid}&quantity=${q}`); },
    updateCartItem(pid, q)       { return apiClient.put(`/cart?productId=${pid}&quantity=${q}`); },
    removeFromCart(pid)          { return apiClient.delete(`/cart/${pid}`); },

    /* — Orders — */
    getOrders()                   { return apiClient.get('/orders'); },
    getOrder(id)                  { return apiClient.get(`/orders/${id}`); },
    placeOrder(paymentMethod)     { return apiClient.post('/orders', null, { params: { paymentMethod }}); },

    /* — Admin logs — */          getLoginLogs()               { return apiClient.get('/admin/logins'); },

    /* — Product management — */  deleteProduct(id)            { return apiClient.delete(`/products/${id}`); },
    createProduct(d)             { return apiClient.post('/products', d); },
    updateProduct(id, d)         { return apiClient.put(`/products/${id}`, d); },

    /* — Image upload — */        uploadProductImage(pid, fd)  {
        return apiClient.post(`/products/${pid}/uploadImage`, fd,
            { headers:{ 'Content-Type':'multipart/form-data' }});
    }
};
