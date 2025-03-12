import { createRouter, createWebHistory } from 'vue-router';
import MainHomePage from '../pages/MainHomePage.vue';
import ShoppingCart from '../pages/ShoppingCart.vue';
import UserOrders from '../pages/UserOrders.vue';
import UserProfile from '../pages/UserProfile.vue';
import LoginPage from '../pages/LoginPage.vue';
import RegisterPage from '../pages/RegisterPage.vue';

const routes = [
    { path: '/', component: MainHomePage },
    { path: '/cart', component: ShoppingCart },
    { path: '/orders', component: UserOrders },
    { path: '/profile', component: UserProfile },
    { path: '/login', component: LoginPage },
    { path: '/register', component: RegisterPage }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
