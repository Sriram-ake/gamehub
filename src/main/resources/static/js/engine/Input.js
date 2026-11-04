/**
 * GameHub Input Manager
 * Handles keyboard state and touch gestures while preventing disruptive browser scrolling.
 */

export class Input {
  constructor(targetElement = window) {
    this.target = targetElement;
    this.keys = new Set();
    this.justPressedKeys = new Set();
    this.touchStartX = 0;
    this.touchStartY = 0;
    this.lastSwipeDirection = null;

    // Keys that trigger page scroll if unhandled
    this.preventScrollKeys = new Set([
      'ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Space', ' '
    ]);

    this._onKeyDown = this._onKeyDown.bind(this);
    this._onKeyUp = this._onKeyUp.bind(this);
    this._onTouchStart = this._onTouchStart.bind(this);
    this._onTouchEnd = this._onTouchEnd.bind(this);

    this.bindEvents();
  }

  bindEvents() {
    window.addEventListener('keydown', this._onKeyDown);
    window.addEventListener('keyup', this._onKeyUp);
    if (this.target) {
      this.target.addEventListener('touchstart', this._onTouchStart, { passive: false });
      this.target.addEventListener('touchend', this._onTouchEnd, { passive: false });
    }
  }

  unbindEvents() {
    window.removeEventListener('keydown', this._onKeyDown);
    window.removeEventListener('keyup', this._onKeyUp);
    if (this.target) {
      this.target.removeEventListener('touchstart', this._onTouchStart);
      this.target.removeEventListener('touchend', this._onTouchEnd);
    }
  }

  _onKeyDown(e) {
    if (this.preventScrollKeys.has(e.key) || this.preventScrollKeys.has(e.code)) {
      e.preventDefault();
    }
    if (!this.keys.has(e.code)) {
      this.justPressedKeys.add(e.code);
    }
    this.keys.add(e.code);
  }

  _onKeyUp(e) {
    this.keys.delete(e.code);
  }

  _onTouchStart(e) {
    if (e.touches && e.touches.length > 0) {
      this.touchStartX = e.touches[0].clientX;
      this.touchStartY = e.touches[0].clientY;
    }
  }

  _onTouchEnd(e) {
    if (!e.changedTouches || e.changedTouches.length === 0) return;
    const dx = e.changedTouches[0].clientX - this.touchStartX;
    const dy = e.changedTouches[0].clientY - this.touchStartY;
    const absDx = Math.abs(dx);
    const absDy = Math.abs(dy);

    const minSwipeDistance = 25;

    if (Math.max(absDx, absDy) > minSwipeDistance) {
      e.preventDefault();
      if (absDx > absDy) {
        this.lastSwipeDirection = dx > 0 ? 'ArrowRight' : 'ArrowLeft';
      } else {
        this.lastSwipeDirection = dy > 0 ? 'ArrowDown' : 'ArrowUp';
      }
      this.justPressedKeys.add(this.lastSwipeDirection);
    } else {
      // Tap counts as Space / action
      this.justPressedKeys.add('Space');
    }
  }

  isDown(code) {
    return this.keys.has(code);
  }

  isJustPressed(code) {
    return this.justPressedKeys.has(code);
  }

  consumeJustPressed(code) {
    const has = this.justPressedKeys.has(code);
    this.justPressedKeys.delete(code);
    return has;
  }

  update() {
    // Clear just-pressed buffer at end of each frame
    this.justPressedKeys.clear();
    this.lastSwipeDirection = null;
  }

  destroy() {
    this.unbindEvents();
    this.keys.clear();
    this.justPressedKeys.clear();
  }
}
