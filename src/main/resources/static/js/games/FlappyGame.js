/**
 * GameHub - Flappy Bird Game
 * Neon arcade bird physics with smooth gravity, flap impulse, moving pipes, and particle pass effects.
 */

import { GameBase, GameState } from '../engine/GameBase.js';

export class FlappyGame extends GameBase {
  constructor(canvasId = 'game-canvas') {
    super(canvasId, 'flappy', 800, 600);

    this.gravity = 900; // px/s^2
    this.flapImpulse = -310; // px/s
    this.pipeSpeed = 175; // px/s
    this.pipeSpawnInterval = 1.7; // seconds between pipes
    this.pipeGap = 150; // px gap between top and bottom pipe

    this.bird = {
      x: 180,
      y: 300,
      radius: 16,
      vy: 0,
      rotation: 0
    };

    this.pipes = [];
    this.particles = [];
    this.spawnTimer = 0;
  }

  init() {
    this.reset();
  }

  reset() {
    this.score = 0;
    this.level = 1;
    this.bird.x = 180;
    this.bird.y = 300;
    this.bird.vy = 0;
    this.bird.rotation = 0;
    this.pipes = [];
    this.particles = [];
    this.spawnTimer = 0.5; // first pipe spawns soon
  }

  flap() {
    if (this.state === GameState.PLAYING) {
      this.bird.vy = this.flapImpulse;
      this.spawnParticles(this.bird.x - 8, this.bird.y + 10, '#00e5ff', 5);
    }
  }

  update(dt) {
    const input = this.input;

    if (this.state === GameState.MENU) {
      if (input.isJustPressed('Space') || input.isJustPressed('ArrowUp') || input.isJustPressed('KeyW')) {
        this.play();
        this.flap();
        return;
      }
    }

    if (this.state !== GameState.PLAYING) return;

    if (input.isJustPressed('Space') || input.isJustPressed('ArrowUp') || input.isJustPressed('KeyW')) {
      this.flap();
    }

    // Bird physics
    this.bird.vy += this.gravity * dt;
    this.bird.y += this.bird.vy * dt;

    // Bird pitch angle based on velocity
    this.bird.rotation = Math.min(Math.PI / 4, Math.max(-Math.PI / 4, (this.bird.vy / 400)));

    // Ceiling and floor limits
    if (this.bird.y - this.bird.radius <= 48) {
      this.bird.y = 48 + this.bird.radius;
      this.bird.vy = 0;
    }

    if (this.bird.y + this.bird.radius >= this.virtualHeight - 15) {
      this.gameOver();
      return;
    }

    // Pipe generation
    this.spawnTimer -= dt;
    if (this.spawnTimer <= 0) {
      this.spawnPipe();
      this.spawnTimer = this.pipeSpawnInterval;
    }

    // Update pipes
    const birdBox = {
      x: this.bird.x - this.bird.radius + 3,
      y: this.bird.y - this.bird.radius + 3,
      w: this.bird.radius * 2 - 6,
      h: this.bird.radius * 2 - 6
    };

    for (let i = this.pipes.length - 1; i >= 0; i--) {
      const p = this.pipes[i];
      p.x -= this.pipeSpeed * dt;

      // Score on passing pipe
      if (!p.passed && p.x + p.width < this.bird.x) {
        p.passed = true;
        this.score += 1;
        this.spawnParticles(this.bird.x + 20, this.bird.y, '#ffbe0b', 12);
      }

      // Collision check with top and bottom pipe
      const topPipeBox = { x: p.x, y: 0, w: p.width, h: p.topHeight };
      const bottomPipeBox = { x: p.x, y: p.topHeight + this.pipeGap, w: p.width, h: this.virtualHeight - (p.topHeight + this.pipeGap) };

      if (this.checkRectOverlap(birdBox, topPipeBox) || this.checkRectOverlap(birdBox, bottomPipeBox)) {
        this.gameOver();
        return;
      }

      // Remove offscreen pipes
      if (p.x + p.width < -50) {
        this.pipes.splice(i, 1);
      }
    }

    // Update particles
    for (let i = this.particles.length - 1; i >= 0; i--) {
      const pt = this.particles[i];
      pt.x += pt.vx * dt * 60;
      pt.y += pt.vy * dt * 60;
      pt.life -= dt;
      if (pt.life <= 0) {
        this.particles.splice(i, 1);
      }
    }
  }

  spawnPipe() {
    const minHeight = 80;
    const maxHeight = this.virtualHeight - this.pipeGap - minHeight - 60;
    const topHeight = Math.floor(Math.random() * (maxHeight - minHeight)) + minHeight;

    this.pipes.push({
      x: this.virtualWidth + 20,
      topHeight,
      width: 65,
      passed: false
    });
  }

