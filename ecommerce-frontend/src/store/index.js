import api from '@/services/api';
import { createStore } from 'vuex';

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
        async addToCart({ state }, { productId, quantity }) {
            try {
                const response = await api.addToCart(productId, quantity);
                const index = state.cart.findIndex(item => item.product.id === productId);
                if (index !== -1) {
                    state.cart[index].quantity = response.data.quantity;
                } else {
                    state.cart.push(response.data);
                }
                alert('Product added to cart successfully!');
            } catch (error) {
                console.error('Error adding product to cart:', error);
                alert('Failed to add product to cart. Please try again.');
            }
        },
        async updateCartItem({ state }, { productId, quantity }) {
            try {
                const response = await api.updateCartItem(productId, quantity);
                const index = state.cart.findIndex(item => item.product.id === productId);
                if (index !== -1) {
                    state.cart[index] = response.data;
                }
                alert('Cart updated successfully!');
            } catch (error) {
                console.error('Error updating cart item:', error);
                alert('Failed to update product quantity. Please try again.');
            }
        },
        async removeFromCart({ commit, state }, { productId }) {
            try {
                await api.removeFromCart(productId);
                const updatedCart = state.cart.filter(item => item.product.id !== productId);
                commit('setCart', updatedCart);
                alert('Product removed from cart successfully!');
            } catch (error) {
                console.error('Error removing product from cart:', error);
                alert('Failed to remove product from cart.');
            }
        },
        async fetchCart({ commit }) {
            try {
                const res = await api.getCart();
                commit('setCart', res.data);
            } catch (error) {
                console.error('Error fetching cart:', error);
            }
        },
        async placeOrder({ commit }, { paymentMethod }) {
            try {
                await api.placeOrder(paymentMethod);
                commit('setCart', []); // Clear the cart after successful order
                alert('Order placed successfully!');
            } catch (error) {
                console.error('Error placing order:', error);
                alert('Failed to place order. Please try again.');
            }
        }
    }
});
