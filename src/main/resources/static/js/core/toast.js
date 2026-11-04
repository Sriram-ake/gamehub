/**
 * GameHub Toast Notification Manager
 * Displays sleek animated toasts for feedback and achievement unlocks.
 */

let container = null;

function ensureContainer() {
  if (!container || !document.body.contains(container)) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  return container;
}

export function showToast(message, type = 'info', duration = 3500) {
  const cont = ensureContainer();
  const el = document.createElement('div');
  el.className = `toast toast-${type}`;

  let icon = 'ℹ️';
  if (type === 'success') icon = '✅';
  if (type === 'error') icon = '⚠️';

  el.innerHTML = `
    <span class="toast-icon">${icon}</span>
    <span class="toast-message" style="font-size: 0.9rem;">${message}</span>
  `;

  cont.appendChild(el);

  setTimeout(() => {
    el.style.opacity = '0';
    el.style.transform = 'translateX(50px)';
    setTimeout(() => {
      if (cont.contains(el)) cont.removeChild(el);
    }, 250);
  }, duration);
}

export function showAchievementToast(achievement) {
  const cont = ensureContainer();
  const el = document.createElement('div');
  el.className = 'toast toast-achievement';

  el.innerHTML = `
    <div class="toast-icon">${achievement.icon || '🏆'}</div>
    <div style="display: flex; flex-direction: column; gap: 2px;">
      <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--accent-gold); font-weight: 700; letter-spacing: 0.05em;">
        Achievement Unlocked!
      </div>
      <div style="font-weight: 700; font-size: 0.95rem; color: #fff;">
        ${achievement.title}
      </div>
      <div style="font-size: 0.8rem; color: var(--text-muted);">
        ${achievement.description || ''} <span style="color: var(--accent-gold); font-weight: 600;">+${achievement.xpReward || 10} XP</span>
      </div>
    </div>
  `;

  cont.appendChild(el);

  setTimeout(() => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(-20px)';
    setTimeout(() => {
      if (cont.contains(el)) cont.removeChild(el);
    }, 300);
  }, 4500);
}

export const toast = {
  success: (msg, dur) => showToast(msg, 'success', dur),
  error: (msg, dur) => showToast(msg, 'error', dur),
  info: (msg, dur) => showToast(msg, 'info', dur),
  achievement: (ach) => showAchievementToast(ach)
};