  checkRectOverlap(r1, r2) {
    return !(r1.x + r1.w < r2.x ||
             r1.x > r2.x + r2.w ||
             r1.y + r1.h < r2.y ||
             r1.y > r2.y + r2.h);
  }

  spawnParticles(x, y, color, count = 8) {
    for (let i = 0; i < count; i++) {
      const angle = Math.random() * Math.PI * 2;
      const speed = Math.random() * 2.5 + 1;
      this.particles.push({
        x,
        y,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed,
        color,
        size: Math.random() * 3 + 1.5,
        life: 0.4
      });
    }
  }

  render(ctx) {
    // Clear canvas with deep space gradient
    const grad = ctx.createLinearGradient(0, 0, 0, this.virtualHeight);
    grad.addColorStop(0, '#060a12');
    grad.addColorStop(1, '#0e1726');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, this.virtualWidth, this.virtualHeight);

    // Subtle background distant grid
    ctx.strokeStyle = 'rgba(0, 229, 255, 0.04)';
    ctx.lineWidth = 1;
    for (let x = 0; x <= this.virtualWidth; x += 40) {
      ctx.beginPath();
      ctx.moveTo(x, 48);
      ctx.lineTo(x, this.virtualHeight);
      ctx.stroke();
    }

    // Render Pipes
    for (const p of this.pipes) {
      ctx.save();
      ctx.shadowColor = 'rgba(157, 78, 221, 0.4)';
      ctx.shadowBlur = 10;
      ctx.fillStyle = '#1c2438';
      ctx.strokeStyle = '#9d4edd';
      ctx.lineWidth = 2;

      // Top pipe
      ctx.fillRect(p.x, 0, p.width, p.topHeight);
      ctx.strokeRect(p.x, 0, p.width, p.topHeight);
      // Top pipe lip
      ctx.fillStyle = '#26344f';
      ctx.fillRect(p.x - 4, p.topHeight - 20, p.width + 8, 20);
      ctx.strokeRect(p.x - 4, p.topHeight - 20, p.width + 8, 20);

      // Bottom pipe
      const bottomY = p.topHeight + this.pipeGap;
      const bottomH = this.virtualHeight - bottomY;
      ctx.fillStyle = '#1c2438';
      ctx.fillRect(p.x, bottomY, p.width, bottomH);
      ctx.strokeRect(p.x, bottomY, p.width, bottomH);
      // Bottom pipe lip
      ctx.fillStyle = '#26344f';
      ctx.fillRect(p.x - 4, bottomY, p.width + 8, 20);
      ctx.strokeRect(p.x - 4, bottomY, p.width + 8, 20);

      ctx.restore();
    }

    // Render Particles
    for (const pt of this.particles) {
      ctx.save();
      ctx.globalAlpha = pt.life / 0.4;
      ctx.fillStyle = pt.color;
      ctx.beginPath();
      ctx.arc(pt.x, pt.y, pt.size, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }

    // Render Bird
    ctx.save();
    ctx.translate(this.bird.x, this.bird.y);
    ctx.rotate(this.bird.rotation);

    // Glow
    ctx.shadowColor = '#00e5ff';
    ctx.shadowBlur = 16;

    // Body
    ctx.fillStyle = '#00e5ff';
    ctx.beginPath();
    ctx.arc(0, 0, this.bird.radius, 0, Math.PI * 2);
    ctx.fill();

    // Wing
    ctx.fillStyle = '#33ebff';
    ctx.beginPath();
    ctx.ellipse(-6, 2, 7, 4, Math.PI / 6, 0, Math.PI * 2);
    ctx.fill();

    // Eye
    ctx.fillStyle = '#07090e';
    ctx.beginPath();
    ctx.arc(6, -4, 3.5, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = '#ffffff';
    ctx.beginPath();
    ctx.arc(7, -5, 1.2, 0, Math.PI * 2);
    ctx.fill();

    // Beak
    ctx.fillStyle = '#ffbe0b';
    ctx.beginPath();
    ctx.moveTo(12, -2);
    ctx.lineTo(20, 2);
    ctx.lineTo(12, 6);
    ctx.closePath();
    ctx.fill();

    ctx.restore();

    // Ground line
    ctx.fillStyle = '#121826';
    ctx.fillRect(0, this.virtualHeight - 12, this.virtualWidth, 12);
    ctx.strokeStyle = '#00e5ff';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(0, this.virtualHeight - 12);
    ctx.lineTo(this.virtualWidth, this.virtualHeight - 12);
    ctx.stroke();
  }
}
