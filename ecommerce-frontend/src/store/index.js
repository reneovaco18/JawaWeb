import { createStore } from 'vuex';
import api from '@/services/api';

export default createStore({
    state: {
        user: null,
        token: null,
        role: null,
        cart: []
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
            state.role = user.role;
        },
        setToken(state, token) {
            state.token = token;
            localStorage.setItem('token', token);
            document.cookie = `jwt=${token}; Secure; HttpOnly`;
        },
        logout(state) {
            state.user = null;
            state.token = null;
            state.role = null;
            state.cart = [];
            localStorage.removeItem('token');
            document.cookie = `jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
        },
        setCart(state, cart) {
            state.cart = cart;
        }
    },
    actions: {
        async login({ commit }, credentials) {
            try {
                const response = await api.login(credentials);
                commit('setUser', response.data.user);
                commit('setToken', response.data.token);
                return true;
            } catch (error) {
                console.error('Login failed:', error);
                return false;
            }
        },
        logout({ commit }) {
            commit('logout');
        },
        async fetchCart({ commit }) {
            try {
                const res = await api.getCart();
                commit('setCart', res.data);
            } catch (error) {
                console.error('Error fetching cart:', error);
            }
        }
    }
});
