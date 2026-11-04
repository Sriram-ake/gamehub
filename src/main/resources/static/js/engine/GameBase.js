/**
 * GameHub Core Engine - GameBase
 * Base class managing the fixed-timestep loop, DPI scaling, pause/resume, HUD, and score submission.
 * Conforms to the Neon Arcade Glass Design System.
 */

import { Input } from './Input.js';
import { auth } from '../core/auth.js';
import { toast } from '../core/toast.js';

export const GameState = {
  MENU: 'MENU',
  PLAYING: 'PLAYING',
  PAUSED: 'PAUSED',
  GAMEOVER: 'GAMEOVER'
};

/**
 * Reads CSS custom properties dynamically from document body so canvas and UI never drift.
 */
export function readTheme() {
  const css = getComputedStyle(document.body);
  const get = (name) => css.getPropertyValue(name).trim();
  return {
    accent: get('--game-accent') || '#8b5cf6',
    glow: get('--game-accent-glow') || 'rgba(139, 92, 246, 0.45)',
    bg: get('--bg-0') || '#07060f',
    surface: get('--bg-2') || '#15122b',
    text: get('--text-0') || '#f5f3ff',
    secondary: get('--secondary') || '#22d3ee',
    danger: get('--danger') || '#fb7185',
    warning: get('--warning') || '#fbbf24',
  };
}

/**
 * Neon glow helper reused by every game canvas.
 */
export function neon(ctx, color, blur = 14) {
  ctx.shadowColor = color;
  ctx.shadowBlur = blur;
}

/**
 * Crisp rendering on high-DPI screens.
 */
export function fitCanvas(canvas, logicalW, logicalH) {
  const dpr = window.devicePixelRatio || 1;
  canvas.width = logicalW * dpr;
  canvas.height = logicalH * dpr;
  canvas.getContext('2d').setTransform(dpr, 0, 0, dpr, 0, 0);
}

export class GameBase {
  /**
   * @param {string} canvasId - HTML canvas element ID
   * @param {string} gameSlug - unique game identifier (e.g. 'snake', 'flappy', '2048', 'tictactoe')
   * @param {number} virtualWidth - internal logical width
   * @param {number} virtualHeight - internal logical height
   */
  constructor(canvasId, gameSlug, virtualWidth = 800, virtualHeight = 600) {
    this.canvas = document.getElementById(canvasId);
    if (!this.canvas) {
      throw new Error(`Canvas element with ID '${canvasId}' not found.`);
    }
    this.ctx = this.canvas.getContext('2d');
    this.slug = gameSlug;
    this.virtualWidth = virtualWidth;
    this.virtualHeight = virtualHeight;

    this.theme = readTheme();
    this.state = GameState.MENU;
    this.input = new Input(this.canvas);

    this.score = 0;
    this.bestScore = 0;
    this.level = 1;
    this.startTime = null;
    this.durationSeconds = 0;

    // Loop variables
    this.lastTime = 0;
    this.animationFrameId = null;
    this.isRunning = false;

    // Load local best score
    this.loadLocalBest();

    // DOM Overlays (optional links if defined in HTML)
    this.startOverlay = document.getElementById('start-overlay');
    this.pauseOverlay = document.getElementById('pause-overlay');
    this.gameOverOverlay = document.getElementById('game-over-overlay');

    this._bindSystemEvents();
    this.setupCanvasDPI();
  }

  _bindSystemEvents() {
    window.addEventListener('resize', () => {
      this.theme = readTheme();
      this.setupCanvasDPI();
    });

    // Auto-pause when user leaves tab
    document.addEventListener('visibilitychange', () => {
      if (document.hidden && this.state === GameState.PLAYING) {
        this.pause();
      }
    });

    window.addEventListener('blur', () => {
      if (this.state === GameState.PLAYING) {
        this.pause();
      }
    });

    // Global Pause & Restart keyboard shortcuts
    window.addEventListener('keydown', (e) => {
      if (e.code === 'KeyP' || e.code === 'Escape') {
        if (this.state === GameState.PLAYING) {
          this.pause();
        } else if (this.state === GameState.PAUSED) {
          this.resume();
        }
      }
      if (e.code === 'KeyR' && this.state === GameState.GAMEOVER) {
        this.restart();
      }
    });
  }

  setupCanvasDPI() {
    const dpr = window.devicePixelRatio || 1;
    const rect = this.canvas.getBoundingClientRect();
    const cssWidth = rect.width || this.virtualWidth;
    const cssHeight = rect.height || this.virtualHeight;

    this.canvas.width = Math.round(cssWidth * dpr);
    this.canvas.height = Math.round(cssHeight * dpr);

    this.ctx.resetTransform?.();
    this.ctx.scale(this.canvas.width / this.virtualWidth, this.canvas.height / this.virtualHeight);
  }

  loadLocalBest() {
    const saved = localStorage.getItem(`gamehub_best_${this.slug}`);
    this.bestScore = saved ? parseInt(saved, 10) : 0;
  }

  saveLocalBest(newBest) {
    this.bestScore = newBest;
    localStorage.setItem(`gamehub_best_${this.slug}`, newBest.toString());
  }

  start() {
    if (this.isRunning) return;
    this.isRunning = true;
    this.init();
    this.state = GameState.MENU;
    this.lastTime = performance.now();
    this.loop = this.loop.bind(this);
    this.animationFrameId = requestAnimationFrame(this.loop);
  }

