/**
 * GameHub - Snake Game
 * Classic arcade snake with smooth neon visuals, golden bonus apples, particle bursts, and speed scaling.
 */

import { GameBase, GameState } from '../engine/GameBase.js';

export class SnakeGame extends GameBase {
  constructor(canvasId = 'game-canvas') {
    super(canvasId, 'snake', 800, 600);

    this.gridSize = 20; // 20px per cell -> 40 columns x 30 rows
    this.cols = this.virtualWidth / this.gridSize;
    this.rows = this.virtualHeight / this.gridSize;

    this.snake = [];
    this.direction = { x: 1, y: 0 };
    this.nextDirection = { x: 1, y: 0 };

    this.food = { x: 10, y: 10 };
    this.goldenFood = null;
    this.goldenTimer = 0;

    this.moveTimer = 0;
    this.baseMoveInterval = 0.12; // seconds per grid step
    this.moveInterval = this.baseMoveInterval;

    this.particles = [];
  }

  init() {
    this.reset();
  }

  reset() {
    this.score = 0;
    this.level = 1;
    this.moveInterval = this.baseMoveInterval;
    this.moveTimer = 0;
    this.goldenFood = null;
    this.goldenTimer = 0;
    this.particles = [];

    const startX = 12;
    const startY = 15;
    this.snake = [
      { x: startX, y: startY },
      { x: startX - 1, y: startY },
      { x: startX - 2, y: startY },
      { x: startX - 3, y: startY }
    ];

    this.direction = { x: 1, y: 0 };
    this.nextDirection = { x: 1, y: 0 };

    this.spawnFood();
  }

  spawnFood() {
    let valid = false;
    let newX, newY;

    while (!valid) {
      newX = Math.floor(Math.random() * (this.cols - 2)) + 1;
      newY = Math.floor(Math.random() * (this.rows - 5)) + 3; // Keep clear of top HUD

      valid = !this.snake.some(segment => segment.x === newX && segment.y === newY);
    }

    this.food = { x: newX, y: newY };

    // 25% chance to spawn a bonus Golden Apple if not currently active
    if (!this.goldenFood && Math.random() < 0.25 && this.score >= 30) {
      this.spawnGoldenFood();
    }
  }

  spawnGoldenFood() {
    let valid = false;
    let newX, newY;

    while (!valid) {
      newX = Math.floor(Math.random() * (this.cols - 2)) + 1;
      newY = Math.floor(Math.random() * (this.rows - 5)) + 3;

      valid = !this.snake.some(segment => segment.x === newX && segment.y === newY) &&
              !(this.food.x === newX && this.food.y === newY);
    }

    this.goldenFood = { x: newX, y: newY };
    this.goldenTimer = 8.0; // 8 seconds duration
  }

  handleInput() {
    const input = this.input;

    if (this.state === GameState.MENU) {
      if (input.isJustPressed('Space') || input.isJustPressed('Enter')) {
        this.play();
        return;
      }
    }

    if (this.state !== GameState.PLAYING) return;

    if ((input.isDown('ArrowUp') || input.isDown('KeyW')) && this.direction.y === 0) {
      this.nextDirection = { x: 0, y: -1 };
    } else if ((input.isDown('ArrowDown') || input.isDown('KeyS')) && this.direction.y === 0) {
      this.nextDirection = { x: 0, y: 1 };
    } else if ((input.isDown('ArrowLeft') || input.isDown('KeyA')) && this.direction.x === 0) {
      this.nextDirection = { x: -1, y: 0 };
    } else if ((input.isDown('ArrowRight') || input.isDown('KeyD')) && this.direction.x === 0) {
      this.nextDirection = { x: 1, y: 0 };
    }
  }

  update(dt) {
    this.handleInput();

    // Update golden apple timer
    if (this.goldenFood) {
      this.goldenTimer -= dt;
      if (this.goldenTimer <= 0) {
        this.goldenFood = null;
      }
    }

    // Update particles
    for (let i = this.particles.length - 1; i >= 0; i--) {
      const p = this.particles[i];
      p.x += p.vx * dt * 60;
      p.y += p.vy * dt * 60;
      p.life -= dt;
      if (p.life <= 0) {
        this.particles.splice(i, 1);
      }
    }

    // Fixed-step snake grid movement
    this.moveTimer += dt;
    if (this.moveTimer >= this.moveInterval) {
      this.moveTimer = 0;
      this.step();
    }
  }

  step() {
    this.direction = { ...this.nextDirection };

    const head = { ...this.snake[0] };
    head.x += this.direction.x;
    head.y += this.direction.y;

    // Wall collision check
    if (head.x < 0 || head.x >= this.cols || head.y < 3 || head.y >= this.rows) {
      this.gameOver();
      return;
    }

    // Self collision check
    for (let i = 0; i < this.snake.length; i++) {
      if (this.snake[i].x === head.x && this.snake[i].y === head.y) {
        this.gameOver();
        return;
      }
    }

    this.snake.unshift(head);

    // Food collision
    if (head.x === this.food.x && head.y === this.food.y) {
      this.score += 10;
      this.spawnParticles(this.food.x * this.gridSize + 10, this.food.y * this.gridSize + 10, '#00ff88');
      this.spawnFood();

      // Progressive speed increase: smoothly decreases step interval
      this.moveInterval = Math.max(0.055, this.baseMoveInterval - (this.score / 1500));
    } else if (this.goldenFood && head.x === this.goldenFood.x && head.y === this.goldenFood.y) {
      this.score += 30;
      this.spawnParticles(this.goldenFood.x * this.gridSize + 10, this.goldenFood.y * this.gridSize + 10, '#ffbe0b');
      this.goldenFood = null;
    } else {
      this.snake.pop(); // Remove tail segment when no food was eaten
    }
  }

