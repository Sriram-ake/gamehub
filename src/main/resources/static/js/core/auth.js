/**
 * GameHub Authentication Manager
 * Manages user session state, local storage persistence, and guest score preservation.
 */

const TOKEN_KEY = 'gamehub_token';
const USER_KEY = 'gamehub_user';
const PENDING_SCORE_KEY = 'gamehub_pending_score';

export const auth = {
  isLoggedIn() {
    return !!localStorage.getItem(TOKEN_KEY);
  },

  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  getUser() {
    const raw = localStorage.getItem(USER_KEY);
    try {
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  setSession(authResponse) {
    if (authResponse && authResponse.token) {
      localStorage.setItem(TOKEN_KEY, authResponse.token);
      localStorage.setItem(USER_KEY, JSON.stringify(authResponse.user));
    }
  },

  updateUser(userData) {
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
  },

  clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  logout() {
    this.clearSession();
    window.location.href = '/login.html';
  },

  requireAuth(redirectUrl = null) {
    if (!this.isLoggedIn()) {
      const returnUrl = redirectUrl || window.location.pathname;
      window.location.href = `/login.html?returnUrl=${encodeURIComponent(returnUrl)}`;
      return false;
    }
    return true;
  },

  savePendingScore(scorePayload) {
    sessionStorage.setItem(PENDING_SCORE_KEY, JSON.stringify(scorePayload));
  },

  getPendingScore() {
    const data = sessionStorage.getItem(PENDING_SCORE_KEY);
    try {
      return data ? JSON.parse(data) : null;
    } catch {
      return null;
    }
  },

  clearPendingScore() {
    sessionStorage.removeItem(PENDING_SCORE_KEY);
  }
};