  play() {
    this.reset();
    this.state = GameState.PLAYING;
    this.startTime = Date.now();
    this.hideOverlays();
  }

  pause() {
    if (this.state !== GameState.PLAYING) return;
    this.state = GameState.PAUSED;
    if (this.pauseOverlay) this.pauseOverlay.classList.remove('hidden');
  }

  resume() {
    if (this.state !== GameState.PAUSED) return;
    this.state = GameState.PLAYING;
    this.lastTime = performance.now();
    if (this.pauseOverlay) this.pauseOverlay.classList.add('hidden');
  }

  restart() {
    this.play();
  }

  gameOver() {
    if (this.state === GameState.GAMEOVER) return;
    this.state = GameState.GAMEOVER;
    this.durationSeconds = this.startTime ? Math.max(1, Math.round((Date.now() - this.startTime) / 1000)) : 1;

    const isNewHighScore = this.score > this.bestScore;
    if (isNewHighScore) {
      this.saveLocalBest(this.score);
    }

    this.onGameOver(this.score, this.durationSeconds, this.level, isNewHighScore);
    this.submitScore(this.score, this.durationSeconds, this.level);
  }

  loop(currentTime) {
    if (!this.isRunning) return;

    // Delta time in seconds, clamped to 0.1s to avoid physics spiral
    let dt = (currentTime - this.lastTime) / 1000;
    dt = Math.min(dt, 0.1);
    this.lastTime = currentTime;

    if (this.state === GameState.PLAYING) {
      this.update(dt);
    }

    this.render(this.ctx);

    // Render Shared HUD in PLAYING and PAUSED states
    this.drawHUD(this.ctx);

    // Post-frame input cleanup
    this.input.update();

    this.animationFrameId = requestAnimationFrame(this.loop);
  }

  drawHUD(ctx) {
    ctx.save();
    // Background glass banner for HUD
    ctx.fillStyle = 'rgba(7, 6, 15, 0.72)';
    ctx.fillRect(0, 0, this.virtualWidth, 48);

    // Border bottom
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(0, 48);
    ctx.lineTo(this.virtualWidth, 48);
    ctx.stroke();

    // Score display (Top Left)
    ctx.fillStyle = this.theme.accent;
    ctx.font = '700 16px "JetBrains Mono", "Orbitron", monospace';
    ctx.textAlign = 'left';
    ctx.fillText(`SCORE: ${this.score}`, 20, 30);

    // Best score display (Top Right)
    ctx.fillStyle = this.theme.warning;
    ctx.textAlign = 'right';
    ctx.fillText(`BEST: ${this.bestScore}`, this.virtualWidth - 20, 30);

    // Center pause reminder
    if (this.state === GameState.PLAYING) {
      ctx.fillStyle = this.theme.text;
      ctx.globalAlpha = 0.6;
      ctx.font = '12px "Inter", sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('[P] Pause', this.virtualWidth / 2, 30);
      ctx.globalAlpha = 1.0;
    } else if (this.state === GameState.PAUSED) {
      ctx.fillStyle = this.theme.warning;
      ctx.font = '700 16px "Orbitron", sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('PAUSED', this.virtualWidth / 2, 30);
    }

    // Reset shadow blur to preserve high performance
    ctx.shadowBlur = 0;
    ctx.restore();
  }

  hideOverlays() {
    if (this.startOverlay) this.startOverlay.classList.add('hidden');
    if (this.pauseOverlay) this.pauseOverlay.classList.add('hidden');
    if (this.gameOverOverlay) this.gameOverOverlay.classList.add('hidden');
  }

  async submitScore(score, durationSeconds, levelReached) {
    const payload = {
      gameSlug: this.slug,
      score,
      durationSeconds,
      levelReached
    };

    if (!auth.isLoggedIn()) {
      auth.savePendingScore(payload);
      toast.info('Game Over! Log in to save your score to the global leaderboards.', 4500);
      return;
    }

    try {
      const { api } = await import('../core/api.js');
      const res = await api.post('/scores', payload);
      if (res) {
        if (res.newHighScore) {
          toast.success(`🎉 New High Score: ${score}! Rank #${res.rank || 1}`);
        }
        if (res.newAchievements && res.newAchievements.length > 0) {
          res.newAchievements.forEach(ach => toast.achievement(ach));
        }
      }
    } catch {
      console.log('Score submitted locally fallback:', payload);
    }
  }

  onGameOver(finalScore, duration, level, isNewBest) {
    if (this.gameOverOverlay) {
      const finalScoreEl = document.getElementById('go-final-score');
      const bestScoreEl = document.getElementById('go-best-score');
      const durationEl = document.getElementById('go-duration');
      const titleEl = document.getElementById('go-title');

      if (finalScoreEl) finalScoreEl.textContent = finalScore;
      if (bestScoreEl) bestScoreEl.textContent = this.bestScore;
      if (durationEl) durationEl.textContent = `${duration}s`;
      if (titleEl) {
        titleEl.textContent = isNewBest ? 'NEW HIGH SCORE!' : 'GAME OVER';
        titleEl.className = isNewBest ? 'overlay-title' : 'overlay-title game-over';
      }

      this.gameOverOverlay.classList.remove('hidden');
    }
  }

  destroy() {
    this.isRunning = false;
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
    }
    this.input.destroy();
  }

  // To be implemented by subclasses
  init() {}
  update(dt) {}
  render(ctx) {}
  reset() {}
}
