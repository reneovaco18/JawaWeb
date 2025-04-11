import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';
import LoginPage from '@/pages/LoginPage.vue';
import AdminDashboard from '@/pages/AdminDashboard.vue';
import ShoppingCart from '@/pages/ShoppingCart.vue';
import MainHomePage from '@/pages/MainHomePage.vue'; // Home shows product list
import RegisterPage from '@/pages/RegisterPage.vue';
import ProductDetails from '@/pages/ProductDetails.vue';
import OrderHistory from '@/pages/UserOrders.vue';
import Profile from '@/pages/UserProfile.vue';
import CategoriesPage from '@/pages/CategoriesPage.vue';
import CategoryProducts from '@/pages/CategoryProducts.vue';
import ProductForm from '@/pages/ProductForm.vue';
import OrderDetails from "@/pages/OrderDetails.vue";

const routes = [
    { path: '/', component: MainHomePage },
    { path: '/login', component: LoginPage },
    { path: '/register', component: RegisterPage },
    { path: '/product/:id', component: ProductDetails },
    { path: '/cart', component: ShoppingCart, meta: { requiresAuth: true } },
    { path: '/admin', component: AdminDashboard, meta: { requiresAuth: true, isAdmin: true } },
    { path: '/orders', component: OrderHistory, meta: { requiresAuth: true } },
    { path: '/profile', component: Profile, meta: { requiresAuth: true } },
    { path: '/categories', component: CategoriesPage },
    { path: '/category/:id', component: CategoryProducts },
    { path: '/order/:orderId', component: OrderDetails },

    { path: '/admin/products/new', component: ProductForm, meta: { requiresAuth: true, isAdmin: true } },
    { path: '/admin/categories/new', component: () => import('@/pages/CategoryForm.vue'), meta: { requiresAuth: true, isAdmin: true } },
    { path: '/admin/categories/:id/edit', component: () => import('@/pages/CategoryForm.vue'), meta: { requiresAuth: true, isAdmin: true } },

    { path: '/admin/products/:id/edit', component: ProductForm, meta: { requiresAuth: true, isAdmin: true } }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const isAuthenticated = store.state.token !== null;
    const isAdmin = store.state.role === 'ADMIN';

    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login');
    } else if (to.meta.isAdmin && !isAdmin) {
        alert('Access Denied: Admins Only');
        next('/');
    } else {
        next();
    }
});

export default router;
