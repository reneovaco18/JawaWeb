import { createStore } from 'vuex';
import axios from 'axios';

export default createStore({
    state: {
        user: null,
        token: localStorage.getItem('token') || '',
        cart: []
    },
    mutations: {
        SET_USER(state, user) {
            state.user = user;
        },
        SET_TOKEN(state, token) {
            state.token = token;
            localStorage.setItem('token', token);
        },
        LOGOUT(state) {
            state.user = null;
            state.token = '';
            localStorage.removeItem('token');
        },
        SET_CART(state, cart) {
            state.cart = cart;
        }
    },
    actions: {
        async login({ commit }, credentials) {
            const res = await axios.post('/api/auth/login', credentials);
            commit('SET_TOKEN', res.data.token);
            const userRes = await axios.get('/api/auth/me', {
                headers: { Authorization: `Bearer ${res.data.token}` }
            });
            commit('SET_USER', userRes.data);
        },
        async logout({ commit }) {
            commit('LOGOUT');
        },
        async fetchCart({ commit }) {
            const res = await axios.get('/api/cart', {
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
            });
            commit('SET_CART', res.data);
        }
    }
});
