/**
 * GameHub Shared Navbar Component
 * Injects a unified, responsive header on every page and syncs with user authentication state.
 */

import { auth } from './auth.js';

export function initNavbar(activePage = '') {
  const headerContainer = document.getElementById('navbar-container');
  if (!headerContainer) return;

  const user = auth.getUser();
  const isLoggedIn = auth.isLoggedIn() && user;

  const currentPath = window.location.pathname;

  const html = `
    <header class="site-header">
      <div class="container nav-container">
        <a href="/index.html" class="nav-brand">
          <div class="brand-logo">GH</div>
          <span>Game<span class="glow">Hub</span></span>
        </a>

        <ul class="nav-menu">
          <li class="nav-item">
            <a href="/index.html" class="${activePage === 'games' || currentPath === '/' || currentPath.endsWith('index.html') ? 'active' : ''}">
              <span>🎮</span>
              <span class="nav-text">Games</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="/leaderboard.html" class="${activePage === 'leaderboard' || currentPath.includes('leaderboard.html') ? 'active' : ''}">
              <span>🏆</span>
              <span class="nav-text">Leaderboards</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="/profile.html" class="${activePage === 'profile' || currentPath.includes('profile.html') ? 'active' : ''}">
              <span>👤</span>
              <span class="nav-text">Profile</span>
            </a>
          </li>
        </ul>

        <div class="nav-auth">
          ${isLoggedIn ? `
            <div class="user-profile-badge" id="nav-user-dropdown" onclick="window.location.href='/profile.html'">
              <img src="${user.avatarUrl || '/assets/images/default-avatar.png'}" alt="${user.username}" class="user-avatar" onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'32\\' height=\\'32\\' viewBox=\\'0 0 24 24\\' fill=\\'%2300e5ff\\'><path d=\\'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z\\'/></svg>'">
              <div class="user-info">
                <span class="username">${user.displayName || user.username}</span>
                <span class="user-level-badge">LVL ${user.level || 1} • ${user.totalXp || 0} XP</span>
              </div>
            </div>
            <button class="btn btn-outline btn-sm" id="btn-logout" title="Sign Out">
              🚪 Logout
            </button>
          ` : `
            <a href="/login.html" class="btn btn-outline btn-sm">Login</a>
            <a href="/register.html" class="btn btn-primary btn-sm">Register</a>
          `}
        </div>
      </div>
    </header>
  `;

  headerContainer.innerHTML = html;

  if (isLoggedIn) {
    const logoutBtn = document.getElementById('btn-logout');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        auth.logout();
      });
    }
  }
}
