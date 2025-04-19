// src/store/index.js
import { createStore } from 'vuex';
import api from '@/services/api';

/* ────────────────────────────────────────────────────────────────
   Helper: pull a valid, non‑expired JWT from localStorage
   → returns null if absent, the string token otherwise
───────────────────────────────────────────────────────────────── */
function loadToken() {
    const raw = localStorage.getItem('token');
    if (!raw || raw === 'null' || raw === '""') return null;

    // (optional) verify expiry
    try {
        const { exp } = JSON.parse(atob(raw.split('.')[1]));
        if (Date.now() / 1000 > exp) return null;     // expired
    } catch { return null; }                        // malformed

    return raw;
}

export default createStore({
    /* ------------------------------------------------ STATE ---------- */
    state: {
        user : null,
        token: loadToken(),           // ← use helper instead of raw localStorage
        role : null,
        cart : []
    },

    /* ------------------------------------------- MUTATIONS ----------- */
    mutations: {
        setUser(state, user) {
            state.user = user;
            state.role = user.role;
        },
        setToken(state, token) {
            state.token = token;
            if (token)  localStorage.setItem('token', token);
            else        localStorage.removeItem('token');
            // HttpOnly cannot be set from JS; keep your cookie line for symmetry
            document.cookie = token
                ? `jwt=${token}; Secure; HttpOnly`
                : 'jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC;';
        },
        logout(state) {
            state.user = state.token = state.role = null;
            state.cart = [];
            localStorage.removeItem('token');
            document.cookie = 'jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC;';
        },
        setCart(state, cart) {
            state.cart = cart;
        }
    },

    /* --------------------------------------------- GETTERS ---------- */
    getters: {

        isAuthenticated: st => !!st.token && !!st.user
    },

    /* --------------------------------------------- ACTIONS ---------- */
    actions: {
        /* ------------- Auth ---------------- */
        async login({ commit }, credentials) {
            try {
                const res = await api.login(credentials);
                commit('setUser',  res.data.user);
                commit('setToken', res.data.token);
                return true;
            } catch (e) {
                console.error('Login failed:', e);
                return false;
            }
        },
        logout({ commit }) { commit('logout'); },

        /* ------------- Cart ---------------- */
        async fetchCart({ commit }) {
            try { commit('setCart', (await api.getCart()).data); }
            catch (e) { console.error('Error fetching cart:', e); }
        },

        async addToCart({ commit, state, getters }, { productId, quantity }) {
            if (!getters.isAuthenticated) {
                window.location.href = '/login?redirect=product';
                return;
            }
            try {
                const res    = await api.addToCart(productId, quantity);
                const exists = state.cart.find(i => i.product.id === productId);
                const cart   = exists
                    ? state.cart.map(i => (i.product.id === productId ? res.data : i))
                    : [...state.cart, res.data];
                commit('setCart', cart);
                alert('Product added to cart!');
            } catch (e) {
                console.error('Error adding to cart:', e);
                alert('Failed—please try again.');
            }
        },

        async updateCartItem({ commit, state, getters }, { productId, quantity }) {
            if (!getters.isAuthenticated) {
                window.location.href = '/login?redirect=cart';
                return;
            }
            try {
                const res = await api.updateCartItem(productId, quantity);
                commit('setCart',
                    state.cart.map(i => (i.product.id === productId ? res.data : i)));
            } catch (e) {
                console.error('Error updating cart item:', e);
                alert('Failed to update product quantity.');
            }
        },

        async removeFromCart({ commit, state, getters }, { productId }) {
            if (!getters.isAuthenticated) {
                window.location.href = '/login?redirect=cart';
                return;
            }
            try {
                await api.removeFromCart(productId);
                commit('setCart', state.cart.filter(i => i.product.id !== productId));
            } catch (e) {
                console.error('Error removing from cart:', e);
            }
        },

        /* …all previously‑sent code unchanged above… */

        /* ------------- Orders ------------- */
        /**
         * COD still uses Axios (-> api.placeOrder).
         * PAYPAL: Axios gets {approvalUrl:"…"} and JS navigates there
         * (top‑level navigation ⇒ no CORS).
         */
        async placeOrder({ commit, getters }, paymentMethod) {
            if (!getters.isAuthenticated) {
                window.location.href = '/login?redirect=checkout';
                return;
            }

            const pm = String(paymentMethod).toUpperCase();

            /* ------ PAYPAL ------ */
            if (pm === 'PAYPAL') {
                try {
                    const { data } = await api.placeOrder('PAYPAL');
                    if (data.approvalUrl) {
                        window.location.href = data.approvalUrl;
                    } else {
                        alert('PayPal approval URL missing.');
                    }
                } catch (e) {
                    console.error('PayPal order error:', e);
                    alert('Unable to start PayPal checkout.');
                }
                return;
            }

            /* ------ COD / others ------ */
            try {
                await api.placeOrder(pm);
                commit('setCart', []);
                alert('Order placed successfully!');
            } catch (e) {
                console.error('Error placing order:', e);
                alert('Failed to place order.');
            }
        }
        /* …rest of file unchanged… */

    }
});
