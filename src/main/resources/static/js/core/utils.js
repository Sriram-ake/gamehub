// ==========================================================================
// GameHub Core Utilities - Scroll Reveal, Count-Up Animation & 3D Tilt
// ==========================================================================

/**
 * Reveals elements with [data-reveal] as they scroll into view.
 */
export function initReveal() {
  if (!('IntersectionObserver' in window)) {
    document.querySelectorAll('[data-reveal]').forEach((el) => {
      el.style.opacity = '1';
    });
    return;
  }

  const io = new IntersectionObserver((entries) => {
    entries.forEach((e) => {
      if (e.isIntersecting) {
        e.target.classList.add('reveal');
        e.target.style.opacity = '1';
        io.unobserve(e.target);
      }
    });
  }, { threshold: 0.15 });

  document.querySelectorAll('[data-reveal]').forEach((el) => {
    el.style.opacity = '0';
    io.observe(el);
  });
}

/**
 * Animates a numeric element smoothly from 0 to target value with cubic ease-out.
 */
export function countUp(el, target, duration = 900) {
  if (!el) return;
  const numTarget = Number(target) || 0;
  const start = performance.now();

  const tick = (now) => {
    const t = Math.min((now - start) / duration, 1);
    const eased = 1 - Math.pow(1 - t, 3);
    el.textContent = Math.round(numTarget * eased).toLocaleString();
    if (t < 1) {
      requestAnimationFrame(tick);
    } else {
      el.textContent = numTarget.toLocaleString();
    }
  };

  requestAnimationFrame(tick);
}

/**
 * Adds an interactive 3D perspective tilt effect on mousemove / pointermove.
 */
export function initTilt(el, max = 6) {
  if (!el) return;

  el.addEventListener('pointermove', (e) => {
    const r = el.getBoundingClientRect();
    const x = (e.clientX - r.left) / r.width - 0.5;
    const y = (e.clientY - r.top) / r.height - 0.5;
    el.style.transform = `perspective(900px) rotateY(${x * max}deg) rotateX(${-y * max}deg)`;
  });

  el.addEventListener('pointerleave', () => {
    el.style.transform = '';
  });
}