  spawnParticles(x, y, color) {
    for (let i = 0; i < 16; i++) {
      const angle = Math.random() * Math.PI * 2;
      const speed = Math.random() * 3 + 1;
      this.particles.push({
        x,
        y,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed,
        color,
        size: Math.random() * 3 + 2,
        life: 0.45
      });
    }
  }

  render(ctx) {
    // Clear Canvas
    ctx.fillStyle = '#07090e';
    ctx.fillRect(0, 0, this.virtualWidth, this.virtualHeight);

    // Subtle background grid
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.025)';
    ctx.lineWidth = 1;
    for (let x = 0; x <= this.virtualWidth; x += this.gridSize) {
      ctx.beginPath();
      ctx.moveTo(x, 48);
      ctx.lineTo(x, this.virtualHeight);
      ctx.stroke();
    }
    for (let y = 48; y <= this.virtualHeight; y += this.gridSize) {
      ctx.beginPath();
      ctx.moveTo(0, y);
      ctx.lineTo(this.virtualWidth, y);
      ctx.stroke();
    }

    // Render Food (Apple with glow)
    const fx = this.food.x * this.gridSize;
    const fy = this.food.y * this.gridSize;
    ctx.save();
    ctx.shadowColor = '#00ff88';
    ctx.shadowBlur = 12;
    ctx.fillStyle = '#00ff88';
    ctx.beginPath();
    ctx.arc(fx + this.gridSize / 2, fy + this.gridSize / 2, this.gridSize / 2 - 2, 0, Math.PI * 2);
    ctx.fill();
    ctx.restore();

    // Render Golden Food if active
    if (this.goldenFood) {
      const gx = this.goldenFood.x * this.gridSize;
      const gy = this.goldenFood.y * this.gridSize;
      ctx.save();
      ctx.shadowColor = '#ffbe0b';
      ctx.shadowBlur = 18;
      ctx.fillStyle = '#ffbe0b';
      ctx.beginPath();
      ctx.arc(gx + this.gridSize / 2, gy + this.gridSize / 2, this.gridSize / 2 - 1, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }

    // Render Particles
    for (const p of this.particles) {
      ctx.save();
      ctx.globalAlpha = p.life / 0.45;
      ctx.fillStyle = p.color;
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }

    // Render Snake Body
    for (let i = this.snake.length - 1; i >= 0; i--) {
      const seg = this.snake[i];
      const sx = seg.x * this.gridSize;
      const sy = seg.y * this.gridSize;

      if (i === 0) {
        // Head
        ctx.save();
        ctx.shadowColor = '#00e5ff';
        ctx.shadowBlur = 15;
        ctx.fillStyle = '#00e5ff';
        ctx.beginPath();
        ctx.roundRect(sx + 1, sy + 1, this.gridSize - 2, this.gridSize - 2, 6);
        ctx.fill();

        // Eyes
        ctx.fillStyle = '#07090e';
        const eyeOffset = 5;
        let eye1 = { x: sx + eyeOffset, y: sy + eyeOffset };
        let eye2 = { x: sx + this.gridSize - eyeOffset, y: sy + eyeOffset };

        if (this.direction.x === 1) {
          eye1 = { x: sx + this.gridSize - 6, y: sy + 5 };
          eye2 = { x: sx + this.gridSize - 6, y: sy + this.gridSize - 5 };
        } else if (this.direction.x === -1) {
          eye1 = { x: sx + 6, y: sy + 5 };
          eye2 = { x: sx + 6, y: sy + this.gridSize - 5 };
        } else if (this.direction.y === 1) {
          eye1 = { x: sx + 5, y: sy + this.gridSize - 6 };
          eye2 = { x: sx + this.gridSize - 5, y: sy + this.gridSize - 6 };
        }

        ctx.beginPath();
        ctx.arc(eye1.x, eye1.y, 2, 0, Math.PI * 2);
        ctx.arc(eye2.x, eye2.y, 2, 0, Math.PI * 2);
        ctx.fill();
        ctx.restore();
      } else {
        // Body segment
        const alpha = Math.max(0.35, 1 - (i / this.snake.length) * 0.65);
        ctx.save();
        ctx.fillStyle = `rgba(0, 229, 255, ${alpha})`;
        ctx.beginPath();
        ctx.roundRect(sx + 2, sy + 2, this.gridSize - 4, this.gridSize - 4, 4);
        ctx.fill();
        ctx.restore();
      }
    }
  }
}
